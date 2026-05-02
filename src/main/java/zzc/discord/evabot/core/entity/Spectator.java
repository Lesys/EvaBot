package zzc.discord.evabot.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "spectator")
public class Spectator extends AbstractEntity {	
	protected String name;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "scrim_id")
	protected Scrim scrim;	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Scrim getScrim() {
		return scrim;
	}

	public void setScrim(Scrim scrim) {
		this.scrim = scrim;
	}
}
