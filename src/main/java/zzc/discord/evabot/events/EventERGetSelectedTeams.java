package zzc.discord.evabot.events;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.Bot;
import zzc.discord.evabot.ERPlayer;
import zzc.discord.evabot.MessageLog;
import zzc.discord.evabot.Priority;
import zzc.discord.evabot.Scrim;
import zzc.discord.evabot.Team;
import zzc.discord.evabot.util.UtilEmpty;

/**
 * 
 * @author Lesys
 *
 *         Class of EventER when the user wants to get the selected teams for a
 *         scrim
 */
public class EventERGetSelectedTeams extends EventER {
	protected boolean byMmr;
	/**
	 * Constructor of EventERGetSelectedTeams
	 */
	public EventERGetSelectedTeams() {
		this.commandName += "selectedTeams";
	}
	
	/**
	 * Gets all the teams from the serialized variable and sort them by MMR if the option was added in the command line. Changes the roles if a role has been mentioned
	 */
	@Override
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();
		Bot.deserializeScrims();
		
		final StringBuilder builder = new StringBuilder();
		builder.append("Registered teams for the scrim \"" + event.getChannel().getName() + "\":\n");
		
		String[] message = event.getMessage().getContentRaw().trim().replaceAll(" +", " ").split(" ");
		
		this.byMmr = message.length > 1 && message[1].equalsIgnoreCase("byMmr");
		System.err.println("ByMmr ? " + byMmr);
		
		String roleName = "";
		
		if (!byMmr && message.length > 1) {
			roleName += message[1];
		}
		
		for (int i = 2; i < message.length; i++)
			roleName += (roleName != "" ? " " : "") + message[i];
		
		Scrim scrim = Bot.getScrim(event);
		/*System.err.println("Members: ");
		event.getGuild().getMembers().forEach(t-> System.err.println(t.getUser().getName()));*/
		if (scrim != null && !UtilEmpty.isEmptyOrNull(scrim.getTeams())) {
			if (EventERManager.hasPermission(event)) {
				List<Team> filtered = this.filterTeams(scrim);
				
				this.createAndSendMessage(filtered, roleName, builder, scrim, event);
			} else {
				event.getChannel().sendMessage("Only an Administrator can use this command.").queue();				
			}
		} else {			
			event.getChannel().sendMessage("No teams has yet to be registered for the scrim \"" + event.getChannel().getName() + "\".").queue();
		}
		
