package zzc.discord.evabot.events;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.dto.ScrimDTO;
import zzc.discord.evabot.service.ScrimService;
import zzc.discord.evabot.service.UpdateMmrService;

/**
 * 
 * @author Lesys
 *
 * Class of EventER when the user wants to get the registered teams for a scrim and forcing the update of the MMR
 */
@Service
public class EventERGetRegisteredTeamsForceUpdate extends EventERGetRegisteredTeams {
	private final transient ScrimService scrimService;
	
	private final transient UpdateMmrService updateMmrService;
	
	/**
	 * Constructor of EventERGetRegisteredTeamsForceUpdate
	 */
	public EventERGetRegisteredTeamsForceUpdate(ScrimService scrimService, UpdateMmrService updateMmrService) {
		super(scrimService, updateMmrService);
		
		this.commandName = EventER.commandPrefix + "registeredTeamsForce";
		
		this.scrimService = scrimService;
		this.updateMmrService = updateMmrService;
	}
	
	/**
	 * Gets all the teams from the serialized variable, force the update of their MMR and sort them by MMR
	 */
	@Override
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		final List<String> messages = new ArrayList<String>();

        final StringBuilder builder = new StringBuilder();
        builder.append("Registered teams for the scrim \"" + event.getChannel().getName() + "\" by Team average MMR order:\n");

		ScrimDTO scrim = this.scrimService.getByEvent(event);
		
		if (scrim != null) {
			AtomicInteger placement = new AtomicInteger(1);
			if (EventERManager.hasPermissionAdminOrHelper(event)) {
				this.updateMmrService.updateTeamsMmr(scrim, true);
				
				scrim.getTeamList().stream().sorted().forEach(team -> {
			    	if (builder.length() > 0 && builder.length() >= 1800) {
			    		messages.add(builder.toString());
			            builder.delete(0, builder.length());
			    	}
					EventERGetRegisteredTeamsForceUpdate.teamStringBuilder(builder, team, placement, true, event);
				});

				messages.add(builder.toString());
		        messages.forEach(m -> sendMessageWait(event, m));
				
				this.scrimService.save(scrim);
				
				this.commandIsSuccessful();
			} else {
				event.getChannel().sendMessage("Only an Administrator can use this command.").queue();				
			}
		} else {			
			event.getChannel().sendMessage("No teams has yet to be registered for the scrim \"" + event.getChannel().getName() + "\".").queue();
		}
	}

	@Override
	public String helpCommand() {
		return "**" + this.commandName + "**" + " - Forces the MMR to update and returns all the teams registered with their average MMR and players registered in the team with their own MMR.\n";
	}
}
