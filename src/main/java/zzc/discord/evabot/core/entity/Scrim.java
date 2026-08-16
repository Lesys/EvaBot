package zzc.discord.evabot.core.entity;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * 
 * @author Lesys
 * 
 * Class representing a scrim of Eternal Return
 */
@Entity
@Table(name = "scrim")
public class Scrim extends AbstractEntity {	
	/**
	 * The Discord server aka. guild
	 */
	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "server_id")
	protected Server server;
	
	/**
	 * Name of the Discord channel where the registration are done
	 */
	@Column(name = "channel_name")
	private String channelName;
	
	/**
	 * ID of the Discord channel where the registration are done
	 */
	@Column(name = "channel_id")
	private String channelId;
	
	/**
	 * Teams registered for this scrim, by order of register time
	 */
	@OneToMany(mappedBy = "scrim", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<Team> teams;

	/**
	 * Spectators willing to spectate for this scrim
	 */
	@OneToMany(mappedBy = "scrim", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<Spectator> spectators;

	/**
	 * List of command logs for this scrim. Every change about the scrim (mostly Teams) is registered
	 */
	@OneToMany(mappedBy = "scrim", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<MessageLog> messageLogs;
	
	public Scrim() {
		
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
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

	public List<Team> getTeams() {
		return teams;
	}

	public void setTeams(List<Team> teams) {
		this.teams = teams;
	}

	public List<Spectator> getSpectators() {
		return spectators;
	}

	public void setSpectators(List<Spectator> spectators) {
		this.spectators = spectators;
	}

	public List<MessageLog> getMessageLogs() {
		return messageLogs;
	}

	public void setMessageLogs(List<MessageLog> logs) {
		this.messageLogs = logs;
	}

	/**
	 * Adds a Team to the registered Teams for this Scrim
	 * @param team		The Team to add
	 */
	public void addTeam(Team team) {
		this.teams.add(team);
	}

	/**
	 * Adds a Spectator to the list of spectating people for this Scrim
	 * @param spectator		The Spectator to add
	 */
	public void addSpectators(Spectator spectator) {
		this.spectators.add(spectator);
	}
	
	/**
	 * Adds the MessageLog as the first one to order them by most recent
	 * @param log	The last Message sent that would affect the Scrim
	 */
	public void addLogs(MessageLog log) {
		if (this.messageLogs == null)
			this.messageLogs = new ArrayList<MessageLog>();
		this.messageLogs.add(log);
	}

	/**
	 * Overrides the .equals method so that a Scrim equals another Scrim if their name is the same in the same server
	 */
	@Override
	public boolean equals(Object o) {
		if (o != null && o.getClass().isAssignableFrom(Scrim.class)) {
			Scrim s = (Scrim)o;
			
			return this.getServer() != null && this.getServer().getServerId() != null && s.getServer() != null && this.getServer().getServerId().equalsIgnoreCase(s.getServer().getServerId())
					&& this.getChannelName() != null && this.getChannelName().equalsIgnoreCase(s.getChannelName());
		}
		
		return false;
	}

}
