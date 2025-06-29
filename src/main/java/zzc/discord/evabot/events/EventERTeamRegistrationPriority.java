package zzc.discord.evabot.events;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.Priority;

/**
 * 
 * @author Lesys
 *
 *         Abstract class of EventER that registers a Team for a scrim with a priority
 */
public abstract class EventERTeamRegistrationPriority extends EventERTeamRegistration {
	
	protected Priority priority = Priority.NEUTRAL;
	
	@Override
	public void preExecuteCommand(@NotNull MessageReceivedEvent event) {		
		this.team.setPriority(this.priority);
	}
}
