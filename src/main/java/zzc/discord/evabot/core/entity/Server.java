package zzc.discord.evabot.core.entity;

import java.util.*;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * 
 * @author Lesys
 * 
 * Class representing the server hosting the scrims of Eternal Return
 */
@Entity
@Table(name = "server")
public class Server extends AbstractEntity {	
	/**
	 * The Discord server name aka. guild name
	 */
	private String name;
	
	
	@Column(name = "server_id")
	private String serverId;

	@OneToMany(mappedBy = "server", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<HelperRole> helperRole;
	
	public Server() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public List<HelperRole> getHelperRole() {
		return helperRole;
	}

	public void setHelperRole(List<HelperRole> helperRole) {
		this.helperRole = helperRole;
	}
	
	public void addHelperRole(HelperRole helperRole) {
		this.helperRole.add(helperRole);
	}

	/**
	 * Overrides the .equals method so that a Server equals another Server if their string ID is the same
	 */
	@Override
	public boolean equals(Object o) {
		if (o != null && o.getClass().isAssignableFrom(Server.class)) {
			Server s = (Server)o;
			
			return this.getServerId() != null && this.getServerId().equalsIgnoreCase(s.getServerId());
		}
		
		return false;
	}

}
