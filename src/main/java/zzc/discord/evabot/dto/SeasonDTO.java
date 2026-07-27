package zzc.discord.evabot.dto;

import java.util.Date;

public class SeasonDTO extends AbstractDTO {
	protected Integer activeSeasonId;

	protected Integer realSeasonId;
	
	protected Date lastDayUpdate;

	public Integer getActiveSeasonId() {
		return activeSeasonId;
	}

	public void setActiveSeasonId(Integer activeSeasonId) {
		this.activeSeasonId = activeSeasonId;
	}

	public Integer getRealSeasonId() {
		return realSeasonId;
	}

	public void setRealSeasonId(Integer realSeasonId) {
		this.realSeasonId = realSeasonId;
	}

	public Date getLastDayUpdate() {
		return lastDayUpdate;
	}

	public void setLastDayUpdate(Date lastDayUpdate) {
		this.lastDayUpdate = lastDayUpdate;
	}
		
	
	/* ==============================================
	 * =============== PUBLIC METHODS ===============
	 * ==============================================
	 */
	
}
