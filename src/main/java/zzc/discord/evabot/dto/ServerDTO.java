package zzc.discord.evabot.dto;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Role;
import zzc.discord.evabot.util.UtilBoolean;

public class ServerDTO extends AbstractDTO {
	/**
	 * The Discord server name aka. guild name
	 */
	private String name;
	
	private String serverId;
	
	private List<HelperRoleDTO> helperRoleList;
	
	public ServerDTO() {
		this.helperRoleList = new ArrayList<HelperRoleDTO>();
	}
	
	/**
	 * Constructor of Scrim
	 * @param name		Name of the Discord server where the channel is
	 * @param serverId	ID of the server where the commands occur
	 */
	public ServerDTO(String name, String serverId) {
		this();
		this.name = name;
		this.serverId = serverId;
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

	public List<HelperRoleDTO> getHelperRoleList() {
		return helperRoleList;
	}

	public void setHelperRoleList(List<HelperRoleDTO> helperRoleList) {
		this.helperRoleList = helperRoleList;
	}

	
	/* ==============================================
	 * =============== PUBLIC METHODS ===============
	 * ==============================================
	 */

	/**
	 * Check if a helper role is already registered in this server
	 * @param name			The helper role
	 * @return				true if the helper role already is registered this server, false if not
	 */
	public Boolean alreadyHelperRole(Role role) {
		if (role == null) return null;
		
		return this.getHelperRoleList().stream().anyMatch(helperRole -> helperRole.getEntityId().equals(role.getId()));
	}
	
	/**
	 * Adds the role to the list of helping roles for this server
	 * @param role
	 * @return		true if the role has been added, else false
	 */
	public boolean addHelperRole(Role role) {		
		if (UtilBoolean.isFalse(this.alreadyHelperRole(role))) {
			this.getHelperRoleList().add(new HelperRoleDTO(role));
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
}
