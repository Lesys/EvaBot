package zzc.discord.evabot.dto;

import net.dv8tion.jda.api.entities.Role;

public class HelperRoleDTO extends AbstractDTO {	
	protected String entityId;
	
	/**
	 * The helper role name
	 */
	protected String name;
	
	protected Integer server_id;
	
	public HelperRoleDTO() {
		
	}
	
	public HelperRoleDTO(Role role) {
		this();
		
		if (role != null) {
			this.entityId = role.getId();
			this.name = role.getName();
		}
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getServer_id() {
		return server_id;
	}

	public void setServer_id(Integer server_id) {
		this.server_id = server_id;
	}
	
	/* ==============================================
	 * =============== PUBLIC METHODS ===============
	 * ==============================================
	 */
	
	@Override
	public boolean equals(Object o) {
		if (o != null && o.getClass().isAssignableFrom(HelperRoleDTO.class)) {
			HelperRoleDTO helperRole = (HelperRoleDTO)o;
			
			return this.getEntityId() != null && this.getEntityId().equals(helperRole.getEntityId());
		}
		
		return false;
	}
	
}
