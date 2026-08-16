package zzc.discord.evabot.events;


import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.dto.ERPlayerDTO;
import zzc.discord.evabot.service.ERPlayerService;

/**
 * 
 * @author Lesys
 *
 * Class of EventER that changes the displayed name of a registered ERPlayer
 */
@Service
public class EventERChangeDisplayName extends EventER {
	private final transient ERPlayerService erPlayerService;
	/**
	 * Constructor of EventERChangeDisplayName
	 */
	@Autowired
	public EventERChangeDisplayName(ERPlayerService erPlayerService) {
		this.commandName += "changePlayerDisplayName";
		
		this.erPlayerService = erPlayerService;
	}
	
	/**
	 * Check if the Player name exists in the registered teams, and changes the ERPlayer name if it exists in a Team
	 */
	@Override
	@Transactional
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		String[] message = this.getMessageArray(event);
		
		if (message.length >= 2) {
			String newDisplayName = "";
			
			for (int i = 1; i < message.length; i++)
				newDisplayName += message[i];
			String discordName = event.getMessage().getMentions().getMembers().size() == 1 ? event.getMessage().getMentions().getMembers().get(0).getUser().getName() : message[0];
			
			ERPlayerDTO player = this.erPlayerService.getDtoByDiscordName(discordName);
			
			if (player != null) {
				if (EventERManager.hasPermissionOnPlayer(event, player)) {
					player.setDisplayName(newDisplayName);
					
					this.erPlayerService.save(player);

			        this.commandIsSuccessful();
				} else {
					event.getChannel().sendMessage(event.getAuthor().getAsMention() + " does not have the rights to use this command. Only " + player.getDiscordName() + " can use it.").queue();
				}
			} else {
				event.getChannel().sendMessage(discordName + " hasn't been registered in this Team.").queue();
			}
		} else {			
			event.getChannel().sendMessage("Please enter the discord name of the player followed by the dak link (or at least their in game name).").queue();
		}
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " {PlayerDiscordName} {NewDisplayName} - Changes the displayed name of a player registered.\n";
	}
}
