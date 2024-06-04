package zzc.discord.evabot.events;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.Bot;
import zzc.discord.evabot.Scrim;

/**
 * 
 * @author Lesys
 *
 * Class of EventER that removes a Scrim
 */
public class EventERRemoveScrim extends EventER {
	/**
	 * Constructor of EventGuildMessageRemoveTeam
	 */
	public EventERRemoveScrim() {
		this.commandName += "removeScrim";
	}
	
	/**
	 * Check if the Scrim exists and removes it
	 */
	@Override
	public void exeuteCommand(@NotNull MessageReceivedEvent event) {
		Bot.deserializeScrims();
		
		Scrim scrim = Bot.getScrim(event);
		
		if (scrim != null) {
			if (EventERManager.hasPermission(event)) {
				if (Bot.scrims.remove(scrim)) {
					Bot.serializeScrims();

					event.getChannel().sendMessage("The scrim " + scrim.getName() + " for the server \"" + scrim.getDiscordServerName() + "\" has been deleted.").queue();
					event.getMessage().addReaction(Emoji.fromUnicode("U+2705")).queue();
				} else {
					event.getChannel().sendMessage(scrim.getName() + " from the server \"" + scrim.getDiscordServerName() + "\" hasn't been removed from the scrim list.").queue();
				}
			} else {
				event.getChannel().sendMessage(event.getAuthor().getAsMention() + " does not have the rights to use this command. Only an Administrator of the server can use it.").queue();
			}
		} else {
			event.getChannel().sendMessage(event.getChannel().getName() + " has no Teams registered so far, therefore cannot be removed.").queue();
		}
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " - Removes the scrim from the list of scrims.\n";
	}
}
