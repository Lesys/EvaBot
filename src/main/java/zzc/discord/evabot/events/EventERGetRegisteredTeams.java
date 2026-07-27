package zzc.discord.evabot.events;


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.dto.ScrimDTO;
import zzc.discord.evabot.dto.TeamDTO;
import zzc.discord.evabot.service.ScrimService;
import zzc.discord.evabot.service.UpdateMmrService;
import zzc.discord.evabot.util.UtilERPlayer;
import zzc.discord.evabot.util.enumeration.Priority;

/**
 * 
 * @author Lesys
 *
 * Class of EventER when the user wants to get the registered teams for a scrim 
 */
@Service
public class EventERGetRegisteredTeams extends EventER {
	private final transient ScrimService scrimService;
	
	private final transient UpdateMmrService updateMmrService;
	
	/**
	 * Constructor of EventERGetRegisteredTeams
	 */
	public EventERGetRegisteredTeams(ScrimService scrimService, UpdateMmrService updateMmrService) {
		this.commandName += "registeredTeams";

		this.scrimService = scrimService;
		this.updateMmrService = updateMmrService;
	}
	
	/**
	 * Gets all the teams from the serialized variable, updates their MMR and sort them by MMR if the option was added in the command line
	 */
	@Override
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		final List<String> messages = new ArrayList<String>();

        final StringBuilder builder = new StringBuilder();
        builder.append("Registered teams for the scrim \"" + event.getChannel().getName() + "\":\n");
		
        boolean byMmr = event.getMessage().getContentRaw().trim().replaceAll(" +", " ").split(" ").length > 1 && event.getMessage().getContentRaw().trim().replaceAll(" +", " ").split(" ")[1].equalsIgnoreCase("byMmr");
//        System.err.println("ByMmr ? " + byMmr);

		ScrimDTO scrim = this.scrimService.getByEvent(event);
		
		if (scrim != null) {
			AtomicInteger placement = new AtomicInteger(1);
			List<TeamDTO> filtered = null;
			this.updateMmrService.updateTeamsMmr(scrim, false);
			
			if (!byMmr)
				filtered = scrim.getTeamList().stream().toList();
			else
				filtered = scrim.getTeamList().stream().sorted().toList();
			
			filtered.stream().forEach(team -> {
		    	if (builder.length() > 0 && builder.length() >= 1800) {
		    		messages.add(builder.toString());
		            builder.delete(0, builder.length());
		    	}
		    	EventERGetRegisteredTeams.teamStringBuilder(builder, team, placement, byMmr, event);
			});

			messages.add(builder.toString());
	        messages.forEach(m -> sendMessageWait(event, m));
			
			this.scrimService.save(scrim);
			
			this.commandIsSuccessful();
		} else {			
			event.getChannel().sendMessage("No teams has yet to be registered for the scrim \"" + event.getChannel().getName() + "\".").queue();
		}
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " [byMmr] - Returns all the teams registered with their average MMR and players registered in the team with their own MMR. Using the option \"byMmr\" orders teams by MMR average, else returns by registration order.\n";
	}
	
	/**
	 * Protected method to construct the string to send
	 * 
	 * @param builder   The string builder that is going to be displayed in the message
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
				builder.append(getMention(event, player));
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
}
