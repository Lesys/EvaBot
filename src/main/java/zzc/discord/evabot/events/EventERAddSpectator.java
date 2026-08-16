package zzc.discord.evabot.events;


import java.util.NoSuchElementException;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.dto.ScrimDTO;
import zzc.discord.evabot.dto.ServerDTO;
import zzc.discord.evabot.service.ScrimService;
import zzc.discord.evabot.service.ServerService;

/**
 * 
 * @author Lesys
 *
 * Class of EventER that adds a spectator to a scrim
 */
@Service
public class EventERAddSpectator extends EventER {
	private final transient ScrimService scrimService;

	private final transient ServerService serverService;
	/**
	 * Constructor of EventERAddSpectator
	 */
	public EventERAddSpectator(ScrimService scrimService, ServerService serverService) {
		this.commandName += "addSpectator";
		
		this.scrimService = scrimService;
		this.serverService = serverService;
	}
	
	/**
	 * Check if the	spectator is already spectating the scrim and if not, adds it
	 */
	@Override
	@Transactional
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		String[] message = this.getMessageArray(event);

		String spectatorName = event.getMessage().getMentions().getMembers().size() == 1 ? event.getMessage().getMentions().getMembers().get(0).getUser().getName() : message[message.length - 1];
		
		ServerDTO server = this.serverService.getByEvent(event);
		
		if (server == null) {
			server = new ServerDTO(event.getGuild().getName(), event.getGuild().getId());

			server = this.serverService.getFormatter().entityToDto(this.serverService.save(server));
		}

		ScrimDTO scrim = this.scrimService.getByEvent(event);
		
		if (scrim == null) {
			scrim = new ScrimDTO(server, event.getChannel().getName());
			scrim.setChannelId(event.getChannel().getId());
		}
		
		if (!scrim.alreadySpectating(spectatorName)) {
			if (EventERManager.hasPermissionAdminOrHelper(event)) {
				String spectator = "";
				try {
					User u = event.getMessage().getMentions().getUsers().getFirst();
					spectator = u.getName();
				} catch (NoSuchElementException e) {
					spectator = spectatorName;
				}
				scrim.addSpectators(spectator);
				scrim.addMessage(event.getMessage());
				
				this.scrimService.save(scrim);
				
				this.commandIsSuccessful();
			} else {
				event.getChannel().sendMessage(event.getAuthor().getAsMention() + " does not have the rights to use this command. Only the captain of the team can use it.").queue();
			}
		} else {
			event.getChannel().sendMessage(spectatorName + " is already in the spectator list for this scrim.").queue();
		}
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " @{SpectatorDiscordTag} - Adds the tagged user to the spectator list (will get the role when the role give command will be used).\n";
	}
}
