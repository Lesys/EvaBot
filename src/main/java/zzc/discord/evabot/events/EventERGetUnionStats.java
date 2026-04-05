package zzc.discord.evabot.events;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import com.mashape.unirest.http.exceptions.UnirestException;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.ERPlayer;
import zzc.discord.evabot.GameLog;
import zzc.discord.evabot.GetPlayerStats;
import zzc.discord.evabot.TeamMate;
import zzc.discord.evabot.util.UtilEmpty;

/**
 * 
 * @author Lesys
 *
 * Class of EventER when the user wants to get the Union games stats about a player.
 */
public class EventERGetUnionStats extends EventER {
	final protected Map<String, BiFunction<TeamMate, List<GameLog>, TeamMate>> options = new TreeMap<String, BiFunction<TeamMate, List<GameLog>, TeamMate>>(String.CASE_INSENSITIVE_ORDER);
	final protected Map<String, Function<TeamMate, String>> stringReturn = new TreeMap<String, Function<TeamMate, String>>(String.CASE_INSENSITIVE_ORDER);
	/**
	 * Constructor of EventERGetUnionStats
	 */
	public EventERGetUnionStats() {
		this.commandName += "getUnionStats";

		this.options.putAll(Map.of("game", EventERGetUnionStats::getGames));

		this.stringReturn.putAll(Map.of("game", EventERGetUnionStats::getStringGames));
	}
	
	/**
	 * Gets the Union games and displays the stat related to the one asked for
	 */
	@Override
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();
		
		String[] message = this.getMessageArray(event);
		
		if (message.length >= 1 && message.length <= 2) {
			final String option = message.length == 2 ? message[message.length - 1] : "game";
			
			if (this.options.keySet().stream().anyMatch(o -> o.equalsIgnoreCase(option))) {
				String playerName = message[0];
				
				try {
					GetPlayerStats.retrieveGames(playerName);
				} catch (UnirestException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ERPlayer player = ERPlayer.getERPlayer(playerName);
				List<GameLog> filteredList = player.getAllUnionGames().stream().filter(gl -> String.valueOf(gl.getSeasonId()).equalsIgnoreCase(GetPlayerStats.getSeason()) || gl.getSeasonId() == 0).toList();
				
				TeamMate result = new TeamMate(playerName);

				result = this.options.get(option).apply(result, filteredList);
				StringBuffer buffer = new StringBuffer();

				String playerNameDisplay = UtilEmpty.isEmptyOrNull(player.getHistoryPlayerName()) ? player.getDakName() : "(" + String.join(" / ", player.getHistoryPlayerName()) + ")";
				
				if (filteredList.size() > 0) {
					buffer.append("Here are the Union statistics of the player " + playerNameDisplay + " about " + option + "s :\n");
					buffer.append(this.stringReturn.get(option).apply(result));
				} else {
					buffer.append("You haven't yet to play this union season.");
				}
				event.getChannel().sendMessage(buffer).queue();
			} else {			
				event.getChannel().sendMessage("Please enter a correct option between " + this.options.keySet().toString() + ".").queue();
			}
		} else {			
			event.getChannel().sendMessage("Please enter the name of the player and only their name.").queue();
		}
		
		event.getMessage().removeReaction(Emoji.fromUnicode("U+1F504")).queue();
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " {AccountName} " + this.options.keySet().toString() + " - Returns the account's best team mate according to the statistic in [option] (win ratio if no option).\n";
	}
	
	protected static TeamMate getGames(TeamMate player, List<GameLog> filteredList) {
		filteredList.stream().forEach(gl -> {
			player.addMmrGainInGame(gl.getMmrGainInGame());
			player.addPlacement(gl.getPlacement());
		});
		player.addTotalGames(filteredList.size());
		
		return player;		
	}
	
	protected static String getStringGames(TeamMate tm) {
		return tm.getNickname() + " - " + tm.getTotalGames() + " games (" + tm.averageRpGains() + " average team score (TS) gain / " + tm.averagePlacement() + " average placement)\n";
	}
}
