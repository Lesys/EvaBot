package zzc.discord.evabot.dto;

public class SpectatorDTO extends AbstractDTO {
	/**
	 * The spectator name
	 */
	protected String name;
	
	protected Integer scrim_id;
	
	public SpectatorDTO() {
		
	}
	
	public SpectatorDTO(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getScrim_id() {
		return scrim_id;
	}

	public void setScrim_id(Integer scrim_id) {
		this.scrim_id = scrim_id;
	}


	
	/* ==============================================
	 * =============== PUBLIC METHODS ===============
	 * ==============================================
	 */
	
	
}
