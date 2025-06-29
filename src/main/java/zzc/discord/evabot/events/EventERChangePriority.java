package zzc.discord.evabot.events;


import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.Bot;
import zzc.discord.evabot.MessageLog;
import zzc.discord.evabot.Priority;
import zzc.discord.evabot.Team;
import zzc.discord.evabot.util.UtilEmpty;

/**
 * 
 * @author Lesys
 *
 * Class of EventER that changes the priority of the registered Team
 */
public class EventERChangePriority extends EventER {
	/**
	 * Constructor of EventERChangeCaptain
	 */
	public EventERChangePriority() {
		this.commandName += "changePriority";
	}
	
	/**
	 * Check if the	messages contains a mention, and put this User as a captain of the Team if it exists
	 */
	@Override
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		Bot.deserializeScrims();

		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();
		
		String[] message = this.getMessageArray(event);

		String teamName = "";
		for (int i = 0; i < message.length - 1; i++)
			teamName += message[i] + (i < message.length - 2 ? " " : "");

		String newPriority = message[message.length - 1];

		Team team = Bot.getTeam(event, teamName);
		if (team != null) {
			if (!UtilEmpty.isEmptyOrNull(newPriority)) {			
				System.err.println("New priority for " + team.getName() + ": " + newPriority);
				
				if (team.getPriority() == null || !Priority.equals(team.getPriority(), newPriority)) {
					if (EventERManager.hasPermission(event, teamName)) {
						team.setPriority(Priority.getPriorityByName(newPriority));
	
						Bot.getScrim(event).addLogs(new MessageLog(event.getMessage()));
						Bot.serializeScrims();
						
						event.getMessage().addReaction(Emoji.fromUnicode("U+2705")).queue();
					} else {
						event.getChannel().sendMessage(event.getAuthor().getAsMention() + " does not have the rights to use this command. Only the captain of the team can use it.").queue();
					}
				} else {
					event.getChannel().sendMessage(newPriority + " is already the current priority for the team.").queue();
				}
			} else {
				event.getChannel().sendMessage("Please choose a priority between the choices to put at the end of the command line (" + Priority.valuesToString() + ").").queue();
			}
		} else {
			event.getChannel().sendMessage(teamName + " hasn't been registered in this scrim.").queue();
		}

		event.getMessage().removeReaction(Emoji.fromUnicode("U+1F504")).queue();
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " {TeamName} {NewPriority} - Puts the new priority (" + Priority.valuesToString() + ") for of the registered team.\n";
	}
}
