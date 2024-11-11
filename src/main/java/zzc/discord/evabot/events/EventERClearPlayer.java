package zzc.discord.evabot.events;


import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.Bot;
import zzc.discord.evabot.ERPlayer;

/**
 * 
 * @author Lesys
 *
 * Class of EventER that removes an ERPlayer from a team
 */
public class EventERClearPlayer extends EventER {
	/**
	 * Constructor of EventGuildMessageRemovePlayer
	 */
	public EventERClearPlayer() {
		this.commandName += "clearPlayer";
	}
	
	/**
	 * Check if the Team name exists in the registered teams, and removes the ERPlayer if it exists in the Team
	 */
	@Override
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		Bot.deserializePlayers();
		Bot.deserializeGameLog();
		
		String[] message = this.getMessageArray(event);
		
		if (message.length == 1) {
			String playerName = message[message.length - 1];
			
			//final String finalPlayerName = playerName;
			ERPlayer player = ERPlayer.getERPlayer(playerName);
			
			if (Bot.games.removeIf(gl -> gl.getNickname().equalsIgnoreCase(player.getDakName()))) {
				Bot.serializeGameLog();
				event.getChannel().sendMessage("The player " + player.getDakName() + " has been correctly cleared from the players. Current games in log: " + Bot.games.stream().filter(gl -> gl.getNickname().equalsIgnoreCase(player.getDakName())).count()).queue();
			} else {
				event.getChannel().sendMessage("The games of player " + player.getDakName() + " couldn't be removed. Current games in log: " + Bot.games.stream().filter(gl -> gl.getNickname().equalsIgnoreCase(player.getDakName())).count()).queue();
			}
		} else {			
			event.getChannel().sendMessage("Please enter the name of the player you want to clear the games.").queue();
		}
		
		event.getMessage().removeReaction(Emoji.fromUnicode("U+1F504")).queue();
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " {PlayerAccountName} - Clears the games of the player from the logs.\n";
	}
}
