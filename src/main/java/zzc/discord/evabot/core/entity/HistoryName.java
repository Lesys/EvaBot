package zzc.discord.evabot.core.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "history_name")
public class HistoryName extends AbstractEntity {	
	protected String nickname;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "player_id")
	protected ERPlayer player;
	
	public HistoryName() {
		
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public ERPlayer getPlayer() {
		return player;
	}

	public void setPlayer(ERPlayer player) {
		this.player = player;
	}	
}
