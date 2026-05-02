package zzc.discord.evabot.dto;

import java.util.Date;

public class HistoryNameDTO extends AbstractDTO {	
	protected Date creationTime;

	protected Date modificationTime;
	
	protected String nickname;
	
	protected Integer player_id;
	
	public HistoryNameDTO() {
		
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

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Integer getPlayer_id() {
		return player_id;
	}

	public void setPlayer_id(Integer player_id) {
		this.player_id = player_id;
	}
}