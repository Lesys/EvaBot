package zzc.discord.evabot.events;


import java.util.*;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * 
 * @author Lesys
 *
 * Class of EventER when the user asks for the command helper
 */
public class EventERHelpCommand extends EventER {
	/**
	 * Constructor of EventGuildMessageHelpCommand
	 */
	public EventERHelpCommand() {
		this.commandName += "help";
	}
	
	/**
	 * Sends the help string for all the EventER registered 
	 */
	@Override
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		final List<String> messages = new ArrayList<String>();
        final StringBuilder builder = new StringBuilder();
        EventERManager.commands.forEach(command -> {
        	if (builder.length() > 0 && builder.length() + command.helpCommand().length() >= 2000) {
        		messages.add(builder.toString());
                builder.delete(0, builder.length());
        	}
        	builder.append(command.helpCommand());
        });

		messages.add(builder.toString());
        event.getAuthor().openPrivateChannel().queue((channel) ->
        {
        	messages.forEach(m -> channel.sendMessage(m).queue());
        });
			
		event.getMessage().addReaction(Emoji.fromUnicode("U+2705")).queue();
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " - Gets this message. Arguments in {} are required while [] are optional.\n";
	}
}
