package zzc.discord.evabot.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import zzc.discord.evabot.util.Constant;
import zzc.discord.evabot.util.enumeration.Priority;

public class TeamDTO extends AbstractDTO {
	protected Date creationTime;

	protected Date modificationTime;
	
	protected ERPlayerDTO captain;
	
	protected ERPlayerDTO sub;
	
	/**
	 * The list of the player discord names in the Team
	 */
	protected List<ERPlayerDTO> playerList;
	
	/**
	 * The name of the Team
	 */
	protected String name;
	
	/**
	 * Priority of the team. Negative is low, 0 is neutral, positive is high
	 */
	protected Priority priority;

	protected Integer scrim_id;
	
	public TeamDTO() {
		this.playerList = new ArrayList<>();
		this.priority = Priority.NEUTRAL;		
	}
	
	/**
	 * Constructor of Team
	 * @param name	The name of the Team
	 */
	public TeamDTO(String name) {
		this();
		this.name = name;
	}	

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public Date getModificationTime() {
		return modificationTime;
	}

	public void setModificationTime(Date modificationTime) {
		this.modificationTime = modificationTime;
	}

	public ERPlayerDTO getCaptain() {
		return captain;
	}

	public void setCaptain(ERPlayerDTO captain) {
		this.captain = captain;
	}

	public ERPlayerDTO getSub() {
		return sub;
	}

	public void setSub(ERPlayerDTO sub) {
		this.sub = sub;
	}

	public List<ERPlayerDTO> getPlayerList() {
		return playerList;
	}

	public void setPlayerList(List<ERPlayerDTO> playerList) {
		this.playerList = playerList;
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

	public Integer getScrim_id() {
		return scrim_id;
	}

	public void setScrim_id(Integer scrim_id) {
		this.scrim_id = scrim_id;
	}


	
	/* ==============================================
	 * =============== PUBLIC METHODS ===============
	 * ==============================================
	 */	

	/**
	 * Function to add an ERPlayer to the Team. Can not add if the player count is at maximum
	 * @param player	The new player to add to this Team
	 * 
	 * @return	true if the player has been correctly added, false if not
	 */
	public boolean addPlayer(ERPlayerDTO player) {
		if (this.getPlayerList().size() < Constant.MAX_NB_PLAYER_PER_TEAM)
			return this.getPlayerList().add(player);
		return false;
	}
	
	/**
	 * Function to add a list of ERPlayer to the Team
	 * @param players	The list of players to add to this Team
	 * 
	 * @return	true if the players have been correctly added, false if not
	 */
	public boolean addPlayers(List<ERPlayerDTO> players) {
		if (this.getPlayerList().size() + players.size() <= Constant.MAX_NB_PLAYER_PER_TEAM)
			return this.getPlayerList().addAll(players);
		return false;
	}

	/**
	 * Adds the sub to the team by removing it from the team standard players if needed
	 * @param name	The new sub player for this Team
	 * 
	 * @return	true
	 */
	public boolean setSubPlayer(ERPlayerDTO sub) {
		if (sub != null) {
			this.sub = sub;
			this.getPlayerList().remove(sub);
		} else {
			this.sub = null;
		}
		return true;
	}
	
	/**
	 * Returns the list of all the not null players from the team main roster + sub   
	 * @return		List of players with the main roster and the sub player
	 */
	public List<ERPlayerDTO> getFullPlayerList() {
		List<ERPlayerDTO> list = new ArrayList<>();
		
		list.addAll(this.getPlayerList());
		list.add(this.getSub());
		
		return list.stream().filter(player -> Objects.nonNull(player)).toList();
	}
	
	public boolean removePlayer(ERPlayerDTO player) {
		if (player == null || !this.getFullPlayerList().contains(player)) return false;
		
		if (this.getPlayerList().contains(player)) {
			return this.getPlayerList().remove(player);
		} else if (this.getSub().equals(player)) {
			this.setSubPlayer(null);
			
			return true;
		}
		
		return false;
	}
	
	public ERPlayerDTO getPlayerByDiscordName(String discordName) {
		return this.getFullPlayerList().stream().filter(player -> player.getDiscordName().equalsIgnoreCase(discordName)).findFirst().orElse(null);
	}
	
	public ERPlayerDTO getPlayerByDakName(String dakName) {
		return this.getFullPlayerList().stream().filter(player -> player.getDakName().equalsIgnoreCase(dakName)).findFirst().orElse(null);
	}
	
}
