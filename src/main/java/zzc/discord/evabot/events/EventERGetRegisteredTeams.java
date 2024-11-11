package zzc.discord.evabot.events;


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.Bot;
import zzc.discord.evabot.ERPlayer;
import zzc.discord.evabot.Scrim;
import zzc.discord.evabot.Team;

/**
 * 
 * @author Lesys
 *
 * Class of EventER when the user wants to get the registered teams for a scrim 
 */
public class EventERGetRegisteredTeams extends EventER {
	/**
	 * Constructor of EventERGetRegisteredTeams
	 */
	public EventERGetRegisteredTeams() {
		this.commandName += "registeredTeams";
	}
	
	/**
	 * Gets all the teams from the serialized variable, updates their MMR and sort them by MMR if the option was added in the command line
	 */
	@Override
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		final List<String> messages = new ArrayList<String>();
		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();
		Bot.deserializeScrims();

        final StringBuilder builder = new StringBuilder();
        builder.append("Registered teams for the scrim \"" + event.getChannel().getName() + "\":\n");
		
        boolean byMmr = event.getMessage().getContentRaw().trim().replaceAll(" +", " ").split(" ").length > 1 && event.getMessage().getContentRaw().trim().replaceAll(" +", " ").split(" ")[1].equalsIgnoreCase("byMmr");
        System.err.println("ByMmr ? " + byMmr);

		Scrim scrim = Bot.getScrim(event);
		if (scrim != null) {
			AtomicInteger placement = new AtomicInteger(1);
			List<Team> filtered = null;
			scrim.getTeams().forEach(team -> team.updateMmr());
			
			if (!byMmr)
				filtered = scrim.getTeams().stream().toList();
			else
				filtered = scrim.getTeams().stream().sorted(Comparator.reverseOrder()).toList();
			
			filtered.stream().forEach(team -> {
		    	if (builder.length() > 0 && builder.length() >= 1800) {
		    		messages.add(builder.toString());
		            builder.delete(0, builder.length());
		    	}
		    	EventERGetRegisteredTeams.teamStringBuilder(builder, team, placement, event, byMmr);
			});

			messages.add(builder.toString());
	        messages.forEach(m -> this.sendMessageWait(event, m));
		} else {			
			event.getChannel().sendMessage("No teams has yet to be registered for the scrim \"" + event.getChannel().getName() + "\".").queue();
		}
		
		this.removeReaction(event, "U+1F504");
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " [byMmr] - Returns all the teams registered with their average MMR and players registered in the team with their own MMR. Using the option \"byMmr\" orders teams by MMR average, else returns by registration order.\n";
	}
	
	/**
	 * Protected method to construct the string to send
	 * @param builder	The string builder that is going to be displayed in the message
	 * @param team		The Team for which you want to create the string
	 * @param placement	The integer corresponding to the place of the team
	 * @param event		The event sent to be able to mention people
	 */
	protected static void teamStringBuilder(final StringBuilder builder, Team team, AtomicInteger placement, MessageReceivedEvent event, boolean showMmr) {
		builder.append("\n" + placement.getAndIncrement() + "°) **__" + team.getName() + "__**"  + (showMmr ? "(" + team.getAverage() + ")" : "") + " :\n");
		team.getPlayerNames().stream().map(p -> ERPlayer.getERPlayer(p)).forEach(player -> builder.append((team.getCaptain().equalsIgnoreCase(player.getDiscordName()) ? "__" : "") + (player.getDisplayName().isEmpty() ? ERPlayer.getNameWithoutSpecialChar(player::getDakName) : ERPlayer.getNameWithoutSpecialChar(player::getDisplayName)) + (team.getCaptain().equalsIgnoreCase(player.getDiscordName()) ? "__" : "") + (showMmr ? " (" + player.getMmr() + ")" : "") + " "));
		ERPlayer sub = ERPlayer.getERPlayer(team.getSub());
		if (sub != null) {
			builder.append("[Sub: " + (team.getCaptain().equalsIgnoreCase(sub.getDiscordName()) ? "__" : "") + (sub.getDisplayName().isEmpty() ? ERPlayer.getNameWithoutSpecialChar(sub::getDakName) : ERPlayer.getNameWithoutSpecialChar(sub::getDisplayName)) + (team.getCaptain().equalsIgnoreCase(sub.getDiscordName()) ? "__" : "") + (showMmr ?  " (" + sub.getMmr() + ")" : "") + "]");
		}
		builder.append("\n");
	}
}
