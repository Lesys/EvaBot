package zzc.discord.evabot.events;


import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.Bot;
import zzc.discord.evabot.MessageLog;
import zzc.discord.evabot.Team;

/**
 * 
 * @author Lesys
 *
 * Class of EventER that changes the name of the captain of the Team
 */
public class EventERChangeCaptain extends EventER {
	/**
	 * Constructor of EventERChangeCaptain
	 */
	public EventERChangeCaptain() {
		this.commandName += "giveCaptain";
	}
	
	/**
	 * Check if the	messages contains a mention, and put this User as a captain of the Team if it exists
	 */
	@Override
	public void exeuteCommand(@NotNull MessageReceivedEvent event) {
		Bot.deserializeScrims();

		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();
		
		String[] message = event.getMessage().getContentRaw().split("(?i)".concat((Arrays.asList("+" , "*" , "?" , "^" , "$" , "(" , ")" , "[" , "]" , "{" , "}" , "|" , "\\").contains(this.commandName.substring(0, 1)) ? "\\" : "") + this.commandName + " "))[1].split(" ");

		String teamName = "";
		for (int i = 0; i < message.length - 1; i++)
			teamName += message[i] + (i < message.length - 2 ? " " : "");

		String newCaptain = event.getMessage().getMentions().getMembers().size() == 1 ? event.getMessage().getMentions().getMembers().get(0).getUser().getName() : message[message.length - 1];
			
		System.err.println("Captain name: " + newCaptain);

		Team team = Bot.getTeam(event, teamName);
		if (team != null) {			
			if (!team.getCaptain().equalsIgnoreCase(newCaptain)) {
				if (EventERManager.hasPermission(event, teamName)) {
					team.setCaptain(newCaptain);

					Bot.getScrim(event).addLogs(new MessageLog(event.getMessage()));
					Bot.serializeScrims();
					
					event.getMessage().addReaction(Emoji.fromUnicode("U+2705")).queue();
				} else {
					event.getChannel().sendMessage(event.getAuthor().getAsMention() + " does not have the rights to use this command. Only the captain of the team can use it.").queue();
				}
			} else {
				event.getChannel().sendMessage(newCaptain + " is already the captain of the team.").queue();
			}
		} else {
			event.getChannel().sendMessage(teamName + " hasn't been registered in this scrim.").queue();
		}/*
		} else {
			event.getChannel().sendMessage("Please put the mentionned user at the end of the command (see help command for more informations).").queue();
		}*/

		event.getMessage().removeReaction(Emoji.fromUnicode("U+1F504")).queue();
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " {TeamName} @{NewCaptain} - Give the captain role of the registered team to the mentionned user.\n";
	}
}
