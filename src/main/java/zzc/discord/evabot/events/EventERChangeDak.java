package zzc.discord.evabot.events;


import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.dto.ERPlayerDTO;
import zzc.discord.evabot.dto.MessageLogDTO;
import zzc.discord.evabot.dto.ScrimDTO;
import zzc.discord.evabot.dto.TeamDTO;
import zzc.discord.evabot.service.ERPlayerService;
import zzc.discord.evabot.service.ScrimService;
import zzc.discord.evabot.service.TeamService;

/**
 * 
 * @author Lesys
 *
 * Class of EventER that changes the DAK of a registered ERPlayer
 */
@Service
public class EventERChangeDak extends EventER {
	private final transient ERPlayerService erPlayerService;

	private final transient TeamService teamService;

	private final transient ScrimService scrimService;
	/**
	 * Constructor of EventERChangeDak
	 */
	@Autowired
	public EventERChangeDak(ERPlayerService erPlayerService, TeamService teamService, ScrimService scrimService) {
		this.commandName += "changePlayerDak";

		this.erPlayerService = erPlayerService;
		this.scrimService = scrimService;
		this.teamService = teamService;
	}
	
	/**
	 * Check if the Player name exists in the registered teams, and changes the ERPlayer DAK if it exists in a Team
	 */
	@Override
	@Transactional
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		String[] message = this.getMessageArray(event);

		if (message.length == 2) {
			String discordName = event.getMessage().getMentions().getMembers().size() == 1 ? event.getMessage().getMentions().getMembers().get(0).getUser().getName() : null;
			
			String oldDak = discordName == null ? message[message.length - 2] : null;
			
			String newDak = message[message.length - 1];
			
			ERPlayerDTO player = discordName == null ? this.erPlayerService.getDtoByDak(oldDak) : this.erPlayerService.getDtoByDiscordName(discordName);

			ScrimDTO scrim = this.scrimService.getByChannelId(event.getChannel().getId());
			
			if (scrim != null) {
				TeamDTO team = this.teamService.getByPlayerAndScrim(player, scrim);
				
				if (player != null) {
					if (EventERManager.hasPermissionOnPlayer(event, player) || EventERManager.hasPermissionOnTeam(event, team)) {
						System.out.println("[EventERChangeDak] dak change start");
						
						player.setDak(newDak);
						
						this.erPlayerService.save(player);
						
						// Updates the scrim in case the player has not been updated in there
						scrim = this.scrimService.getByChannelId(event.getChannel().getId());
						
						scrim.getMessageLogList().add(new MessageLogDTO(event.getMessage()));
						
						this.scrimService.save(scrim);

				        this.commandIsSuccessful();
						
						System.out.println("[EventERChangeDak] dak change end");
					} else {
						event.getChannel().sendMessage(event.getAuthor().getAsMention() + " does not have the rights to use this command. Only " + player.getDiscordName() + " can use it.").queue();
					}
				} else {
					event.getChannel().sendMessage(discordName == null ? oldDak : discordName + " hasn't been registered in this Team.").queue();
				}
			} else {
				event.getChannel().sendMessage(event.getChannel().getName() + " hasn't been registered as a Scrim yet.").queue();
			}
		} else {			
			event.getChannel().sendMessage("Please enter the discord name of the player followed by the dak link (or at least their in game name).").queue();
		}
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " {PlayerDiscordMention | OldPlayerDakName} {NewPlayerDakLink} - Changes the DAK of a player registered.\n";
	}
}
