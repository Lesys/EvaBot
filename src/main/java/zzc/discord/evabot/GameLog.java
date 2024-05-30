package zzc.discord.evabot;

import java.io.Serializable;
import java.time.LocalDateTime;

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
	 * Constructor of a GameLog
	 * 
	 * @param o		The JSONObject representing a GameLog after retrieving it from the ERAPI
	 */
	public GameLog(JSONObject o) {
		this.server = o.getString("serverName");
		this.gameId = o.getLong("gameId");
		this.startTime = o.getString("startDtm");
		this.seasonId = o.getInt("seasonId");
	}
	
	/**
	 * Getter of startTime but transformed
	 * @return		startTime but put in a LocalDateTime object
	 */
	public LocalDateTime getDateTime() {
		return GetPlayerStats.getLocalDateTime(this.startTime);
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
}
