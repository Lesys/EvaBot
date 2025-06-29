package zzc.discord.evabot.events;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.Priority;

/**
 * 
 * @author Lesys
 *
 *         Class of EventER that registers a Team for a scrim with low priority
 */
public class EventERTeamRegistrationPriorityLow extends EventERTeamRegistrationPriority {
	/**
	 * Constructor of EventERTeamRegistrationLowPriority
	 */
	public EventERTeamRegistrationPriorityLow() {
		this.commandName = EventER.commandPrefix + "registerPriorityLow";
		this.priority = Priority.LOW;
	}
}
