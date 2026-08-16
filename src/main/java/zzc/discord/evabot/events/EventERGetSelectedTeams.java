package zzc.discord.evabot.events;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.dto.ERPlayerDTO;
import zzc.discord.evabot.dto.MessageLogDTO;
import zzc.discord.evabot.dto.ScrimDTO;
import zzc.discord.evabot.dto.SpectatorDTO;
import zzc.discord.evabot.dto.TeamDTO;
import zzc.discord.evabot.service.ScrimService;
import zzc.discord.evabot.util.UtilERPlayer;
import zzc.discord.evabot.util.UtilEmpty;
import zzc.discord.evabot.util.enumeration.Priority;

/**
 * 
 * @author Lesys
 *
 *         Class of EventER when the user wants to get the selected teams for a
 *         scrim
 */
@Service
public class EventERGetSelectedTeams extends EventER {
	protected boolean byMmr;

	private final transient ScrimService scrimService;
	
	/**
	 * Constructor of EventERGetSelectedTeams
	 */
	public EventERGetSelectedTeams(ScrimService scrimService) {
		this.commandName += "selectedTeams";

		this.scrimService = scrimService;
	}
	
	/**
	 * Gets all the teams from the serialized variable and sort them by MMR if the option was added in the command line. Changes the roles if a role has been mentioned
	 */
	@Override
	@Transactional
	public void executeCommand(@NotNull MessageReceivedEvent event) {		
		final StringBuilder builder = new StringBuilder();
		builder.append("Registered teams for the scrim \"" + event.getChannel().getName() + "\":\n");
		
		String[] message = event.getMessage().getContentRaw().trim().replaceAll(" +", " ").split(" ");
		
		this.byMmr = message.length > 1 && message[1].equalsIgnoreCase("byMmr");
		
		String roleName = "";
		
		if (!byMmr && message.length > 1) {
			roleName += message[1];
		}
		
		for (int i = 2; i < message.length; i++)
			roleName += (roleName != "" ? " " : "") + message[i];
		
		ScrimDTO scrim = this.scrimService.getByEvent(event);
		/*System.err.println("Members: ");
		event.getGuild().getMembers().forEach(t-> System.err.println(t.getUser().getName()));*/
		if (scrim != null && !UtilEmpty.isEmptyOrNull(scrim.getTeamList())) {
			if (EventERManager.hasPermissionAdminOrHelper(event)) {
				List<TeamDTO> filtered = this.filterTeams(scrim);
				
				this.createAndSendMessage(filtered, roleName, builder, scrim, event);
				
				this.commandIsSuccessful();
			} else {
				event.getChannel().sendMessage("Only an Administrator can use this command.").queue();				
			}
		} else {			
			event.getChannel().sendMessage("No teams has yet to be registered for the scrim \"" + event.getChannel().getName() + "\".").queue();
		}
	}
	
	@Override
	public String helpCommand() {
		return super.helpCommand()
				+ " [byMmr] [RolesMention] - Returns all the teams registered with their average MMR and players registered in the team with their own MMR. Using the option \"byMmr\" orders teams by MMR average, else returns by registration order. If a role is mentioned (or fully written at the end of the command), removes the roles to everyone not in the selected teams or spectators and add it to them instead (won't return the selected teams).\n";
	}
	
	protected List<TeamDTO> filterTeams(ScrimDTO scrim) {
		List<TeamDTO> filtered = null;
	
		if (!this.byMmr)
			filtered = scrim.getTeamList().stream().limit(8).toList();
		else
			filtered = scrim.getTeamList().stream().sorted().limit(8).toList();
		
		return filtered;
	}
	
	protected void createAndSendMessage(List<TeamDTO> filtered, String roleName, StringBuilder builder, ScrimDTO scrim, @NotNull MessageReceivedEvent event) {
		final List<String> messages = new ArrayList<String>();
		AtomicInteger placement = new AtomicInteger(1);
		
		// If a role is mentioned, manages the roles on the teams selected but does not display them; if no roles are mentioned, displays the selected teams
		if (roleName != "") {
			List<Role> roles = event.getGuild().getRolesByName(roleName, true);
			List<Member> toAdd = new ArrayList<Member>();
			
//			List<String> names = new ArrayList<String>();
//			filtered.stream().flatMap(team -> team.getFullPlayerList().stream()).forEach(p -> names.add(ERPlayer.getERPlayer(p).getDiscordName()));
			toAdd.addAll(filtered.stream().flatMap(team -> team.getFullPlayerList().stream())
					.map(player -> EventERGetSelectedTeams.getMemberFromGuild(event, player))
					.filter(Objects::nonNull).distinct().toList());
			toAdd.addAll(scrim.getSpectatorList().stream().map(spec -> EventERGetSelectedTeams.getMemberFromGuild(event, spec)).filter(Objects::nonNull).distinct().toList());
			
			List<Member> toRemove = new ArrayList<Member>();
			toRemove.addAll(event.getGuild().getMembersWithRoles(roles));
			
			List<Member> mutual = toAdd.stream().filter(m -> toRemove.contains(m)).collect(Collectors.toList());
			
			toRemove.removeAll(mutual);
			toAdd.removeAll(mutual);
			
			roles.forEach(r -> {
				toRemove.forEach(m -> event.getGuild().removeRoleFromMember(m, r).queue());
				toAdd.forEach(m -> event.getGuild().addRoleToMember(m, r).queue());
			});
			
			MessageLogDTO ml = new MessageLogDTO(event.getMessage());
			ml.addToMessage("\nRoles: " + roles.stream().map(r -> r.getName()).toList().toString() + "\nAdded: " + toAdd.stream().map(m -> m.getEffectiveName()).toList().toString() + "; Removed: " + toRemove.stream().map(m -> m.getEffectiveName()).toList().toString() + "; Kept: " + mutual.stream().map(m -> m.getEffectiveName()).toList().toString());
			scrim.getMessageLogList().add(ml);

			this.scrimService.save(scrim);
			
			event.getChannel().sendMessage("Roles have been assigned; " + toRemove.size() + " members lost their roles, " + toAdd.size() + " members have been added and " + mutual.size() + " kept their roles.").queue();
		} else {					
			filtered.stream().forEach(team -> {
				if (builder.length() > 0 && builder.length() >= 1800) {
					messages.add(builder.toString());
					builder.delete(0, builder.length());
				}
				EventERGetSelectedTeams.teamStringBuilder(builder, team, placement, this.byMmr, event);
			});
			
			messages.add(builder.toString());
	        messages.forEach(m -> sendMessageWait(event, m));
			
			//Bot.serializeScrims();
		}
	}
	
