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

/**
 * 
 * @author Lesys
 *
 * Class of EventER when the user wants to get the character with which he had the most success depending on the option chosen.
 */
public class EventERGetBestCharacter extends EventER {
	final protected Map<String, BiFunction<List<TeamMate>, List<GameLog>, List<TeamMate>>> options = new TreeMap<String, BiFunction<List<TeamMate>, List<GameLog>, List<TeamMate>>>(String.CASE_INSENSITIVE_ORDER);
	final protected Map<String, Function<TeamMate, String>> stringReturn = new TreeMap<String, Function<TeamMate, String>>(String.CASE_INSENSITIVE_ORDER);
	/**
	 * Constructor of EventERGetBestCharacter
	 */
	public EventERGetBestCharacter() {
		this.commandName += "getBestCharacter";
	}
	
	/**
	 * Gets the rank of the player and displays his MMR with his global ranking alongside with its server distribution (high ranking players need at least 80% games in the same server to be eligible for the high ranking rewards in this servers)
	 */
	@Override
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();
		this.options.putAll(Map.of("win", EventERGetBestCharacter::getMostWins,
			"TK", EventERGetBestCharacter::getMostTk,
			"placement", EventERGetBestCharacter::getBestPlacement,
			"games", EventERGetBestCharacter::getGames,
			"RP", EventERGetBestCharacter::getAverageRp));

		this.stringReturn.putAll(Map.of("win", EventERGetBestCharacter::getStringWins,
			"TK", EventERGetBestCharacter::getStringTk,
			"placement", EventERGetBestCharacter::getStringPlacement,
			"games", EventERGetBestCharacter::getStringGames,
			"RP", EventERGetBestCharacter::getStringRp));
		
		String[] message = this.getMessageArray(event);
		
