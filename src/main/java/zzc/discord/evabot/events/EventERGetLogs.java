package zzc.discord.evabot.events;


import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.Bot;
import zzc.discord.evabot.dto.MessageLogDTO;
import zzc.discord.evabot.dto.ScrimDTO;
import zzc.discord.evabot.service.ScrimService;
import zzc.discord.evabot.util.UtilDate;

/**
 * 
 * @author Lesys
 *
 * Class of EventER when a moderator wants to get the logs of a scrim registration channel
 */
@Service
public class EventERGetLogs extends EventER {
	private static int DEFAULT_LOG_NUMBER = 10;
	private final transient ScrimService scrimService;
	
	/**
	 * Constructor of EventERGetLogs
	 */
	public EventERGetLogs(ScrimService scrimService) {
		this.commandName += "getLogs";
		
		this.scrimService = scrimService;
	}
	
	/**
	 * Gets the scrim and sends the logs in DM. Number of logs sent can be changed with putting argument in command line.
	 */
	@Override
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		final List<String> messages = new ArrayList<String>();

        final StringBuilder builder = new StringBuilder();
        int logNumber;
        try {
	        logNumber = event.getMessage().getContentRaw().split(" ").length > 1 ? Integer.valueOf(event.getMessage().getContentRaw().split(" ")[1]) : DEFAULT_LOG_NUMBER;
        } catch (NumberFormatException e) {
        	System.err.println("Conversion exception: " + event.getMessage().getContentRaw().split(" ")[1] + " is not an integer. Default value set to " + DEFAULT_LOG_NUMBER);
        	logNumber = DEFAULT_LOG_NUMBER;
        }
        builder.append("List of logs (last " + logNumber + ") in \"" + event.getChannel().getName() + "\":\n");

		ScrimDTO scrim = this.scrimService.getByEvent(event);
		
		if (scrim != null) {
			if (EventERManager.hasPermission(event)) {
				List<MessageLogDTO> logs = scrim.getMessageLogList();
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

				this.commandIsSuccessful();
			} else {
				event.getChannel().sendMessage(event.getAuthor().getAsMention() + " does not have the rights to use this command. Only a moderator can use it.").queue();
			}
		} else {			
			event.getChannel().sendMessage("No teams has yet to be registered for the scrim \"" + event.getChannel().getName() + "\".").queue();
		}
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " [logNumber] - Returns the last \"logNumber\" logs for the scrim (\"logNumber\" is " + DEFAULT_LOG_NUMBER + " by default).\n";
	}
	
	/**
	 * Protected method to construct the string to send
	 * @param builder	The string builder that is going to be displayed in the message
	 * @param message	The MessageLogDTO from which data are retrieved
	 */
	protected static void logStringBuilder(final StringBuilder builder, MessageLogDTO message) {
		builder.append("\n- **__" + message.getAuthorName() + "__** on __" + UtilDate.dateToString(message.getCreationTime()) + "__:\n" + message.getMessage() + "\n");
	}
}
