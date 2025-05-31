package zzc.discord.evabot;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

import org.json.JSONObject;

/**
 * 
 * @author Lesys
 * 
 * Class representing a competitive Eternal Return Player (ERPlayer)
 */
public class ERPlayer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1884928778100498144L;
	
	/**
	 * The discord name of the player
	 */
	protected String discordName;
	
	protected String displayName = "";
	
	/**
	 * The MMR of the player, the most uptodate
	 */
	protected int mmr;
	
	/**
	 * The global rank of the player, the most uptodate
	 */
	protected int rank;
	/**
	 * The DAK link of the player. The account name may differ from the player name
	 */
	protected String dak;
	
	/**
	 * The last time the MMR was updated
	 */
	protected LocalDateTime lastUpdateTime;

	/**
	 * Constructor of an ERPlayer
	 * 
	 * @param dakName	The DAK name of the player. Will also serve as the player name
	 */
	public static ERPlayer getERPlayer(String dakName) {
		if (dakName != null) {
			Bot.deserializePlayers();
			
			//Bot.allPlayers.stream().forEach(p -> System.err.println("player dakName: " + p.getDakName()));
			if (Bot.allPlayers.stream().anyMatch(p -> p.getDakName().equalsIgnoreCase(dakName))) {
				//System.err.println("Player already exists; getting back the properties...");
				ERPlayer player = Bot.allPlayers.stream().filter(p -> p.getDakName().equalsIgnoreCase(dakName)).findFirst().orElse(null);
				/*if (player != null)
					player.updateMmr();*/
				return player;
			} else {
				return new ERPlayer(dakName, "");
			}
		} else {
			return null;
		}
	}
	
	public static ERPlayer getERPlayerByDiscordName(String discordName) {
		if (discordName != null) {
			Bot.deserializePlayers();
			
			if (Bot.allPlayers.stream().anyMatch(p -> p.getDiscordName().equalsIgnoreCase(discordName))) {
				//System.err.println("Player already exists; getting back the properties...");
				ERPlayer player = Bot.allPlayers.stream().filter(p -> p.getDiscordName().equalsIgnoreCase(discordName)).findFirst().orElse(null);
				/*if (player != null)
					player.updateMmr();*/
				return player;
			} else {
				return new ERPlayer(discordName, discordName);
			}
		} else
			return null;
	}
	

	
	public static String getDiscordNameByDakName(String dakName) {
		ERPlayer player = ERPlayer.getERPlayer(dakName);
		if (player != null) {
			return player.getDiscordName();
		} else {
			return null;
		}
	}
	
	/**
	 * Constructor of an ERPlayer
	 * 
	 * @param dak			The DAK link of the player
	 * @param discordName	The discordName of the player
	 */
	public ERPlayer(String dak, String discordName) {
		System.err.println("New player ! ");
		this.dak = dak;
		this.discordName = discordName;
		this.displayName = "";

		this.mmr = 0;
		this.rank = 0;
		this.lastUpdateTime = null;
		
		Bot.allPlayers.add(this);
		
		Bot.serializePlayers();
		
		//this.updateMmr();
	}

	/**
	 * Getter of discordName
	 * @return	The discord name of this player
	 */
	public String getDiscordName() {
		return this.discordName;
	}

	public String getDisplayName() {
		if (this.displayName == null)
			this.displayName = "";
		return this.displayName;//.isEmpty() ? (this.discordName == null || this.discordName.isEmpty() ? this.getDakName() : this.discordName) : this.displayName;
	}
	
	/**
	 * Getter of MMR
	 * @return	The MMR of this player
	 */
	public int getMmr() {
		return this.mmr;
	}

	/**
	 * Getter of dak
	 * @return	The DAK link of this player
	 */
	public String getDak() {
		return this.dak;
	}
	
	/**
	 * Getter of rank
	 * @return	The rank of this player
	 */
	public int getRankGlobal() {
		return this.rank;
	}
	
	/**
	 * Getter of the last game registered of this player (used for knowing the time they played it mostly)
	 * @return	The last game registered of this player
	 */
	public GameLog getLastGame() {
		List<GameLog> games = this.getAllGames();
		if (games.size() > 0) {
			return games.get(0);
		} else
			return null;
	}
	
	/**
	 * Getter of games
	 * @return	The list of all the games registered of this player
	 */
	public List<GameLog> getAllGames() {
		Bot.deserializeGameLog();
		return Bot.games.stream().filter(gl -> gl.nickname.equalsIgnoreCase(this.getDakName())).toList();
	}
	
	/**
	 * Setter of discordName
	 * @param discordName	The new discord name for this player
	 */
	public void setDiscordName(String discordName) {
		this.discordName = discordName;
		Bot.serializePlayers();
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
		Bot.serializePlayers();
	}
	
	/**
	 * Setter of MMR
	 * @param name	The new MMR for this player
	 */
	protected void setMmr(int mmr) {
		this.lastUpdateTime = LocalDateTime.now();
		this.mmr = mmr;
	}

	/**
	 * Setter of dak
	 * @param name	The new DAK link for this player
	 */
	public void setDak(String dak) {
		if (!this.dak.equalsIgnoreCase(dak)) {
			this.dak = dak;
			this.lastUpdateTime = null;
			this.updateMmr();
		}
	}
	
	/**
	 * Function to get the dak name out of the dak link
	 * @return	The name at the end of the dak link. Used with the ER API calls.
	 */
	public String getDakName() {
		return this.dak.split("/")[this.dak.split("/").length - 1];
	}
	
	/**
	 * Function allowing this player to update their MMR if the last update was more than an hour ago
	 */
	public void updateMmr() {
		LocalDateTime date = LocalDateTime.now();
		//System.out.println("Date: " + date + "Last: " + this.lastUpdateTime + " -1: " + date.minusHours(1) + "; Comparaison: " + date.minusHours(1).isAfter(this.lastUpdateTime));
		if (this.lastUpdateTime == null || date.minusHours(1).isAfter(this.lastUpdateTime)) {
			this.updateMmrForce();
		}
	}

	/**
	 * Function forcing this player to update their MMR
	 */
	public void updateMmrForce() {
		this.lastUpdateTime = LocalDateTime.now();
		JSONObject userRank = GetPlayerStats.getPlayerStats(this.getDakName());
		if (userRank != null) {
			this.setMmr(userRank.getInt("mmr"));
			this.rank = userRank.getInt("rank");
		} else {
			System.err.println("The JSONResponse is null, setting MMR to 0 for " + this.getDakName());
			this.setMmr(0);
			this.rank = 0;
			this.lastUpdateTime = null;
		}
		
		System.err.println("Serialization players...");
		Bot.serializePlayers();
	}

	/**
	 * Check if a player (by player name) is already registered in this channel (== scrim)
	 * @param name			The player name
	 * @param channelName	The channel name (== scrim name)
	 * @return				true if the player already is registered for this scrim, false if not
	 */
	public static boolean alreadyRegistered(String name, String discordServerName, String channelName) {
		Scrim scrim = Bot.getScrim(discordServerName, channelName);
		if (scrim != null)
			return scrim.getTeams().stream().anyMatch(team -> team.getPlayerNames().stream().anyMatch(pName -> pName.equalsIgnoreCase(name)) || (team.getSub() != null && team.getSub().equalsIgnoreCase(name)));
		return false;
	}

	/**
	 * Overrides the .equals method so that a ERPlayer equals another ERPlayer if their name is the same
	 */
	@Override
	public boolean equals(Object o) {
		if (o != null && o.getClass().isAssignableFrom(ERPlayer.class)) {
			ERPlayer p = (ERPlayer)o;
			
			return this.getDiscordName().equalsIgnoreCase(p.getDiscordName());
		}
		
		return false;
	}
	
	/**
	 * Copy the player from another player. Used when the player we are trying to create already exists.
	 * @param player	The player to copy
	 */
	protected void copy(ERPlayer player) {
		this.discordName = player.discordName;
		this.displayName = player.displayName;
		this.dak = player.dak;
		this.mmr = player.mmr;
		this.lastUpdateTime = player.lastUpdateTime;
		this.rank = player.rank;
	}
	
	@Override
	public String toString() {
		return this.discordName;
	}
	
	public static String getNameWithoutSpecialChar(Supplier<String> getString) {
		return getString.get().replaceAll("[*_]", "");
	}
}
