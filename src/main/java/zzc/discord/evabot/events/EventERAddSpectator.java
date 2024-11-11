package zzc.discord.evabot.events;


import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.Bot;
import zzc.discord.evabot.MessageLog;
import zzc.discord.evabot.Scrim;

/**
 * 
 * @author Lesys
 *
 * Class of EventER that changes the name of the captain of the Team
 */
public class EventERAddSpectator extends EventER {
	/**
	 * Constructor of EventERAddSpectator
	 */
	public EventERAddSpectator() {
		this.commandName += "addSpectator";
	}
	
	/**
	 * Check if the	messages contains a mention, and put this User as a captain of the Team if it exists
	 */
	@Override
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		Bot.deserializeScrims();

		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();
		
		String[] message = event.getMessage().getContentRaw().split("(?i)".concat((Arrays.asList("+" , "*" , "?" , "^" , "$" , "(" , ")" , "[" , "]" , "{" , "}" , "|" , "\\").contains(this.commandName.substring(0, 1)) ? "\\" : "") + this.commandName + " "))[1].split(" ");

		String spectator = event.getMessage().getMentions().getMembers().size() == 1 ? event.getMessage().getMentions().getMembers().get(0).getUser().getName() : message[message.length - 1];

		Scrim scrim = Bot.getScrim(event);
		
		if (scrim == null) {
			scrim = new Scrim(event.getGuild().getName(), event.getChannel().getName());
			Bot.scrims.add(scrim);
		}
		
		if (!scrim.getSpectators().contains(spectator)) {
			if (EventERManager.hasPermission(event)) {
				scrim.addSpectators(spectator);

				Bot.getScrim(event).addLogs(new MessageLog(event.getMessage()));
				Bot.serializeScrims();
				
				event.getMessage().addReaction(Emoji.fromUnicode("U+2705")).queue();
			} else {
				event.getChannel().sendMessage(event.getAuthor().getAsMention() + " does not have the rights to use this command. Only the captain of the team can use it.").queue();
			}
		} else {
			event.getChannel().sendMessage(spectator + " is already in the spectator list for this scrim.").queue();
		}

		event.getMessage().removeReaction(Emoji.fromUnicode("U+1F504")).queue();
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " @{SpectatorDiscordTag} - Adds the tagged user to the spectator list (will get the role when the role give command will be used).\n";
	}
}