		event.getMessage().removeReaction(Emoji.fromUnicode("U+1F504")).queueAfter(2, TimeUnit.SECONDS);
	}
	
	@Override
	public String helpCommand() {
		return super.helpCommand()
				+ " [byMmr] [RolesMention] - Returns all the teams registered with their average MMR and players registered in the team with their own MMR. Using the option \"byMmr\" orders teams by MMR average, else returns by registration order. If a role is mentioned (or fully written at the end of the command), removes the roles to everyone not in the selected teams or spectators and add it to them instead (won't return the selected teams).\n";
	}
	
	protected List<Team> filterTeams(Scrim scrim) {
		List<Team> filtered = null;
	
		if (!this.byMmr)
			filtered = scrim.getTeams().stream().limit(8).toList();
		else
			filtered = scrim.getTeams().stream().sorted().limit(8).toList();
		
		return filtered;
	}
	
	protected void createAndSendMessage(List<Team> filtered, String roleName, StringBuilder builder, Scrim scrim, @NotNull MessageReceivedEvent event) {
		final List<String> messages = new ArrayList<String>();
		AtomicInteger placement = new AtomicInteger(1);
		
		// If a role is mentioned, manages the roles on the teams selected but does not display them; if no roles are mentioned, displays the selected teams
		if (roleName != "") {
			List<Role> roles = event.getGuild().getRolesByName(roleName, true);
			List<Member> toAdd = new ArrayList<Member>();
			
			List<String> names = new ArrayList<String>();
			filtered.stream().flatMap(team -> team.getPlayerNames().stream()).forEach(p -> names.add(ERPlayer.getERPlayer(p).getDiscordName()));
			toAdd.addAll(filtered.stream()
					.flatMap(team -> team.getSub() != null ? Stream.concat(team.getPlayerNames().stream().map(p -> ERPlayer.getERPlayer(p)), Arrays.asList(ERPlayer.getERPlayer(team.getSub())).stream()) : team.getPlayerNames().stream().map(p -> ERPlayer.getERPlayer(p)))
					.map(player -> EventERGetSelectedTeams.getMemberFromGuild(event, player))
					.filter(Objects::nonNull).distinct().toList());
			toAdd.addAll(scrim.getSpectators().stream().map(spec -> EventERGetSelectedTeams.getMemberFromGuild(event, spec)).filter(Objects::nonNull).distinct().toList());
			List<Member> toRemove = new ArrayList<Member>();
			toRemove.addAll(event.getGuild().getMembersWithRoles(roles));
			List<Member> mutual = toAdd.stream().filter(m -> toRemove.contains(m)).collect(Collectors.toList());
			
			toRemove.removeAll(mutual);
			toAdd.removeAll(mutual);
			
			roles.forEach(r -> {
				toRemove.forEach(m -> event.getGuild().removeRoleFromMember(m, r).queue());
				toAdd.forEach(m -> event.getGuild().addRoleToMember(m, r).queue());
			});
			
			MessageLog ml = new MessageLog(event.getMessage());
			ml.addToMessage("\nRoles: " + roles.stream().map(r -> r.getName()).toList().toString() + "\nAdded: " + toAdd.stream().map(m -> m.getEffectiveName()).toList().toString() + "; Removed: " + toRemove.stream().map(m -> m.getEffectiveName()).toList().toString() + "; Kept: " + mutual.stream().map(m -> m.getEffectiveName()).toList().toString());
			scrim.addLogs(ml);
			
			Bot.serializeScrims();
			
			event.getChannel().sendMessage("Roles have been assigned; " + toRemove.size() + " members lost their roles removed, " + toAdd.size() + " members have been added and " + mutual.size() + " kept their roles.").queue();	
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
	protected static void teamStringBuilder(final StringBuilder builder, Team team, AtomicInteger placement, boolean order,
			MessageReceivedEvent event) {
		builder.append("\n" + placement.getAndIncrement() + "°) **__" + team.getName() + "__** (" + team.getAverage()
			+ ")" + (order && !Priority.NEUTRAL.equals(team.getPriority()) ? " **" + team.getPriority().toString() + " priority** " : "") + ":\n");
		team.getPlayerNames().stream().map(p -> ERPlayer.getERPlayer(p))
			.forEach(player -> {
				System.err.println("Player getselectedteams: " + player.getDak() + "; " + player.getDisplayName());
				builder.append((team.getCaptain().equalsIgnoreCase(player.getDiscordName()) ? "__" : "")
						+ EventERGetSelectedTeams.getMention(event, player)
						+ (team.getCaptain().equalsIgnoreCase(player.getDiscordName()) ? "__" : "")
						+ " (" + ERPlayer.getNameWithoutSpecialChar(player::getDakName) + " - " + player.getMmr() + "); ");
		});
		ERPlayer sub = ERPlayer.getERPlayer(team.getSub());
		if (sub != null) {
			builder.append("[Sub: " + (team.getCaptain().equalsIgnoreCase(sub.getDiscordName()) ? "__" : "")
					+ EventERGetSelectedTeams.getMention(event, sub)
					+ (team.getCaptain().equalsIgnoreCase(sub.getDiscordName()) ? "__" : "")
					+ " (" + ERPlayer.getNameWithoutSpecialChar(sub::getDakName) + " - " + sub.getMmr() + ")]");
		}
	}
	
	/**
	 * Returns the string corresponding to the mention of the member
	 * 
	 * @param event		The event sent to be able to mention people
	 * @param player 	The player to mention
	 * @return The mention (or a String if member is null)
	 */
	protected static String getMention(MessageReceivedEvent event, ERPlayer player) {
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
	
	protected static Member getMemberFromGuild(MessageReceivedEvent event, ERPlayer player) {
		Member m = null;
		try {
			m = event.getGuild().getMembersByName(player.getDiscordName(), true).stream().findFirst().orElse(null);
			if (m == null) {
				m = null;
			}
		} catch (IllegalArgumentException e) {
			System.err.println("getMemberFromGuild for player " + player.getDiscordName() + ": " + e.getMessage());
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
			System.err.println("getMemberFromGuild for name " + playerName + ": " + e.getMessage());
			m = null;
		}
		
		return m;
	}
}
