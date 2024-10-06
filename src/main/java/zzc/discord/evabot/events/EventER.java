package zzc.discord.evabot.events;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * 
 * @author Lesys
 *
 * Abstract class for all the Event linked to Eternal Return with JDA
 */
public abstract class EventER {
	/**
	 * Prefix of all the commands extending this class
	 */
	public static String commandPrefix = "!";
	
	/**
	 * The command name of the EventER. Makes it easier to compare if an event has to be executed based on the command name sent by the user.
	 * It is initialized with the prefix for all the commands.
	 */
	public String commandName = EventER.commandPrefix;
	
	/**
	 * Function to let the EventERManager know if the class is the one which needs to execute its command
	 * @param commandName	The name sent by the user
	 * @return				true if this class hsa the same command name, false if not
	 */
	public boolean matchingName(String commandName) {
		return this.commandName.equalsIgnoreCase(commandName);
	}

	/**
	 * 
	 * @return	This command name in bold for Discord message. Needs more explanation of each command in extended classes
	 */
	public String helpCommand() {
		return "**" + this.commandName + "**";
	}

	/**
	 * The execution body of the Event to do when the command is called
	 * @param event		The event received when the user sent a message
	 */
	public abstract void exeuteCommand(@NotNull MessageReceivedEvent event);

	/**
	 * Splits the command call from the rest of the message
	 * @param event		The event received when the user sent a message
	 * @return			The message content without the command call
	 */
	public String[] getMessageArray(@NotNull MessageReceivedEvent event) {
		return this.getMessage(event).split(" ");
	}

	/**
	 * Splits the command call from the rest of the message
	 * @param event		The event received when the user sent a message
	 * @return			The message content without the command call
	 */
	public String getMessage(@NotNull MessageReceivedEvent event) {
		try {
			return event.getMessage().getContentRaw().split("(?i)".concat((Arrays.asList("+" , "*" , "?" , "^" , "$" , "(" , ")" , "[" , "]" , "{" , "}" , "|" , "\\").contains(this.commandName.substring(0, 1)) ? "\\" : "") + this.commandName + " "))[1].trim().replaceAll(" +",  " ");
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	protected void removeReaction(MessageReceivedEvent event, String emoji) {
		event.getMessage().removeReaction(Emoji.fromUnicode(emoji)).queueAfter(2, TimeUnit.SECONDS);
	}
}