	/**
	 * Protected method to construct the string to send
	 * 
	 * @param builder   The string builder that is going to be displayed in the
	 *                  message
	 * @param team      The Team for which you want to create the string
	 * @param placement The integer corresponding to the place of the team
	 * @param event     The event sent to be able to mention people
	 */
	protected static void teamStringBuilder(final StringBuilder builder, TeamDTO team, AtomicInteger placement, boolean order, MessageReceivedEvent event) {
		builder.append("\n")
			.append(placement.getAndIncrement()).append("°) **__").append(team.getName()).append("__** (").append(team.getAverage()).append(")")
			.append((order && !Priority.NEUTRAL.equals(team.getPriority())) ? " **" + team.getPriority().toString() + " priority** " : "").append(":\n");
		team.getPlayerList().forEach(player -> {
				System.out.println("Player getSelectedTeams: " + player.getDak() + "; " + player.getDisplayName());

				// If player is sub, open bracket
				if (player.equals(team.getSub())) {
					builder.append("[Sub: ");
				}
				
				String underline = UtilERPlayer.hasSameDiscordName(team.getCaptain(), player) ? "__" : "";
				
				builder.append(underline);
				builder.append(EventERGetSelectedTeamsSnake.getMention(event, player));
				builder.append(underline);
				builder.append(" (").append(UtilERPlayer.getNameWithoutSpecialChar(player::getDakName)).append(" - ").append(player.getMmr()).append(")");

				// If player is sub, close bracket
				if (player.equals(team.getSub())) {
					builder.append("]");
				} else {
					builder.append("; ");
				}
		});
	}
	
	/**
	 * Returns the string corresponding to the mention of the member
	 * 
	 * @param event		The event sent to be able to mention people
	 * @param player 	The player to mention
	 * @return The mention (or a String if member is null)
	 */
	protected static String getMention(MessageReceivedEvent event, ERPlayerDTO player) {
		String mention = "";
		try {
			Member m = event.getGuild().getMembersByName(player.getDiscordName(), true).stream().findFirst().orElse(null);
			if (m != null) {
				mention = m.getAsMention();
			} else {
				mention = player.getDiscordName().replaceAll("[*_]", "");
			}
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
			mention = player.getDiscordName().replaceAll("[*_]", "");
		}
		return mention;
	}
	
	protected static Member getMemberFromGuild(MessageReceivedEvent event, SpectatorDTO spec) {
		Member m = null;
		try {
			m = event.getGuild().getMembersByName(spec.getName(), true).stream().findFirst().orElse(null);
			if (m == null) {
				m = null;
			}
		} catch (IllegalArgumentException e) {
			System.err.println("[getMemberFromGuild] for spectator " + spec.getName() + ": " + e.getMessage());
			m = null;
		}
		
		return m;
	}
	
	protected static Member getMemberFromGuild(MessageReceivedEvent event, ERPlayerDTO player) {
		Member m = null;
		try {
			m = event.getGuild().getMembersByName(player.getDiscordName(), true).stream().findFirst().orElse(null);
			if (m == null) {
				m = null;
			}
		} catch (IllegalArgumentException e) {
			System.err.println("[getMemberFromGuild] for player " + player.getDiscordName() + ": " + e.getMessage());
			m = null;
		}
		
		return m;
	}
	
	protected static Member getMemberFromGuild(MessageReceivedEvent event, String playerName) {
		Member m = null;
		try {
			m = event.getGuild().getMembersByName(playerName, true).stream().findFirst().orElse(null);
			if (m == null) {
				m = null;
			}
		} catch (IllegalArgumentException e) {
			System.err.println("[getMemberFromGuild] for name " + playerName + ": " + e.getMessage());
			m = null;
		}
		
		return m;
	}
}
