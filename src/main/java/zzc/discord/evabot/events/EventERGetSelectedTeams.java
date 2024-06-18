package zzc.discord.evabot.events;


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.Member;
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
 * Class of EventER when the user wants to get the selected teams for a scrim 
 */
public class EventERGetSelectedTeams extends EventER {
	/**
	 * Constructor of EventERGetSelectedTeams
	 */
	public EventERGetSelectedTeams() {
		this.commandName += "selectedTeams";
	}
	
	/**
	 * Gets all the teams from the serialized variable, updates their MMR and sort them by MMR if the option was added in the command line
	 */
	@Override
	public void exeuteCommand(@NotNull MessageReceivedEvent event) {
		final List<String> messages = new ArrayList<String>();
		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();
		Bot.deserializeScrims();

        final StringBuilder builder = new StringBuilder();
        builder.append("Registered teams for the scrim \"" + event.getChannel().getName() + "\":\n");
		
        boolean byMmr = event.getMessage().getContentRaw().split(" ").length > 1 && event.getMessage().getContentRaw().split(" ")[1].equalsIgnoreCase("byMmr");
        System.err.println("ByMmr ? " + byMmr);

		Scrim scrim = Bot.getScrim(event);
		if (scrim != null) {
			AtomicInteger placement = new AtomicInteger(1);
			if (EventERManager.hasPermission(event)) {
				List<Team> filtered = null;
				scrim.getTeams().stream().forEach(team -> team.updateMmr());
				if (!byMmr)
					filtered = scrim.getTeams().stream().limit(8).toList();
				else
					filtered = scrim.getTeams().stream().sorted(Comparator.reverseOrder()).limit(8).toList();
				
				filtered.stream().forEach(team -> {
			    	if (builder.length() > 0 && builder.length() >= 1800) {
			    		messages.add(builder.toString());
			            builder.delete(0, builder.length());
			    	}
			    	EventERGetSelectedTeams.teamStringBuilder(builder, team, placement, event);
				});
	
				messages.add(builder.toString());
		        messages.forEach(m -> event.getChannel().sendMessage(m).queue());
		        
				Bot.serializeScrims();
			} else {
				event.getChannel().sendMessage("Only an Administrator can use this command.").queue();				
			}
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
	 * @param placement	The integer corresponding to the place of the team
	 * @param event		The event sent to be able to mention people
	 */
	protected static void teamStringBuilder(final StringBuilder builder, Team team, AtomicInteger placement, MessageReceivedEvent event) {
		builder.append("\n" + placement.getAndIncrement() + "°) **__" + team.getName() + "__** (" + team.getAverage() + "):\n");
		team.getPlayers().stream().forEach(player -> builder.append((team.getCaptain().equalsIgnoreCase(player.getDiscordName()) ? "__" : "") + EventERGetSelectedTeams.getMention(event.getGuild().getMembersByName(player.getDiscordName(), true).stream().findFirst().orElse(null)) + (team.getCaptain().equalsIgnoreCase(player.getDiscordName()) ? "__" : "") + " (" + player.getDakName() + " - " + player.getMmr() + "); "));
		ERPlayer sub = ERPlayer.getERPlayerByDiscordName(team.getSub());
		if (sub != null) {
			builder.append("[Sub: " + (team.getCaptain().equalsIgnoreCase(sub.getDiscordName()) ? "__" : "") + EventERGetSelectedTeams.getMention(event.getGuild().getMembersByName(sub.getDiscordName(), true).stream().findFirst().orElse(null)) + (team.getCaptain().equalsIgnoreCase(sub.getDiscordName()) ? "__" : "") + " (" + sub.getDakName() + " - " + sub.getMmr() + ")]");
		}
	}
	
	/**
	 * Returns the string corresponding to the mention of the member
	 * @param member	The member to mention
	 * @return			The mention (or a String if member is null)
	 */
	protected static String getMention(Member member) {
		return member != null ? member.getAsMention() : "N/A";
	}
}
