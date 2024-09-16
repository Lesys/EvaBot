package zzc.discord.evabot;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.json.JSONObject;

/**
 * 
 * @author Lesys
 * 
 * Class representing a game of Eternal Return
 */
public class GameLog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6767589585609547345L;

	/**
	 * The nickname of the owner of the replay
	 */
	protected String nickname;
	
	/**
	 * The server where the game was played
	 */
	protected String server;
	
	/**
	 * The ID of the game (unique)
	 */
	protected long gameId;
	
	/**
	 * The string of the time when the game started (in the format yyyy-MM-dd'T'HH:mm:ss.SSSZ)
	 */
	protected String startTime;
	
	/**
	 * The ID of the season when the game started
	 */
	protected int seasonId;
	
	/**
	 * The team mates of the player for the game
	 */
	protected List<String> teammates;

	protected List<String> teammateCharacters;
	
	/**
	 * The final placement of the player for the game
	 */
	protected int placement;
	
	protected String characterPlayed;
	
	/**
	 * The team number of the player for the game
	 */
	protected int teamNumber;
	
	/**
	 * The total number of kills the player's team did for the game
	 */
	protected int teamKill;
	
	/**
	 * The MMR gained by the player (does not take entry cost in the total)
	 */
	protected int mmrGainInGame;
	
	/**
	 * Constructor of a GameLog
	 * 
	 * @param o		The JSONObject representing a GameLog after retrieving it from the ERAPI
	 */
	public GameLog(JSONObject o) {
		this.nickname = o.getString("nickname");
		this.server = o.getString("serverName");
		this.gameId = o.getLong("gameId");
		this.startTime = o.getString("startDtm");
		this.seasonId = o.getInt("seasonId");
		this.placement = o.getInt("gameRank");
		this.teamNumber = o.getInt("teamNumber");
		this.teamKill = o.getInt("teamKill");
		this.mmrGainInGame = o.getInt("mmrGainInGame");
		this.characterPlayed = GetPlayerStats.characters.get(o.getInt("characterNum"));
		this.teammates = new ArrayList<String>();
	}
	
	/**
	 * Getter of startTime but transformed
	 * @return		startTime but put in a LocalDateTime object
	 */
	public LocalDateTime getDateTime() {
		return GetPlayerStats.getLocalDateTime(this.startTime);
	}

	/**
	 * Getter of creationTime but formatted
	 * @return		creationTime with a specified format
	 */
	public String getDateTimeString() {
		return this.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MMMM-dd HH:mm:ss"));
	}
	
	/**
	 * Getter of nickname
	 * @return		The nickname of the owner of the replay
	 */
	public String getNickname() {
		return this.nickname;
	}
	
	/**
	 * Getter of server
	 * @return		The server of the game
	 */
	public String getServer() {
		return this.server;
	}
	
	/**
	 * Getter of gameId 
	 * @return		The ID of the game
	 */
	public long getGameId() {
		return this.gameId;
	}
	
	/**
	 * Getter of seasonId
	 * @return		The ID of the season
	 */
	public int getSeasonId() {
		return this.seasonId;
	}
	
	/**
	 * Getter of teammates
	 * @return		The List of the team mates name
	 */
	public List<String> getTeammates() {
		return this.teammates;
	}
	
	/**
	 * Getter of placement
	 * @return		The final placement of the player for the game
	 */
	public int getPlacement() {
		return this.placement;
	}
	
	public String getCharacterPlayed() {
		return this.characterPlayed;
	}
	
	/**
	 * Getter of teamNumber
	 * @return		The team number of the player for the game
	 */
	public int getTeamnumber() {
		return this.teamNumber;
	}
	
	/**
	 * Getter of teamKill
	 * @return		The total number of kills the player's team did for the game
	 */
	public int getTeamKill() {
		return this.teamKill;
	}
	
	/**
	 * Getter of mmrGainInGame
	 * @return		The MMR gained by the player for this game (without entry cost calculation)
	 */
	public int getMmrGainInGame() {
		return this.mmrGainInGame;
	}
	
	public void setCharacterPlayed(String characterPlayed) {
		this.characterPlayed = characterPlayed;
	}
	
	/**
	 * Adds a team mate name to the list for this game
	 * @param teammates		The name of the team mate to add
	 */
	public void addTeammantes(String teammates) {
		this.teammates.add(teammates);
	}
}
