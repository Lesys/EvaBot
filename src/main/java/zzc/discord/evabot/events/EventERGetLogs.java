package zzc.discord.evabot.events;


import java.util.*;

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
 * Class of EventER when a moderator wants to get the logs of a scrim registration channel
 */
public class EventERGetLogs extends EventER {
	/**
	 * Constructor of EventERGetLogs
	 */
	public EventERGetLogs() {
		this.commandName += "getLogs";
	}
	
	/**
	 * Gets the scrim and sends the logs in DM. Number of logs sent can be changed with putting argument in command line.
	 */
	@Override
	public void exeuteCommand(@NotNull MessageReceivedEvent event) {
		final List<String> messages = new ArrayList<String>();
		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();
		Bot.deserializeScrims();

        final StringBuilder builder = new StringBuilder();
        int logNumber;
        try {
	        logNumber = event.getMessage().getContentRaw().split(" ").length > 1 ? Integer.valueOf(event.getMessage().getContentRaw().split(" ")[1]) : 10;
        } catch (NumberFormatException e) {
        	System.err.println("Conversion exception: " + event.getMessage().getContentRaw().split(" ")[1] + " is not an integer. Default value set to 10");
        	logNumber = 10;
        }
        builder.append("List of logs (last " + logNumber + ") in \"" + event.getChannel().getName() + "\":\n");

		Scrim scrim = Bot.getScrim(event);
		if (scrim != null) {
			if (EventERManager.hasPermission(event)) {
				List<MessageLog> logs = scrim.getLogs();
				Collections.sort(logs);
				Collections.reverse(logs);

				for (int i = 0; i < logNumber && i < logs.size(); i++) {
			    	if (builder.length() >= 1800) {
			    		messages.add(builder.toString());
			            builder.delete(0, builder.length());
			    	}
					EventERGetLogs.logStringBuilder(builder, logs.get(i));
				}

				messages.add(builder.toString());
				
		        event.getAuthor().openPrivateChannel().queue((channel) ->
		        {
		        	messages.forEach(m -> channel.sendMessage(m).queue());
		        });

				event.getMessage().addReaction(Emoji.fromUnicode("U+2705")).queue();
			} else {
				event.getChannel().sendMessage(event.getAuthor().getAsMention() + " does not have the rights to use this command. Only a moderator can use it.").queue();
			}
		} else {			
			event.getChannel().sendMessage("No teams has yet to be registered for the scrim \"" + event.getChannel().getName() + "\".").queue();
		}
		
		event.getMessage().removeReaction(Emoji.fromUnicode("U+1F504")).queue();
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " [logNumber] - Returns the last \"logNumber\" logs for the scrim (\"logNumber\" is 10 by default).\n";
	}
	
	/**
	 * Protected method to construct the string to send
	 * @param builder	The string builder that is going to be displayed in the message
	 * @param message	The Message for which you want to create the string
	 */
	protected static void logStringBuilder(final StringBuilder builder, MessageLog message) {
		builder.append("\n- **__" + message.getAuthor() + "__** on __" + message.getDateTimeString() + "__:\n" + message.getMessage() + "\n");
	}
}
