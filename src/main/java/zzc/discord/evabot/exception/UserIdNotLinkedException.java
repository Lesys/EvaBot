package zzc.discord.evabot.exception;

public class UserIdNotLinkedException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1175501021565808795L;
	
	private Integer statusCode;
	
	public UserIdNotLinkedException() {
		this(null);
	}
		
	public UserIdNotLinkedException(Integer statusCode) {
		super();
		this.statusCode = statusCode;
	}
	
	public Integer getStatusCode() {
		return this.statusCode;
	}

	@Override
	public String getMessage() {
		return "The userId was not able to find the user linked to. Be sure that the user didn't go through a name change.";
	}
}
