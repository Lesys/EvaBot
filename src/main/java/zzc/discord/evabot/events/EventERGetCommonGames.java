package zzc.discord.evabot.events;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.GameLog;
import zzc.discord.evabot.GetPlayerStats;

/**
 * 
 * @author Lesys
 *
 * Class of EventER when the user wants to get statistics of games for a player1 and a player2 together.
 */
public class EventERGetCommonGames extends EventER {
	/**
	 * Constructor of EventERGetRank
	 */
	public EventERGetCommonGames() {
		this.commandName += "getCommonGames";
	}
	
	/**
	 * Gets the games of player 1 and player 2 in common, and displays number of games, average placement, kills and wins alongside last games ID
	 */
	@Override
	public void exeuteCommand(@NotNull MessageReceivedEvent event) {
		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();

		String[] message = event.getMessage().getContentRaw().split("(?i)".concat((Arrays.asList("+" , "*" , "?" , "^" , "$" , "(" , ")" , "[" , "]" , "{" , "}" , "|" , "\\").contains(this.commandName.substring(0, 1)) ? "\\" : "") + this.commandName + " "))[1].split(" ");

		if (message.length == 2) {
			String playerName1 = message[message.length - 2];
			String playerName2 = message[message.length - 1];
			
			//final String finalPlayerName = playerName;
			//ERPlayer player = ERPlayer.getERPlayer(playerName1);
			List<GameLog> commonGames = GetPlayerStats.commonGames(playerName1, playerName2);
			StringBuffer buffer = new StringBuffer();

			int totalGames = commonGames.size();
			
			if (commonGames.size() > 0) {
				buffer.append("You have played a total of **" + totalGames + "** games with " + playerName2 + " for an average kills of " + commonGames.stream().mapToDouble(gl -> gl.getTeamKill()).average().orElse(0) + " and an average placement of **" + commonGames.stream().mapToDouble(gl -> gl.getPlacement()).average().orElse(0) + "** (__" + commonGames.stream().map(gl -> gl.getPlacement()).filter(p -> p == 1).count() + " wins__).\n");
				buffer.append("Last game played was on " + commonGames.get(0).getDateTimeString() + " (GameID: " + commonGames.get(0).getGameId() + ") and finished " + commonGames.get(0).getPlacement() + ".\n");
				buffer.append("Last 10 games:"); commonGames.stream().limit(10).forEach(gl -> buffer.append(" __" + gl.getGameId() + "__"));
			} else {
				buffer.append("You haven't yet to play with " + playerName2 + " this ranked season.");
			}
			event.getChannel().sendMessage(buffer).queue();
		} else {			
			event.getChannel().sendMessage("Please enter the name of the player and only their name.").queue();
		}
		
		event.getMessage().removeReaction(Emoji.fromUnicode("U+1F504")).queue();
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " {AccountName} {TeammateName} - Returns the account's number of games played with the other player, average placement and number of wins. Also returns ID of last 10 games.\n";
	}
}
