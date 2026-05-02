package zzc.discord.evabot.events;

import org.springframework.stereotype.Service;

import zzc.discord.evabot.service.ERPlayerService;
import zzc.discord.evabot.service.ScrimService;
import zzc.discord.evabot.util.enumeration.Priority;

/**
 * 
 * @author Lesys
 *
 *         Class of EventER that registers a Team for a scrim with low priority
 */
@Service
public class EventERRegisterTeamPriorityLow extends EventERRegisterTeamPriority {
	/**
	 * Constructor of EventERRegisterTeamPriorityLow
	 */
	public EventERRegisterTeamPriorityLow(ERPlayerService erPlayerService, ScrimService scrimService) {
		super(erPlayerService, scrimService);
		
		this.commandName = EventER.commandPrefix + "registerPriorityLow";
		this.priority = Priority.LOW;
	}
}
