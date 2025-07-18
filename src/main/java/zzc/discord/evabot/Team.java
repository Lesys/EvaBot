package zzc.discord.evabot;

import java.io.Serializable;
import java.util.*;

/**
 * 
 * @author Lesys
 *
 * Class representing a Team of ERPlayer
 */
public class Team implements Serializable, Comparable<Team> {
	public static int MAX_NB_PLAYER = 3;
	/**
	 * 
	 */
	private static final long serialVersionUID = -7721800394853051024L;

	/**
	 * The captain of the Team, the only one who will be able to use commands to change the team composition
	 */
	protected String captain;
	
	/**
	 * The list of the player discord names in the Team
	 */
	protected List<String> playerNames;
	
	/**
	 * The sub of the Team, can be null
	 */
	protected String sub;
	
	/**
	 * The name of the Team
	 */
	protected String name;
	
	/**
	 * Priority of the team. Negative is low, 0 is neutral, positive is high
	 */
	protected Priority priority;
	
	/**
	 * Constructor of Team
	 * @param name	The name of the Team
	 */
	public Team(String name) {
		this.name = name;
		this.playerNames = new ArrayList<String>();
		this.sub = null;
		this.priority = Priority.NEUTRAL;
	}
	
	/**
	 * Getter of captain
	 * @return	The String captain of this Team
	 */
	public String getCaptain() {
		return this.captain;
	}
	
	/**
	 * Getter of players
	 * @return	The list of players in this Team
	 */
	/* NOT FUNCTIONAL ANYMORE
	public List<ERPlayer> getPlayers() {
		return this.playerNames.stream().map(discordName -> ERPlayer.getERPlayerByDiscordName(discordName)).toList();
	}*/

	/**
	 * Getter of playerNames
	 * @return		The list of the names of all the main players in this Team
	 */
	public List<String> getPlayerNames() {
		return this.playerNames;
	}
	
	/**
	 * Getter of name
	 * @return	The name of this Team
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Getter of sub
	 * @return	The sub player in this Team
	 */
	public String getSub() {
		return this.sub;
	}
	
	public Priority getPriority() {
		return this.priority;
	}
	
	/**
	 * Setter of captain
	 * @param captain	The new User captain of this Team
	 */
	public void setCaptain(String captain) {
		this.captain = captain;
	}
	
	/**
	 * Setter of name
	 * @param name	The new name of this Team
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Setter of sub
	 * @param name	The new sub player for this Team
	 * 
	 * @return	true if the sub has been correctly set, false if not
	 */
	public boolean setSub(ERPlayer sub) {
		if (sub != null) {
			this.sub = sub.getDakName();
			this.playerNames.remove(sub.getDakName());
		} else {
			this.sub = null;
		}
		return true;
	}
	
	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	/**
	 * Function to add an ERPlayer to the Team. Can not add if the player count is at maximum
	 * @param player	The new player to add to this Team
	 * 
	 * @return	true if the player has been correctly added, false if not
	 */
	public boolean addPlayer(ERPlayer player) {
		if (this.playerNames.size() < Team.MAX_NB_PLAYER)
			return this.playerNames.add(player.getDakName());
		return false;
	}
	
	/**
	 * Function to add a list of ERPlayer to the Team
	 * @param players	The list of players to add to this Team
	 * 
	 * @return	true if the players have been correctly added, false if not
	 */
	public boolean addPlayers(List<ERPlayer> players) {
		if (this.playerNames.size() + players.size() <= Team.MAX_NB_PLAYER)
			return this.playerNames.addAll(players.stream().map(player -> player.getDakName()).toList());
		return false;
	}
	
	/**
	 * Function to get the average MMR of the Team, based on the MMR of each players (without sub) in this Team
	 * @return	The average MMR of this Team
	 */
	public Double getAverage() {
		return Math.floor((this.getPlayerNames().stream().map(p -> ERPlayer.getERPlayer(p)).map(p -> p.getMmr()).reduce(0, (x, y) -> x + y).doubleValue() / this.playerNames.size()) * 100) / 100;
	}

	/**
	 * Updates the MMR of all players in this Team
	 */
	public void updateMmr() {
		this.getPlayerNames().forEach(p -> ERPlayer.getERPlayer(p).updateMmr());
		if (sub != null)
			ERPlayer.getERPlayer(sub).updateMmr();
	}

	/**
	 * Force update the MMR of all players in this Team
	 */
	public void updateMmrForce() {
		this.getPlayerNames().forEach(p -> ERPlayer.getERPlayer(p).updateMmrForce());
		if (sub != null)
			ERPlayer.getERPlayer(sub).updateMmrForce();
	}
	
	/**
	 * Removes a player from this Team. Does work if the player is the sub
	 * @param name	The name of the player to remove from this Team
	 * @return	true if the player has correctly been removed from this Team, false if no player were found
	 */
	public boolean removePlayer(String name) {
		return this.playerNames.removeIf(p -> p.equalsIgnoreCase(name)) ? true : this.getSub() != null && this.getSub().equalsIgnoreCase(name) ? this.setSub(null) : false;
	}
	
	public boolean removePlayer(ERPlayer player) {
		String name = player.getDakName();
		if (name != null) {
			return this.playerNames.removeIf(p -> p.equalsIgnoreCase(name)) ? true : this.getSub() != null && this.getSub().equalsIgnoreCase(name) ? this.setSub(null) : false;
		}
		
		return false;
	}

	/**
	 * Makes this Team comparable to another Team, based on the priority and then MMR average
	 */
	@Override
	public int compareTo(Team o) {
		return this.getPriority().equals(o.getPriority()) ?
				-this.getAverage().compareTo(o.getAverage()) : this.getPriority().compareTo(o.getPriority());
	}
	
	/**
	 * Overrides the .equals method so that a Team equals another Team if their name is the same
	 */
	@Override
	public boolean equals(Object o) {
		if (o != null && o.getClass().isAssignableFrom(Team.class)) {
			Team t = (Team)o;
			
			return this.getName().equalsIgnoreCase(t.getName());
		}
		
		return false;
	}
	
}
