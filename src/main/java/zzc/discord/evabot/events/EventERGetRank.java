package zzc.discord.evabot.events;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.ERPlayer;

/**
 * 
 * @author Lesys
 *
 * Class of EventER when the user wants to get the rank of a player.
 */
public class EventERGetRank extends EventER {
	/**
	 * Constructor of EventERGetRank
	 */
	public EventERGetRank() {
		this.commandName += "getRank";
	}
	
	/**
	 * Gets the rank of the player and displays his MMR with his global ranking
	 */
	@Override
	public void exeuteCommand(@NotNull MessageReceivedEvent event) {
		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();

		String[] message = this.getMessageArray(event);
		
		if (message.length == 1) {
			String playerName = message[message.length - 1];
			
			//final String finalPlayerName = playerName;
			ERPlayer player = ERPlayer.getERPlayer(playerName);
			player.updateMmr();
			StringBuffer buffer = new StringBuffer();
			buffer.append(player.getDakName() + ": " + player.getMmr() + " RP - #" + player.getRankGlobal() + " Global\n");

			event.getChannel().sendMessage(buffer).queue();
		} else {			
			event.getChannel().sendMessage("Please enter the name of the player and only their name.").queue();
		}
		
		event.getMessage().removeReaction(Emoji.fromUnicode("U+1F504")).queue();
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " {AccountName} - Returns the account's MMR and global ranking. Allows user to check the RP without having to register to a scrim.\n";
	}
}
