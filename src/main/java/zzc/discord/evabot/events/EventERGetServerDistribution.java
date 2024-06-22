package zzc.discord.evabot.events;

import java.util.*;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.ERPlayer;
import zzc.discord.evabot.GetPlayerStats;

/**
 * 
 * @author Lesys
 *
 * Class of EventER when the user wants to get the rank of a player alongside with its server distribution.
 */
public class EventERGetServerDistribution extends EventER {
	/**
	 * Constructor of EventERGetRank
	 */
	public EventERGetServerDistribution() {
		this.commandName += "getServerDistribution";
	}
	
	/**
	 * Gets the rank of the player and displays his MMR with his global ranking alongside with its server distribution (high ranking players need at least 80% games in the same server to be eligible for the high ranking rewards in this servers)
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
			Map<String, Integer> servs = GetPlayerStats.serverDistribution(playerName);
			StringBuffer buffer = new StringBuffer();
			buffer.append(player.getDiscordName() + ": " + player.getMmr() + " RP - #" + player.getRankGlobal() + " Global\n");

			StringBuffer mostPlayedServ = new StringBuffer("");
			servs.forEach((k, v) -> {
				buffer.append("Server " + k + ": " + v + " games\n");
				mostPlayedServ.replace(0, mostPlayedServ.length(), (mostPlayedServ.toString().equalsIgnoreCase("") ? k : (v > servs.get(mostPlayedServ.toString()) ? k : mostPlayedServ.toString())));
			});

			int totalGames = servs.keySet().stream().map(key -> servs.get(key)).reduce(0, (a, b) -> a + b);
			
			if (servs.get(mostPlayedServ.toString()) != null) {
				if (servs.get(mostPlayedServ.toString()) >= (int)Math.ceil(totalGames * 80 / 100)) {
					buffer.append(mostPlayedServ.toString() + " is your most played server with more than 80% games (" + servs.get(mostPlayedServ.toString()) + "/" + totalGames + ").");
				} else {
					buffer.append("You don't have a minimum of 80% games in a dedicated server. You would need " + ((totalGames - servs.get(mostPlayedServ.toString())) * 4 - servs.get(mostPlayedServ.toString())) + " more games on " + mostPlayedServ.toString() + " to be eligible on that server (current percentage: " + (servs.get(mostPlayedServ.toString()) * 100 / totalGames) + "%).");
				}
			} else {
				buffer.append("You haven't yet to play a game on this ranked season.");
			}
			event.getChannel().sendMessage(buffer).queue();
		} else {			
			event.getChannel().sendMessage("Please enter the name of the player and only their name.").queue();
		}
		
		event.getMessage().removeReaction(Emoji.fromUnicode("U+1F504")).queue();
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " {AccountName} - Returns the account's MMR and global ranking alongside with the server distribution. A more complete form of information than **\"getRank\"** command but takes longer to execute.\n";
	}
}
