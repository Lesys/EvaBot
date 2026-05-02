package zzc.discord.evabot.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScrimDTO extends AbstractDTO {
	protected Date creationTime;

	protected Date modificationTime;
	
	/**
	 * The Discord server name aka. guild name
	 */
	private String discordServerName;
	
	/**
	 * Name of the Discord channel where the registration are done
	 */
	private String channelName;

	private String channelId;
	
	private List<TeamDTO> teamList;
	
	private List<SpectatorDTO> spectatorList;
	
	private List<MessageLogDTO> messageLogList;
	
	public ScrimDTO() {
		this.teamList = new ArrayList<TeamDTO>();
		this.messageLogList = new ArrayList<MessageLogDTO>();
		this.spectatorList = new ArrayList<SpectatorDTO>();
	}
	
	/**
	 * Constructor of Scrim
	 * @param discordServerName		Name of the Discord server where the channel is
	 * @param channelName			Name of the channel where the commands occur
	 */
	public ScrimDTO(String discordServerName, String channelName) {
		this();
		this.discordServerName = discordServerName;
		this.channelName = channelName;
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

	public String getDiscordServerName() {
		return discordServerName;
	}

	public void setDiscordServerName(String discordServerName) {
		this.discordServerName = discordServerName;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public List<TeamDTO> getTeamList() {
		return teamList;
	}

	public void setTeamList(List<TeamDTO> teamList) {
		this.teamList = teamList;
	}

	public List<SpectatorDTO> getSpectatorList() {
		return spectatorList;
	}

	public void setSpectatorList(List<SpectatorDTO> spectatorList) {
		this.spectatorList = spectatorList;
	}

	public List<MessageLogDTO> getMessageLogList() {
		return messageLogList;
	}

	public void setMessageLogList(List<MessageLogDTO> messageLogList) {
		this.messageLogList = messageLogList;
	}

	
	/* ==============================================
	 * =============== PUBLIC METHODS ===============
	 * ==============================================
	 */
	
	public TeamDTO getTeam(String teamName) {
		return this.getTeamList().stream().filter(team -> team.getName().equalsIgnoreCase(teamName)).findFirst().orElse(null);
	}

	/**
	 * Check if a player (by player name) is already registered in this scrim
	 * @param name			The player name
	 * @return				true if the player already is registered for this scrim, false if not
	 */
	public boolean alreadyRegistered(String name) {
		return this.getTeamList().stream().anyMatch(team -> team.getPlayerList().stream().anyMatch(pName -> pName.getDakName().equalsIgnoreCase(name)) || (team.getSub() != null && team.getSub().getDakName().equalsIgnoreCase(name)));
	}
}
