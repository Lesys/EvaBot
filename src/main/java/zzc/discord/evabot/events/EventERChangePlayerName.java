package zzc.discord.evabot.events;


import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.dto.ERPlayerDTO;
import zzc.discord.evabot.dto.MessageLogDTO;
import zzc.discord.evabot.dto.ScrimDTO;
import zzc.discord.evabot.service.ERPlayerService;
import zzc.discord.evabot.service.ScrimService;

/**
 * 
 * @author Lesys
 *
 * Class of EventER that changes the name of a registered ERPlayer
 */
@Service
public class EventERChangePlayerName extends EventER {
	private final transient ERPlayerService erPlayerService;

	private final transient ScrimService scrimService;
	/**
	 * Constructor of EventERChangePlayerName
	 */
	@Autowired
	public EventERChangePlayerName(ERPlayerService erPlayerService, ScrimService scrimService) {
		this.commandName += "changePlayerName";

		this.erPlayerService = erPlayerService;
		this.scrimService = scrimService;
	}
	
	/**
	 * Check if the Player name exists in the registered teams, and changes the ERPlayer name if it exists in a Team
	 */
	@Override
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();
		
		String[] message = this.getMessageArray(event);
		
		if (message.length == 2) {
			String discordName = event.getMessage().getMentions().getMembers().size() == 2 ? event.getMessage().getMentions().getMembers().get(0).getUser().getName() : message[message.length - 2];
			
			final String finalPlayerName = discordName;
			String newPlayerDiscordName = event.getMessage().getMentions().getMembers().size() == 2 ? event.getMessage().getMentions().getMembers().get(1).getUser().getName() : message[message.length - 1];
			ERPlayerDTO player = this.erPlayerService.getDtoByDiscordName(discordName);
			
			if (player != null) {
				ScrimDTO scrim = this.scrimService.getByEvent(event);
				
				if (!scrim.alreadyRegistered(newPlayerDiscordName)) {
					if (EventERManager.hasPermission(event, player)) {
						player.setDiscordName(newPlayerDiscordName);

						this.erPlayerService.save(player);
						
						// Retrieve the scrim with the updated player
						scrim = this.scrimService.getByEvent(event);
						
						scrim.getMessageLogList().add(new MessageLogDTO(event.getMessage()));

						this.scrimService.save(scrim);

				        this.commandIsSuccessful();
					} else {
						event.getChannel().sendMessage(event.getAuthor().getAsMention() + " does not have the rights to use this command. Only " + player.getDiscordName() + " can use it.").queue();
					}
				} else {
					event.getChannel().sendMessage(newPlayerDiscordName + " has already been registered for this scrim.").queue();
				}
			} else {
				event.getChannel().sendMessage(finalPlayerName + " hasn't been registered in this Team.").queue();
			}
		} else {			
			event.getChannel().sendMessage("Please enter the discord name of the player followed by their new discord name.").queue();
		}

		event.getMessage().removeReaction(Emoji.fromUnicode("U+1F504")).queue();
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " {CurrentPlayerDiscordName} {NewPlayerDiscordName} - Changes the name of a player registered.\n";
	}
}
