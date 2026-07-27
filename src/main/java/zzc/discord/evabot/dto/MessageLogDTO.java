package zzc.discord.evabot.dto;

import java.util.Date;

import net.dv8tion.jda.api.entities.Message;

public class MessageLogDTO extends AbstractDTO implements Comparable<MessageLogDTO> {
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

	
	/**
	 * Adds something to the message content
	 * @param string	The String to add to the message
	 */
	public void addToMessage(String string) {
		this.message += string;
	}

	@Override
	public int compareTo(MessageLogDTO o) {
		return this.creationTime.compareTo(o.creationTime);
	}
	
}
