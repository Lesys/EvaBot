package zzc.discord.evabot.events;


import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.Bot;

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
	public void exeuteCommand(@NotNull MessageReceivedEvent event) {
		Bot.deserializeScrims();

        final StringBuilder builder = new StringBuilder();
        EventERManager.commands.forEach(command -> builder.append(command.helpCommand()));
        
        event.getAuthor().openPrivateChannel().queue((channel) ->
        {
            channel.sendMessage(builder).queue();
        });
			
		event.getMessage().addReaction(Emoji.fromUnicode("U+2705")).queue();
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " - Gets this message. Arguments in {} are required while [] are optional.\n";
	}
}
