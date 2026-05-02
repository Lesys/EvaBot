package zzc.discord.evabot.events;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.service.ERPlayerService;
import zzc.discord.evabot.service.ScrimService;
import zzc.discord.evabot.util.enumeration.Priority;

/**
 * 
 * @author Lesys
 *
 *         Abstract class of EventER that registers a Team for a scrim with a priority
 */
public abstract class EventERRegisterTeamPriority extends EventERRegisterTeam {
	
	protected Priority priority = Priority.NEUTRAL;
	
	@Autowired
	public EventERRegisterTeamPriority(ERPlayerService erPlayerService, ScrimService scrimService) {
		super(erPlayerService, scrimService);
	}
	
	@Override
	public void preExecuteCommand(@NotNull MessageReceivedEvent event) {
		if (EventERManager.hasPermission(event)) {
			System.out.println("[" + this.getClass().getName() + "] sets priority to " + this.priority.name());
			this.team.setPriority(this.priority);
		} else {
			System.err.println("[" + this.getClass().getName() + "] Only authorized entites can set priority");			
		}
	}
}