		if (message.length >= 1 && message.length <= 2) {
			final String option = message.length == 2 ? message[message.length - 1] : "win";
			
			if (this.options.keySet().stream().anyMatch(o -> o.equalsIgnoreCase(option))) {
				String playerName = message[0];
				
				try {
					GetPlayerStats.retrieveGames(playerName);
				} catch (UnirestException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ERPlayer player = ERPlayer.getERPlayer(playerName);
				List<GameLog> filteredList = player.getAllRankedGames().stream().filter(gl -> String.valueOf(gl.getSeasonId()).equalsIgnoreCase(GetPlayerStats.getSeason())).toList();

				List<TeamMate> bestCharacter = new ArrayList<TeamMate>();
				List<TeamMate> result = new ArrayList<TeamMate>();

				result = this.options.get(option).apply(bestCharacter, filteredList);
				StringBuffer buffer = new StringBuffer();
				
				//bestTeammate.stream().sorted(Comparator.comparing(TeamMate::averageTK));
				
				if (filteredList.size() > 0) {
					buffer.append("Here are the 10 characters which " + player.getDakName() + " had the most " + option + "s with :\n");
					result.stream().limit(10).forEach(tm -> buffer.append(this.stringReturn.get(option).apply(tm)));
				} else {
					buffer.append("You haven't yet to play this ranked season or the player \"" + playerName + "\" doesn't exist.");
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
		return super.helpCommand() + " {AccountName} " + this.options.keySet().toString() + " - Returns the account's best character according to the statistic in [option] (win ratio if no option).\n";
	}
	
	protected static List<TeamMate> getMostWins(List<TeamMate> bestCharacter, List<GameLog> filteredList) {
		filteredList.stream().filter(gl -> gl.getPlacement() == 1).forEach(gl -> {TeamMate teammate = null; teammate = bestCharacter.stream().filter(tm -> tm.getNickname().equals(gl.getCharacterPlayed())).findFirst().orElse(null); if (teammate == null) {teammate = new TeamMate(gl.getCharacterPlayed()); bestCharacter.add(teammate);} teammate.addTotalWins(1);});
		bestCharacter.forEach(tm -> tm.addTotalGames(Long.valueOf(filteredList.stream().filter(gl -> gl.getCharacterPlayed().equalsIgnoreCase(tm.getNickname())).count()).intValue()));

		return bestCharacter.stream().sorted(Comparator.reverseOrder()).toList();
	}

	protected static List<TeamMate> getMostTk(List<TeamMate> bestCharacter, List<GameLog> filteredList) {
		filteredList.stream().forEach(gl -> {TeamMate teammate = null; teammate = bestCharacter.stream().filter(tm -> tm.getNickname().equals(gl.getCharacterPlayed())).findFirst().orElse(null); if (teammate == null) {teammate = new TeamMate(gl.getCharacterPlayed()); bestCharacter.add(teammate);} teammate.addTeamKill(gl.getTeamKill());});
		bestCharacter.forEach(tm -> tm.addTotalGames(Long.valueOf(filteredList.stream().filter(gl -> gl.getCharacterPlayed().equalsIgnoreCase(tm.getNickname())).count()).intValue()));
		
		return bestCharacter.stream().sorted(Comparator.comparing(TeamMate::averageTK).reversed()).toList();
	}
	
	protected static List<TeamMate> getBestPlacement(List<TeamMate> bestCharacter, List<GameLog> filteredList) {
		filteredList.stream().forEach(gl -> {TeamMate teammate = null; teammate = bestCharacter.stream().filter(tm -> tm.getNickname().equals(gl.getCharacterPlayed())).findFirst().orElse(null); if (teammate == null) {teammate = new TeamMate(gl.getCharacterPlayed()); bestCharacter.add(teammate);} teammate.addPlacement(gl.getPlacement());});
		bestCharacter.forEach(tm -> tm.addTotalGames(Long.valueOf(filteredList.stream().filter(gl -> gl.getCharacterPlayed().equalsIgnoreCase(tm.getNickname())).count()).intValue()));
		
		return bestCharacter.stream().filter(tm -> tm.getTotalGames() > 1).sorted(Comparator.comparing(TeamMate::averagePlacement)).toList();		
	}
	
	protected static List<TeamMate> getGames(List<TeamMate> bestCharacter, List<GameLog> filteredList) {
		filteredList.stream().forEach(gl -> {TeamMate teammate = null; teammate = bestCharacter.stream().filter(tm -> tm.getNickname().equals(gl.getCharacterPlayed())).findFirst().orElse(null); if (teammate == null) {teammate = new TeamMate(gl.getCharacterPlayed()); bestCharacter.add(teammate);} teammate.addMmrGainInGame(gl.getMmrGainInGame()); teammate.addPlacement(gl.getPlacement());});
		bestCharacter.forEach(tm -> tm.addTotalGames(Long.valueOf(filteredList.stream().filter(gl -> gl.getCharacterPlayed().equalsIgnoreCase(tm.getNickname())).count()).intValue()));
		
		return bestCharacter.stream().filter(tm -> tm.getTotalGames() > 1).sorted(Comparator.comparing(TeamMate::getTotalGames).thenComparing(TeamMate::getMmrGainInGame).reversed()).toList();		
	}
	
	protected static List<TeamMate> getAverageRp(List<TeamMate> bestCharacter, List<GameLog> filteredList) {
		filteredList.stream().forEach(gl -> {TeamMate teammate = null; teammate = bestCharacter.stream().filter(tm -> tm.getNickname().equals(gl.getCharacterPlayed())).findFirst().orElse(null); if (teammate == null) {teammate = new TeamMate(gl.getCharacterPlayed()); bestCharacter.add(teammate);} teammate.addMmrGainInGame(gl.getMmrGainInGame()); teammate.addPlacement(gl.getPlacement());});
		bestCharacter.forEach(tm -> tm.addTotalGames(Long.valueOf(filteredList.stream().filter(gl -> gl.getCharacterPlayed().equalsIgnoreCase(tm.getNickname())).count()).intValue()));

		return bestCharacter.stream().filter(tm -> tm.getTotalGames() > 1).sorted(Comparator.comparing(TeamMate::averageRpGains).reversed()).toList();		
	}
	
	protected static String getStringWins(TeamMate tm) {
		return tm.getNickname() + " - " + tm.getTotalWins() + " wins (" + tm.ratioWinGames() * 100 + "%)\n";
	}
	
	protected static String getStringTk(TeamMate tm) {
		return tm.getNickname() + " - " + tm.getTeamKill() + " Total TKs (" + tm.averageTK() + " average on " + tm.getTotalGames() + " games)\n";
	}
	
	protected static String getStringPlacement(TeamMate tm) {
		return tm.getNickname() + " - " + tm.averagePlacement() + " average placement (" + tm.getTotalGames() + " total games)\n";
	}
	
	protected static String getStringGames(TeamMate tm) {
		return tm.getNickname() + " - " + tm.getTotalGames() + " games (" + tm.averageRpGains() + " average RP gain / " + tm.averagePlacement() + " average placement)\n";
	}
	
	protected static String getStringRp(TeamMate tm) {
		return tm.getNickname() + " - " + tm.averageRpGains() + " average RP (" + tm.averagePlacement() + " average placement / " + tm.getTotalGames() + " games)\n";
	}
}
