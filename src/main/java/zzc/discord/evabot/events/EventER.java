package zzc.discord.evabot.events;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.dto.ERPlayerDTO;

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
	public static final String commandPrefix = "!";
	
	/**
	 * The command name of the EventER. Makes it easier to compare if an event has to be executed based on the command name sent by the user.
	 * It is initialized with the prefix for all the commands.
	 */
	protected String commandName = EventER.commandPrefix;
	
	protected boolean commandSuccessful = false;

	public void setCommandSuccessful(boolean commandSuccessful) {
		this.commandSuccessful = commandSuccessful;
	}

	protected void commandIsSuccessful() {
		this.commandSuccessful = true;
	}

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
	@Transactional
	public abstract void executeCommand(@NotNull MessageReceivedEvent event);
	
	public void preExecuteCommand(@NotNull MessageReceivedEvent event) {}
	
	public void postExecuteCommand(@NotNull MessageReceivedEvent event) {}

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
	
	public void sendFinalReaction(@NotNull MessageReceivedEvent event) {
		if (this.commandSuccessful) {
			this.addSuccessfullReaction(event);
		} else {
			this.addErrorReaction(event);
		}
	}
	
	public void addSuccessfullReaction(@NotNull MessageReceivedEvent event) {
		this.addReaction(event, "U+2705");
	}
	
	public void addErrorReaction(@NotNull MessageReceivedEvent event) {
		this.addReaction(event, "U+274C");
	}
	
	protected void addReaction(@NotNull MessageReceivedEvent event, String emoji) {
		event.getMessage().addReaction(Emoji.fromUnicode(emoji)).queueAfter(2, TimeUnit.SECONDS);
	}
	
	protected void removeReaction(@NotNull MessageReceivedEvent event, String emoji) {
		event.getMessage().removeReaction(Emoji.fromUnicode(emoji)).queueAfter(2, TimeUnit.SECONDS);
	}
	
	protected static synchronized void sendMessageWait(@NotNull MessageReceivedEvent event, String message) {
		try {
			event.getChannel().sendMessage(message).queue();
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the string corresponding to the mention of the member
	 * 
	 * @param event		The event sent to be able to mention people
	 * @param player 	The player to mention
	 * @return The mention (or a String if member is null)
	 */
	protected static String getMention(MessageReceivedEvent event, ERPlayerDTO player) {
		if (event == null || player == null) return "";
		
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
	
	protected static Member getMemberFromGuild(MessageReceivedEvent event, ERPlayerDTO player) {
		if (event == null || player == null) return null;
		
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
		if (event == null || playerName == null) return null;
		
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
