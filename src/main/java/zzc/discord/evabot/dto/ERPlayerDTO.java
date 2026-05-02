package zzc.discord.evabot.dto;

import java.util.ArrayList;
import java.util.Date;
import java.time.LocalDateTime;
import java.util.List;

public class ERPlayerDTO extends AbstractDTO {
	protected Date creationTime;

	protected Date modificationTime;
	/**
	 * The discord name of the player
	 */
	protected String discordName;
	
	protected String displayName;
	
	/**
	 * The userId provided by the API to prevent it from making one every request
	 */
	protected String userId;
	
	/**
	 * The MMR of the player, the most uptodate
	 */
	protected Integer mmr;
	
	/**
	 * The global rank of the player, the most uptodate
	 */
	protected Integer globalRank;
	
	/**
	 * The last time the MMR was updated
	 */
	protected LocalDateTime lastUpdateTime;
	/**
	 * The DAK link of the player. The account name may differ from the player name
	 */
	protected String dak;
	
	protected List<HistoryNameDTO> historyPlayerNameList;
	
	protected List<Integer> captainTeams;
	
	protected List<Integer> subTeams;
	
	protected List<Integer> teamList;
	
	public ERPlayerDTO() {
		this.historyPlayerNameList = new ArrayList<>();
		this.captainTeams = new ArrayList<>();
		this.subTeams = new ArrayList<>();
		this.teamList = new ArrayList<>();		
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

	public List<HistoryNameDTO> getHistoryPlayerNameList() {
		return historyPlayerNameList;
	}

	public void setHistoryPlayerNameList(List<HistoryNameDTO> historyPlayerNameList) {		
		this.historyPlayerNameList = historyPlayerNameList;
	}
	
	public List<Integer> getCaptainTeams() {
		return captainTeams;
	}

	public void setCaptainTeams(List<Integer> captainTeams) {
		this.captainTeams = captainTeams;
	}

	public List<Integer> getSubTeams() {
		return subTeams;
	}

	public void setSubTeams(List<Integer> subTeams) {
		this.subTeams = subTeams;
	}

	public List<Integer> getTeamList() {
		return teamList;
	}

	public void setTeamList(List<Integer> teamList) {
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

	@Override
	public boolean equals(Object o) {
		if (o instanceof ERPlayerDTO) {
			ERPlayerDTO player = (ERPlayerDTO)o;
			return this.getId() != null && this.getId().equals(player.getId());
		}
		
		return false;
	}
	
}
