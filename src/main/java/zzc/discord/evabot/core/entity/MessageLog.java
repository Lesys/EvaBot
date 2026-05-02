package zzc.discord.evabot.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.text.SimpleDateFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * 
 * @author Lesys
 * 
 * Class representing a Message sent on Discord
 */
@Entity
@Table(name = "message_log")
public class MessageLog extends AbstractEntity implements Comparable<MessageLog> {
	/**
	 * The content of the message
	 */
	protected String message;
	
	/**
	 * The sender name
	 */
	protected String authorName;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "scrim_id")
	protected Scrim scrim;
	
	public MessageLog() {
		
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public Scrim getScrim() {
		return scrim;
	}

	public void setScrim(Scrim scrim) {
		this.scrim = scrim;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Getter of creationTime but formatted
	 * @return		creationTime with a specified format
	 */
	public String getDateTimeString() {
		if (this.creationTime == null) return null;
		SimpleDateFormat spf = new SimpleDateFormat("yyyy-MMMM-dd HH:mm:ss Z");
		return spf.format(this.creationTime);
	}
	
	/**
	 * Getter of message
	 * @return		The raw content of the Message
	 */
	public String getMessage() {
		return this.message;
	}
	
	/**
	 * Getter of authorName
	 * @return		The author of the Message
	 */
	public String getAuthor() {
		return this.authorName;
	}
	
	/**
	 * Adds something to the message content
	 * @param string	The String to add to the message
	 */
	public void addToMessage(String string) {
		this.message += string;
	}

	@Override
	public int compareTo(MessageLog o) {
		return this.creationTime.compareTo(o.creationTime);
	}
}
