package zzc.discord.evabot.dto;

import java.util.Date;

import net.dv8tion.jda.api.entities.Message;

public class MessageLogDTO extends AbstractDTO {
	protected Date creationTime;

	protected Date modificationTime;

	/**
	 * The content of the message
	 */
	protected String message;
	
	/**
	 * The sender name
	 */
	protected String authorName;
	
	protected Integer scrim_id;
	
	public MessageLogDTO() {
		
	}
	
	/**
	 * Constructor of a MessageLog
	 * 
	 * @param m		The Message sent on Discord, supposed a command
	 */
	public MessageLogDTO(Message m) {
		this();
		this.message = m.getContentRaw();
		this.authorName = m.getAuthor().getName();
		this.creationTime = new Date();
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public Date getModificationTime() {
		return modificationTime;
	}

	public void setModificationTime(Date modificationTime) {
		this.modificationTime = modificationTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
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
