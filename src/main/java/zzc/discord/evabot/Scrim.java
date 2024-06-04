package zzc.discord.evabot;

import java.io.Serializable;
import java.util.*;

public class Scrim implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7490045532864113108L;

	private String channelName;
	
	private List<Team> teams;

	private List<MessageLog> logs;
	
	public Scrim(String channelName) {
		this.channelName = channelName;
		this.teams = new ArrayList<Team>();
		this.logs = new ArrayList<MessageLog>();
	}
	
	public String getName() {
		return this.channelName;
	}
	
	public List<Team> getTeams() {
		return this.teams;
	}
	
	public List<MessageLog> getLogs() {
		if (this.logs == null)
			this.logs = new ArrayList<MessageLog>();
		return this.logs;
	}
	
	public void addTeam(Team team) {
		this.teams.add(team);
	}
	
	/**
	 * Adds the MessageLog as the first one to order them by most recent
	 * @param log	The last Message sent that would affect the Scrim
	 */
	public void addLogs(MessageLog log) {
		if (this.logs == null)
			this.logs = new ArrayList<MessageLog>();
		this.logs.add(log);
	}

	/**
	 * Overrides the .equals method so that a Scrim equals another Scrim if their name is the same
	 */
	@Override
	public boolean equals(Object o) {
		if (o != null && o.getClass().isAssignableFrom(Scrim.class)) {
			Scrim s = (Scrim)o;
			
			return this.getName().equalsIgnoreCase(s.getName());
		}
		
		return false;
	}

}
