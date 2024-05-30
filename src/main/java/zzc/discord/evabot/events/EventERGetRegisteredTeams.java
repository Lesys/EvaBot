package zzc.discord.evabot.events;


import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.Bot;
import zzc.discord.evabot.ERPlayer;
import zzc.discord.evabot.Team;

/**
 * 
 * @author Lesys
 *
 * Class of EventER when the user wants to get the registered teams for a scrim 
 */
public class EventERGetRegisteredTeams extends EventER {
	/**
	 * Constructor of EventGuildMessageGetRegisteredTeams
	 */
	public EventERGetRegisteredTeams() {
		this.commandName += "registeredTeams";
	}
	
	/**
	 * Gets all the teams from the serialized variable, updates their MMR and sort them by MMR if the option was added in the command line
	 */
	@Override
	public void exeuteCommand(@NotNull MessageReceivedEvent event) {
		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();
		Bot.deserializeScrims();

        final StringBuilder builder = new StringBuilder();
        builder.append("Registered teams for the scrim \"" + event.getChannel().getName() + "\":\n");
		
        boolean byMmr = event.getMessage().getContentRaw().split(" ").length > 1 && event.getMessage().getContentRaw().split(" ")[1].equalsIgnoreCase("byMmr");
        System.err.println("ByMmr ? " + byMmr);

		List<Team> teams = Bot.getScrim(event);
		if (teams != null) {
			teams.stream().forEach(team -> {
				team.updateMmr();
				if (!byMmr) {
					EventERGetRegisteredTeams.teamStringBuilder(builder, team);
				}
			});
			
			if (byMmr)
				teams.stream().sorted(Comparator.reverseOrder()).forEach(team -> {
					EventERGetRegisteredTeams.teamStringBuilder(builder, team);
				});
	
			Bot.serializeScrims();
			
			event.getChannel().sendMessage(builder).queue();
		} else {			
			event.getChannel().sendMessage("No teams has yet to be registered for the scrim \"" + event.getChannel().getName() + "\".").queue();
		}
		
		event.getMessage().removeReaction(Emoji.fromUnicode("U+1F504")).queue();
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " [byMmr] - Returns all the teams registered with their average MMR and players registered in the team with their own MMR. Using the option \"byMmr\" orders teams by MMR average, else returns by registration order.\n";
	}
	
	/**
	 * Protected method to construct the string to send
	 * @param builder	The string builder that is going to be displayed in the message
	 * @param team		The Team for which you want to create the string
	 */
	protected static void teamStringBuilder(final StringBuilder builder, Team team) {
		AtomicInteger placement = new AtomicInteger(1);
		builder.append("\n" + placement.getAndIncrement() + "°) **__" + team.getName() + "__** (" + team.getAverage() + "):\n");
		team.getPlayers().stream().forEach(player -> builder.append((team.getCaptain().equalsIgnoreCase(player.getName()) ? "__" : "") + player.getName() + (team.getCaptain().equalsIgnoreCase(player.getName()) ? "__" : "") + " (" + player.getDak().split("/")[player.getDak().split("/").length - 1] + " - " + player.getMmr() + "); "));
		ERPlayer sub = team.getSub();
		if (sub != null) {
			builder.append("[Sub: " + (team.getCaptain().equalsIgnoreCase(sub.getName()) ? "__" : "") + sub.getName() + (team.getCaptain().equalsIgnoreCase(sub.getName()) ? "__" : "") + " (" + sub.getDak().split("/")[sub.getDak().split("/").length - 1] + " - " + sub.getMmr() + ")]");
		}
	}
}
