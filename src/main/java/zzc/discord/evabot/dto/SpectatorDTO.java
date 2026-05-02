package zzc.discord.evabot.dto;

import java.util.Date;

public class SpectatorDTO extends AbstractDTO {
	protected Date creationTime;

	protected Date modificationTime;
	
	/**
	 * The spectator name
	 */
	protected String name;
	
	protected Integer scrim_id;
	
	public SpectatorDTO() {
		
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	
	
}
