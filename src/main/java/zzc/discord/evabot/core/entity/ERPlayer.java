package zzc.discord.evabot.core.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/** 
 * @author Lesys
 * 
 * Entity class representing a competitive Eternal Return Player (ERPlayer) as it is in the database
 */
@Entity
@Table(name = "er_player")
public class ERPlayer extends AbstractEntity {	
	/**
	 * The discord name of the player
	 */
	@Column(name = "discord_name")
	protected String discordName;

	@Column(name = "display_name")
	protected String displayName;
	
	/**
	 * The userId provided by the API to prevent it from making one every request
	 */
	@Column(name = "user_id")
	protected String userId;
	
	/**
	 * The MMR of the player, the most uptodate
	 */
	protected Integer mmr;
	
	/**
	 * The global rank of the player, the most uptodate
	 */
	@Column(name = "global_rank")
	protected Integer globalRank;
	
	/**
	 * The last time the MMR was updated
	 */
	@Column(name = "last_update_time")
	protected LocalDateTime lastUpdateTime;
	/**
	 * The DAK link of the player. The account name may differ from the player name
	 */
	protected String dak;
	
	// TODO Put to Lazy after having the hibernate session global
	@OneToMany(mappedBy = "player", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	protected List<HistoryName> historyPlayerNameList;
	
	// TODO Put to Lazy after having the hibernate session global
	@OneToMany(mappedBy = "captain", fetch = FetchType.EAGER)
	protected transient List<Team> captainTeams;
	
	// TODO Put to Lazy after having the hibernate session global
	@OneToMany(mappedBy = "sub", fetch = FetchType.EAGER)
	protected transient List<Team> subTeams;

	@ManyToMany//(cascade = CascadeType.ALL)
	@JoinTable(name = "team_players",
			joinColumns = @JoinColumn(name = "player_id"),
			inverseJoinColumns = @JoinColumn(name = "team_id")
	)
	protected transient List<Team> teamList;
	
	@Autowired
	public ERPlayer() {
		
	}

	public String getDiscordName() {
		return discordName;
	}

	public void setDiscordName(String discordName) {
		this.discordName = discordName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getMmr() {
		return mmr;
	}

	public void setMmr(Integer mmr) {
		this.mmr = mmr;
	}

	public Integer getGlobalRank() {
		return globalRank;
	}

	public void setGlobalRank(Integer globalRank) {
		this.globalRank = globalRank;
	}

	public LocalDateTime getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getDak() {
		return dak;
	}

	public void setDak(String dak) {
		this.dak = dak;
	}

	public List<HistoryName> getHistoryPlayerNameList() {
		return historyPlayerNameList;
	}

	public void setHistoryPlayerNameList(List<HistoryName> historyPlayerNameList) {
		this.historyPlayerNameList = historyPlayerNameList;
	}
	
	public List<Team> getCaptainTeams() {
		return captainTeams;
	}

	public void setCaptainTeams(List<Team> captainTeams) {
		this.captainTeams = captainTeams;
	}

	public List<Team> getSubTeams() {
		return subTeams;
	}

	public void setSubTeams(List<Team> subTeams) {
		this.subTeams = subTeams;
	}

	public List<Team> getTeamList() {
		return teamList;
	}

	public void setTeamList(List<Team> teamList) {
		this.teamList = teamList;
	}
	
	/* ==============================================
	 * =============== PUBLIC METHODS ===============
	 * ==============================================
	 */
	
	/**
	 * Function to get the dak name out of the dak link
	 * @return	The name at the end of the dak link. Used with the ER API calls.
	 */
	public String getDakName() {
		return this.dak != null ? this.dak.split("/")[this.dak.split("/").length - 1] : null;
	}
	
	

	// TODO IN ERPlayerService
//	/**
//	 * Constructor of an ERPlayer
//	 * 
//	 * @param dakName	The DAK name of the player. Will also serve as the player name
//	 */
//	public static ERPlayer getERPlayer(String dakName) {
//		if (dakName != null) {
//			Bot.deserializePlayers();
//			
//			//Bot.allPlayers.stream().forEach(p -> System.err.println("player dakName: " + p.getDakName()));
//			if (Bot.allPlayers.stream().anyMatch(p -> p.getDakName().equalsIgnoreCase(dakName))) {
//				//System.err.println("Player already exists; getting back the properties...");
//				ERPlayer player = Bot.allPlayers.stream().filter(p -> p.getDakName().equalsIgnoreCase(dakName)).findFirst().orElse(null);
//				/*if (player != null)
//					player.updateMmr();*/
//				return player;
//			} else {
//				return new ERPlayer(dakName, "");
//			}
//		} else {
//			return null;
//		}
//	}
	
//	public static ERPlayer getERPlayerByDiscordName(String discordName) {
//		if (discordName != null) {
//			Bot.deserializePlayers();
//			
//			if (Bot.allPlayers.stream().anyMatch(p -> p.getDiscordName().equalsIgnoreCase(discordName))) {
//				//System.err.println("Player already exists; getting back the properties...");
//				ERPlayer player = Bot.allPlayers.stream().filter(p -> p.getDiscordName().equalsIgnoreCase(discordName)).findFirst().orElse(null);
//				/*if (player != null)
//					player.updateMmr();*/
//				return player;
//			} else {
//				return new ERPlayer(discordName, discordName);
//			}
//		} else
//			return null;
//	}
	

	
//	public static String getDiscordNameByDakName(String dakName) {
//		ERPlayer player = ERPlayer.getERPlayer(dakName);
//		if (player != null) {
//			return player.getDiscordName();
//		} else {
//			return null;
//		}
//	}
//	
//	/**
//	 * Constructor of an ERPlayer
//	 * 
//	 * @param dak			The DAK link of the player
//	 * @param discordName	The discordName of the player
//	 */
//	public ERPlayer(String dak, String discordName) {
//		System.err.println("New player ! ");
//		this.dak = dak;
//		this.discordName = discordName;
//		this.displayName = "";
//
//		this.mmr = 0;
//		this.rank = 0;
//		this.lastUpdateTime = null;
//		
//		this.historyPlayerName = new ArrayList<String>();
//		
//		Bot.allPlayers.add(this);
//		
//		Bot.serializePlayers();
//		
//		//this.updateMmr();
//	}
//
//	/**
//	 * Getter of discordName
//	 * @return	The discord name of this player
//	 */
//	public String getDiscordName() {
//		return this.discordName;
//	}
//
//	public String getDisplayName() {
//		if (this.displayName == null)
//			this.displayName = "";
//		return this.displayName;//.isEmpty() ? (this.discordName == null || this.discordName.isEmpty() ? this.getDakName() : this.discordName) : this.displayName;
//	}
//	
//	/**
//	 * Getter of MMR
//	 * @return	The MMR of this player
//	 */
//	public int getMmr() {
//		return this.mmr;
//	}
//
//	/**
//	 * Getter of dak
//	 * @return	The DAK link of this player
//	 */
//	public String getDak() {
//		return this.dak;
//	}
//	
//	/**
//	 * Getter of rank
//	 * @return	The rank of this player
//	 */
//	public int getRankGlobal() {
//		return this.rank;
//	}
//	
//	/**
//	 * Getter of the last game registered of this player (used for knowing the time they played it mostly)
//	 * @return	The last game registered of this player
//	 */
//	public GameLog getLastGame() {
//		List<GameLog> games = this.getAllGames();
//		if (games.size() > 0) {
//			return games.get(0);
//		} else
//			return null;
//	}
//	
//	/**
//	 * Retrieves all the games of the player done in Lone Wolf mode
//	 * @return		List of all the games of the player done in Lone Wolf mode
//	 */
//	public List<GameLog> getAllLoneWolfGames() {
//		return this.getAllGames(MatchingMode.LONE_WOLF);
//	}
//
//	/**
//	 * Retrieves all the games of the player done in Normal mode
//	 * @return		List of all the games of the player done in Normal mode
//	 */
//	public List<GameLog> getAllNormalGames() {
//		return this.getAllGames(MatchingMode.NORMAL);
//	}
//
//	/**
//	 * Retrieves all the games of the player done in Ranked mode
//	 * @return		List of all the games of the player done in Ranked mode
//	 */
//	public List<GameLog> getAllRankedGames() {
//		return this.getAllGames(MatchingMode.RANKED);
//	}
//
//	/**
//	 * Retrieves all the games of the player done in Cobalt mode
//	 * @return		List of all the games of the player done in Cobalt mode
//	 */
//	public List<GameLog> getAllCobaltGames() {
//		return this.getAllGames(MatchingMode.COBALT);
//	}
//
//	/**
//	 * Retrieves all the games of the player done in Union mode
//	 * @return		List of all the games of the player done in Union mode
//	 */
//	public List<GameLog> getAllUnionGames() {
//		return this.getAllGames(MatchingMode.UNION);
//	}
//	
//	/**
//	 * Will return only the games corresponding to the matching mode in parameter if there is one, else will return all the games of the player
//	 * @param matchingMode	The matching mode to match in the GameLog
//	 * @return		The games corresponding to the matching mode in parameter if there is one, else all the games of the player
//	 */
//	public List<GameLog> getAllGames(MatchingMode matchingMode) {
//		if (matchingMode != null) {
//			return this.getAllGames().stream().filter(gl -> gl.getMatchingMode().equals(matchingMode)).toList();
//		} else {
//			return this.getAllGames();
//		}
//	}
//
//	/**
//	 * Getter of games
//	 * @return	The list of all the games registered of this player
//	 */
//	public List<GameLog> getAllGames() {
//		List<GameLog> gameLogList = new ArrayList<>();
//		Bot.deserializeGameLog();
//		if (this.historyPlayerName != null) {
//			gameLogList.addAll(Bot.games.stream().filter(gl -> this.historyPlayerName.stream().anyMatch(gl.nickname::equalsIgnoreCase) || gl.nickname.equalsIgnoreCase(this.getDakName())).toList());
//		}
//		
//		return gameLogList;
//	}
//	
//	/**
//	 * Getter of games
//	 * @return	The list of all the games registered of this player
//	 */
//	public List<GameLog> retrieveAndGetAllGames() {
//		List<GameLog> gameLogList = new ArrayList<>();
//		try {
//			GetPlayerStats.retrieveGames(discordName);
//			Bot.deserializeGameLog();
//			gameLogList.addAll(Bot.games.stream().filter(gl -> this.historyPlayerName.stream().anyMatch(gl.nickname::equalsIgnoreCase) || gl.nickname.equalsIgnoreCase(this.getDakName())).toList());
//		} catch (UnirestException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return gameLogList;
//	}
//	
//	/**
//	 * Setter of discordName
//	 * @param discordName	The new discord name for this player
//	 */
//	public void setDiscordName(String discordName) {
//		this.discordName = discordName;
//		Bot.serializePlayers();
//	}
//
//	public void setDisplayName(String displayName) {
//		this.displayName = displayName;
//		Bot.serializePlayers();
//	}
//		
//	public String getUserId() {
//		// Retrieves the userId if null
//		if (this.userId == null) {
//			this.userId = GetPlayerStats.getUserId(this.getDakName());
//		}
//		return userId;
//	}
//
//	public void setUserId(String userId) {
//		this.userId = userId;
//	}
//
//	public void addHistoryPlayerName(String name) {
//		this.historyPlayerName.add(name);
//	}
//	
//	public void addAllHistoryPlayerName(List<String> names) {
//		this.historyPlayerName.addAll(names);
//	}
//	
//	public List<String> getHistoryPlayerName() {
//		return this.historyPlayerName;
//	}
//	
//	/**
//	 * Setter of MMR
//	 * @param name	The new MMR for this player
//	 */
//	protected void setMmr(int mmr) {
//		this.lastUpdateTime = LocalDateTime.now();
//		this.mmr = mmr;
//	}
//
//	/**
//	 * Setter of dak
//	 * @param name	The new DAK link for this player
//	 */
//	public void setDak(String dak) {
//		if (!this.dak.equalsIgnoreCase(dak)) {
//			this.dak = dak;
//			this.lastUpdateTime = null;
//			this.updateMmr();
//		}
//	}
//	
//	/**
//	 * Function to get the dak name out of the dak link
//	 * @return	The name at the end of the dak link. Used with the ER API calls.
//	 */
//	public String getDakName() {
//		return this.dak.split("/")[this.dak.split("/").length - 1];
//	}
//	
//	/**
//	 * Function allowing this player to update their MMR if the last update was more than an hour ago
//	 */
//	public void updateMmr() {
//		LocalDateTime date = LocalDateTime.now();
//		//System.out.println("Date: " + date + "Last: " + this.lastUpdateTime + " -1: " + date.minusHours(1) + "; Comparaison: " + date.minusHours(1).isAfter(this.lastUpdateTime));
//		if (this.lastUpdateTime == null || date.minusSeconds(1).isAfter(this.lastUpdateTime)) {
//			this.updateMmrForce();
//		}
//	}
//
//	/**
//	 * Function forcing this player to update their MMR
//	 */
//	public void updateMmrForce() {
//		this.lastUpdateTime = LocalDateTime.now();
//		JSONObject userRank = null;
//		
//		if (this.getUserId() != null) {
//			try {
//				userRank = GetPlayerStats.getPlayerStatsByUserId(this.getUserId());
//			} catch (UserIdNotLinkedException e) {
//				e.printStackTrace();
//			}
//		}
//		
//		// If the userRank couldn't be retrieved with the userId
//		if (userRank == null) {
//			userRank = GetPlayerStats.getPlayerStats(this);
//		}
//		
//		if (userRank != null) {
//			this.setMmr(userRank.getInt("mmr"));
//			this.rank = userRank.getInt("rank");
//		} else {
//			System.err.println("The JSONResponse is null, setting MMR to 0 for " + this.getDakName());
//			this.setMmr(0);
//			this.rank = 0;
//			this.lastUpdateTime = null;
//		}
//		
//		System.err.println("Serialization players...");
//		Bot.serializePlayers();
//	}
//
//	/**
//	 * Check if a player (by player name) is already registered in this channel (== scrim)
//	 * @param name			The player name
//	 * @param channelName	The channel name (== scrim name)
//	 * @return				true if the player already is registered for this scrim, false if not
//	 */
//	public static boolean alreadyRegistered(String name, String discordServerName, String channelName) {
//		Scrim scrim = Bot.getScrim(discordServerName, channelName);
//		if (scrim != null)
//			return scrim.getTeams().stream().anyMatch(team -> team.getPlayerNames().stream().anyMatch(pName -> pName.equalsIgnoreCase(name)) || (team.getSub() != null && team.getSub().equalsIgnoreCase(name)));
//		return false;
//	}
//
//	/**
//	 * Overrides the .equals method so that a ERPlayer equals another ERPlayer if their name is the same
//	 */
//	@Override
//	public boolean equals(Object o) {
//		if (o != null && o.getClass().isAssignableFrom(ERPlayer.class)) {
//			ERPlayer p = (ERPlayer)o;
//			
//			return this.getDiscordName().equalsIgnoreCase(p.getDiscordName());
//		}
//		
//		return false;
//	}
//	
//	/**
//	 * Copy the player from another player. Used when the player we are trying to create already exists.
//	 * @param player	The player to copy
//	 */
//	protected void copy(ERPlayer player) {
//		this.discordName = player.discordName;
//		this.displayName = player.displayName;
//		this.dak = player.dak;
//		this.mmr = player.mmr;
//		this.lastUpdateTime = player.lastUpdateTime;
//		this.rank = player.rank;
//	}
//	
//	@Override
//	public String toString() {
//		return this.discordName;
//	}
//	
}
