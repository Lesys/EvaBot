package zzc.discord.evabot.core.entity;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

//import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import zzc.discord.evabot.util.enumeration.Priority;

/**
 * 
 * @author Lesys
 *
 * Class representing a Team of ERPlayer
 */
@Entity
@Table(name = "team")
public class Team extends AbstractEntity implements Comparable<Team> {
	/**
	 * The captain of the Team, the only one who will be able to use commands to change the team composition
	 */
	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "captain_id")
	protected ERPlayer captain;

	/**
	 * The sub of the Team if there is one
	 */
	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sub_id")
	protected ERPlayer sub;
	
	/**
	 * The list of the player discord names in the Team
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "team_players",
			joinColumns = @JoinColumn(name = "team_id"),
			inverseJoinColumns = @JoinColumn(name = "player_id")
	)
	protected List<ERPlayer> playerList;
	
	/**
	 * The name of the Team
	 */
	protected String name;
	
	/**
	 * Priority of the team. Negative is low, 0 is neutral, positive is high
	 */
	protected Priority priority;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "scrim_id")
	protected Scrim scrim;
	
	public Team() {	
	}

	public ERPlayer getCaptain() {
		return captain;
	}

	public void setCaptain(ERPlayer captain) {
		this.captain = captain;
	}

	public ERPlayer getSub() {
		return sub;
	}

	public void setSub(ERPlayer sub) {
		this.sub = sub;
	}

	public List<ERPlayer> getPlayerList() {
		return playerList;
	}

	public void setPlayerList(List<ERPlayer> playerNames) {
		this.playerList = playerNames;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public Scrim getScrim() {
		return scrim;
	}

	public void setScrim(Scrim scrim) {
		this.scrim = scrim;
	}
	
	/**
	 * Function to get the average MMR of the Team, based on the MMR of each players (without sub) in this Team
	 * @return	The average MMR of this Team
	 */
	public Double getAverage() {
		return Math.floor((this.getPlayerList().stream().map(p -> p.getMmr()).reduce(0, (x, y) -> x + y).doubleValue() / this.playerList.size()) * 100) / 100;
	}
//
//	/**
//	 * Updates the MMR of all players in this Team
//	 */
//	public void updateMmr() {
//		this.getPlayerNames().forEach(p -> ERPlayer.getERPlayer(p).updateMmr());
//		if (sub != null)
//			ERPlayer.getERPlayer(sub).updateMmr();
//	}
//
//	/**
//	 * Force update the MMR of all players in this Team
//	 */
//	public void updateMmrForce() {
//		this.getPlayerNames().forEach(p -> ERPlayer.getERPlayer(p).updateMmrForce());
//		if (sub != null)
//			ERPlayer.getERPlayer(sub).updateMmrForce();
//	}
	
//	/**
//	 * Removes a player from this Team. Does work if the player is the sub
//	 * @param name	The name of the player to remove from this Team
//	 * @return	true if the player has correctly been removed from this Team, false if no player were found
//	 */
//	public boolean removePlayer(String name) {
//		return this.playerNames.removeIf(p -> p.equalsIgnoreCase(name)) ? true : this.getSub() != null && this.getSub().equalsIgnoreCase(name) ? this.setSub(null) : false;
//	}
//	
//	public boolean removePlayer(ERPlayer player) {
//		String name = player.getDakName();
//		if (name != null) {
//			return this.playerNames.removeIf(p -> p.equalsIgnoreCase(name)) ? true : this.getSub() != null && this.getSub().equalsIgnoreCase(name) ? this.setSub(null) : false;
//		}
//		
//		return false;
//	}

	/**
	 * Makes this Team comparable to another Team, based on the priority and then MMR average
	 */
	@Override
	public int compareTo(Team o) {
		return this.getPriority().equals(o.getPriority()) ? -this.getAverage().compareTo(o.getAverage()) : this.getPriority().compareTo(o.getPriority());
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
