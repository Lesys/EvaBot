package zzc.discord.evabot;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import net.dv8tion.jda.api.entities.Message;

/**
 * 
 * @author Lesys
 * 
 * Class representing a Message sent on Discord
 */
public class MessageLog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6767589585609547345L;

	/**
	 * The content of the message
	 */
	protected String message;
	
	/**
	 * The sender name
	 */
	protected String authorName;
	
	/**
	 * The time when the message was sent
	 */
	protected OffsetDateTime creationTime;
	
	/**
	 * Constructor of a MessageLog
	 * 
	 * @param m		The Message sent on Discord, supposed a command
	 */
	public MessageLog(Message m) {
		this.message = m.getContentRaw();
		this.authorName = m.getAuthor().getName();
		this.creationTime = m.getTimeCreated();
	}
	
	/**
	 * Getter of message
	 * @return		The raw content of the Message
	 */
	public String getMessage() {
		return this.message;
	}
	
	/**
	 * Getter of creationTime but formatted
	 * @return		creationTime with a specified format
	 */
	public String getDateTimeString() {
		return this.creationTime.format(DateTimeFormatter.ofPattern("yyyy-MMMM-dd HH:mm:ss Z"));
	}
	
	/**
	 * Getter of authorName
	 * @return		The author of the Message
	 */
	public String getAuthor() {
		return this.authorName;
	}
}
