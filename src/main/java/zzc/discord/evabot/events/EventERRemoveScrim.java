package zzc.discord.evabot.events;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.dto.ScrimDTO;
import zzc.discord.evabot.service.ScrimService;

/**
 * 
 * @author Lesys
 *
 * Class of EventER that removes a Scrim
 */
@Service
public class EventERRemoveScrim extends EventER {

	private final transient ScrimService scrimService;
	
	/**
	 * Constructor of EventGuildMessageRemoveTeam
	 */
	public EventERRemoveScrim(ScrimService scrimService) {
		this.commandName += "removeScrim";
		
		this.scrimService = scrimService;
	}
	
	/**
	 * Check if the Scrim exists and removes it
	 */
	@Override
	@Transactional
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		ScrimDTO scrim = this.scrimService.getByEvent(event);
		
		if (scrim != null) {
			if (EventERManager.hasPermissionAdminOrHelper(event)) {
				this.scrimService.delete(scrim);
				
				if (this.scrimService.getByEvent(event) == null) {

					event.getChannel().sendMessage("The scrim " + scrim.getChannelName() + " for the server \"" + scrim.getServer() + "\" has been deleted.").queue();
					
					this.commandIsSuccessful();
				} else {
					event.getChannel().sendMessage(scrim.getChannelName() + " from the server \"" + scrim.getServer() + "\" hasn't been removed from the scrim list.").queue();
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
