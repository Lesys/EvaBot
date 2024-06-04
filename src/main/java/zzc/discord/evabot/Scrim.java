package zzc.discord.evabot;

import java.io.Serializable;
import java.util.*;

/**
 * 
 * @author Lesys
 * 
 * Class representing a scrim of Eternal Return
 */
public class Scrim implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7490045532864113108L;

	/**
	 * The Discord server name aka. guild name
	 */
	private String discordServerName;
	
	/**
	 * Name of the Discord channel where the registration are done
	 */
	private String channelName;
	
	/**
	 * Teams registered for this scrim, by order of register time
	 */
	private List<Team> teams;

	/**
	 * List of command logs for this scrim. Every change about the scrim (mostly Teams) is registered
	 */
	private List<MessageLog> logs;
	
	/**
	 * Constructor of Scrim
	 * @param discordServerName		Name of the Discord server where the channel is
	 * @param channelName			Name of the channel where the commands occur
	 */
	public Scrim(String discordServerName, String channelName) {
		this.discordServerName = discordServerName;
		this.channelName = channelName;
		this.teams = new ArrayList<Team>();
		this.logs = new ArrayList<MessageLog>();
	}
	
	/**
	 * Getter of discordServerName
	 * @return		Name of the Discord server
	 */
	public String getDiscordServerName() {
		return this.discordServerName;
	}
	
	/**
	 * Getter of channelName
	 * @return		The channel name of the Scrim registrations
	 */
	public String getName() {
		return this.channelName;
	}
	
	/**
	 * Getter of teams
	 * @return		The list of the Teams registered for this Scrim
	 */
	public List<Team> getTeams() {
		return this.teams;
	}
	
	/**
	 * Getter of logs
	 * @return		The list of commands done since the creation of this Scrim
	 */
	public List<MessageLog> getLogs() {
		if (this.logs == null)
			this.logs = new ArrayList<MessageLog>();
		return this.logs;
	}
	
	/**
	 * Adds a Team to the registered Teams for this Scrim
	 * @param team		The Team to add
	 */
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
			
			return this.getDiscordServerName().equalsIgnoreCase(s.getDiscordServerName()) && this.getName().equalsIgnoreCase(s.getName());
		}
		
		return false;
	}

}
