package zzc.discord.evabot.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import zzc.discord.evabot.service.ERPlayerService;
import zzc.discord.evabot.service.ScrimService;
import zzc.discord.evabot.util.enumeration.Priority;

/**
 * 
 * @author Lesys
 *
 *         Class of EventER that registers a Team for a scrim with high priority
 */
@Service
public class EventERRegisterTeamPriorityHigh extends EventERRegisterTeamPriority {
	/**
	 * Constructor of EventERRegisterTeamPriorityHigh
	 */
	@Autowired
	public EventERRegisterTeamPriorityHigh(ERPlayerService erPlayerService, ScrimService scrimService) {
		super(erPlayerService, scrimService);
		
		this.commandName = EventER.commandPrefix + "registerPriorityHigh";
		this.priority = Priority.HIGH;
	}
}
