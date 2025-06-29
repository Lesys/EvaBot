package zzc.discord.evabot.events;

import zzc.discord.evabot.Priority;

/**
 * 
 * @author Lesys
 *
 *         Class of EventER that registers a Team for a scrim with high priority
 */
public class EventERTeamRegistrationPriorityHigh extends EventERTeamRegistrationPriority {
	/**
	 * Constructor of EventERTeamRegistrationHighPriority
	 */
	public EventERTeamRegistrationPriorityHigh() {
		
		this.commandName = EventER.commandPrefix + "registerPriorityHigh";
		this.priority = Priority.HIGH;
	}
	
//	@Override
//	public void executeCommand(@NotNull MessageReceivedEvent event) {
//		if (EventERManager.hasPermission(event)) {
//			super.executeCommand(event);
//		} else {			
//			event.getChannel().sendMessage("Only an Administrator can use this command.").queue();
//		}
//	}
}
