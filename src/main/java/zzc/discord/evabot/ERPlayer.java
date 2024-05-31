package zzc.discord.evabot;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

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
	 * The name of the player
	 */
	protected String name;
	
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
	 * The ranked games registered of the player
	 */
	protected List<GameLog> games;

	/**
	 * Constructor of an ERPlayer
	 * 
	 * @param dakName	The DAK name of the player. Will also serve as the player name
	 */
	public static ERPlayer getERPlayer(String dakName) {
		Bot.deserializePlayers();
		
		if (Bot.allPlayers.stream().anyMatch(p -> p.getName().equalsIgnoreCase(dakName))) {
			System.err.println("Player already exists; getting back the properties...");
			ERPlayer player = Bot.allPlayers.stream().filter(p -> p.getName().equalsIgnoreCase(dakName)).findFirst().orElse(null);
			if (player != null)
				player.updateMmr();
			return player;
			//System.err.println("LocalTime: " + this.lastUpdateTime.toString());
		} else {
			return new ERPlayer(dakName, dakName);
		}
	}
	
	/**
	 * Constructor of an ERPlayer
	 * 
	 * @param name	The name of the player
	 * @param dak	The DAK link of the player
	 */
	public ERPlayer(String name, String dak) {
		this.name = name;
		//this.mmr = mmr;
		this.dak = dak;
		
		this.games = new ArrayList<GameLog>();
		
		Bot.allPlayers.add(this);
		
		this.updateMmr();
		//this.lastUpdateTime = LocalDateTime.now();
	}
	
	/**
	 * Getter of name
	 * @return	The name of this player
	 */
	public String getName() {
		return this.name;
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
		if (this.games.size() > 0) {
//			LocalDateTime date = LocalDateTime.parse(this.games.get(0).getString("startDtm"));
//			return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
			return this.games.get(0);
		} else
			return null;
	}
	
	/**
	 * Getter of games
	 * @return	The list of all the games registered of this player
	 */
	public List<GameLog> getAllGames() {
		return this.games;
	}
	
	/**
	 * Setter of name
	 * @param name	The new name for this player
	 */
	public void setName(String name) {
		this.name = name;
		
		Bot.serializePlayers();
	}

	public void addGame(GameLog game) {
		this.games.add(0, game);
	}

	public void addGames(List<GameLog> game) {
		this.games.addAll(0, game);
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
	 * Private function to get the dak name out of the dak link
	 * @return	The name at the end of the dak link. Used with the ER API calls.
	 */
	private String getDakName() {
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
			this.setMmr(0);
			this.rank = 0;
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
	public static boolean alreadyRegistered(String name, String channelName) {
		List<Team> teams = Bot.scrims.get(channelName);
		if (teams != null)
			return teams.stream().anyMatch(team -> team.getPlayers().stream().anyMatch(player -> player.getName().equalsIgnoreCase(name)));
		return false;
	}

	/**
	 * Overrides the .equals method so that a ERPlayer equals another ERPlayer if their name is the same
	 */
	@Override
	public boolean equals(Object o) {
		if (o != null && o.getClass().isAssignableFrom(ERPlayer.class)) {
			ERPlayer p = (ERPlayer)o;
			
			return this.getName().equalsIgnoreCase(p.getName());
		}
		
		return false;
	}
	
	/**
	 * Copy the player from another player. Used when the player we are trying to create already exists.
	 * @param player	The player to copy
	 */
	protected void copy(ERPlayer player) {
		this.name = player.name;
		this.dak = player.dak;
		this.mmr = player.mmr;
		this.lastUpdateTime = player.lastUpdateTime;
		this.rank = player.rank;
	}
}
