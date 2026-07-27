package zzc.discord.evabot.core.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Season extends AbstractEntity {
	@Column(name = "active_season_id")
	protected Integer activeSeasonId;

	@Column(name = "real_season_id")
	protected Integer realSeasonId;
	
	@Column(name = "last_day_update")
	protected Date lastDayUpdated;

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

	public Date getLastDayUpdated() {
		return lastDayUpdated;
	}

	public void setLastDayUpdated(Date lastDayUpdated) {
		this.lastDayUpdated = lastDayUpdated;
	}
}
