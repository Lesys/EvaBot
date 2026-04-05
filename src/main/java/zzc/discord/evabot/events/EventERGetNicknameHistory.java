package zzc.discord.evabot.events;

import java.util.*;

import org.jetbrains.annotations.NotNull;

import com.mashape.unirest.http.exceptions.UnirestException;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.Bot;
import zzc.discord.evabot.ERPlayer;
import zzc.discord.evabot.GetPlayerStats;

/**
 * 
 * @author Lesys
 *
 * Class of EventER when the user wants to get the character with which he had the most success depending on the option chosen.
 */
public class EventERGetNicknameHistory extends EventER {
	/**
	 * Constructor of EventERGetBestCharacter
	 */
	public EventERGetNicknameHistory() {
		this.commandName += "getNicknameHistory";
	}
	
	/**
	 * Gets the rank of the player and displays his MMR with his global ranking alongside with its server distribution (high ranking players need at least 80% games in the same server to be eligible for the high ranking rewards in this servers)
	 */
	@Override
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();
		String[] message = this.getMessageArray(event);
		
		if (message.length == 1) {
			String playerName = message[0];
			
			try {
				GetPlayerStats.retrieveNicknames(playerName);
			
				Bot.deserializePlayers();
	
				ERPlayer player = ERPlayer.getERPlayer(playerName);
				
				List<String> result = player.getHistoryPlayerName();
				
				StringBuffer buffer = new StringBuffer();
				
				if (result.isEmpty() || (result.size() == 1 && result.contains(playerName))) {
					buffer.append("The only name \"" + playerName + "\" used this season is this exact same one.");
				} else {
					buffer.append("Here are the last nicknames used by \"" + playerName + "\" during the current season :");
					result.forEach(nickname -> buffer.append("\n - " + nickname));
				}
				event.getChannel().sendMessage(buffer).queue();
			} catch (UnirestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {			
			event.getChannel().sendMessage("Please enter the name of the player and only their name.").queue();
		}
		
		event.getMessage().removeReaction(Emoji.fromUnicode("U+1F504")).queue();
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " {AccountName} - Returns the account's nicknames used during this season.\n";
	}
}
