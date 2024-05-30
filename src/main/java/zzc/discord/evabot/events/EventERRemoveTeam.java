package zzc.discord.evabot.events;


import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.Bot;
import zzc.discord.evabot.Team;

/**
 * 
 * @author Lesys
 *
 * Class of EventER that removes a Team registered for a scrim
 */
public class EventERRemoveTeam extends EventER {
	/**
	 * Constructor of EventGuildMessageRemoveTeam
	 */
	public EventERRemoveTeam() {
		this.commandName += "removeTeam";
	}
	
	/**
	 * Check if the team exists in the scrim registration and removes it
	 */
	@Override
	public void exeuteCommand(@NotNull MessageReceivedEvent event) {
		Bot.deserializeScrims();
		
		String teamName = event.getMessage().getContentRaw().split("(?i)".concat((Arrays.asList("+" , "*" , "?" , "^" , "$" , "(" , ")" , "[" , "]" , "{" , "}" , "|" , "\\").contains(this.commandName.substring(0, 1)) ? "\\" : "") + this.commandName + " "))[1];

		Team team = Bot.getTeam(event, teamName);
		if (team != null) {
			if (EventERManager.hasPermission(event, teamName)) {
				if (Bot.getScrim(event).remove(team)) {
					Bot.serializeScrims();
					
					event.getMessage().addReaction(Emoji.fromUnicode("U+2705")).queue();
				} else {
					event.getChannel().sendMessage(teamName + " hasn't been removed from this scrim.").queue();
				}
			} else {
				event.getChannel().sendMessage(event.getAuthor().getAsMention() + " does not have the rights to use this command. Only the captain of the team can use it.").queue();
			}
		} else {
			event.getChannel().sendMessage(teamName + " hasn't been registered in this scrim.").queue();
		}
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " {TeamName} - Removes a team from the registered teams.\n";
	}
}
