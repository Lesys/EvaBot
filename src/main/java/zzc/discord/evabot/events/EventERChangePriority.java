package zzc.discord.evabot.events;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.dto.MessageLogDTO;
import zzc.discord.evabot.dto.ScrimDTO;
import zzc.discord.evabot.dto.TeamDTO;
import zzc.discord.evabot.service.ScrimService;
import zzc.discord.evabot.service.TeamService;
import zzc.discord.evabot.util.UtilEmpty;
import zzc.discord.evabot.util.enumeration.Priority;

/**
 * 
 * @author Lesys
 *
 * Class of EventER that changes the priority of the registered Team
 */
@Service
public class EventERChangePriority extends EventER {
	private final transient ScrimService scrimService;

	private final transient TeamService teamService;
	/**
	 * Constructor of EventERChangePriority
	 */
	@Autowired
	public EventERChangePriority(ScrimService scrimService, TeamService teamService) {
		this.commandName += "changePriority";
		
		this.scrimService = scrimService;
		this.teamService = teamService;
	}
	
	/**
	 * Check if the	messages contains a mention, and put this User as a captain of the Team if it exists
	 */
	@Override
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		String[] message = this.getMessageArray(event);

		String teamName = "";
		for (int i = 0; i < message.length - 1; i++)
			teamName += message[i] + (i < message.length - 2 ? " " : "");

		String newPriority = message[message.length - 1];

		TeamDTO team = this.teamService.getByEventAndTeamName(event, teamName);
		
		if (team != null) {
			Priority priority = Priority.getPriorityByName(newPriority);
			
			if (!UtilEmpty.isEmptyOrNull(newPriority) && priority != null) {			
				if (team.getPriority() == null || !Priority.equals(team.getPriority(), newPriority)) {					
					// Only administrator or said entities can change priority
					if (EventERManager.hasPermission(event)) {
						System.out.println("[EventERChangePriority] New priority for " + team.getName() + ": " + priority);
						
						team.setPriority(priority);

						ScrimDTO scrim = this.scrimService.getByEvent(event);
						
						scrim.getMessageLogList().add(new MessageLogDTO(event.getMessage()));
						
						this.scrimService.save(scrim);

				        this.commandIsSuccessful();
					} else {
						event.getChannel().sendMessage(event.getAuthor().getAsMention() + " does not have the rights to use this command. Only the captain of the team can use it.").queue();
					}
				} else {
					event.getChannel().sendMessage(newPriority + " is already the current priority for the team.").queue();
				}
			} else {
				event.getChannel().sendMessage("Please choose a priority between the choices to put at the end of the command line (" + Priority.valuesToString() + ").").queue();
			}
		} else {
			event.getChannel().sendMessage(teamName + " hasn't been registered in this scrim.").queue();
		}
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " {TeamName} {NewPriority} - Puts the new priority (" + Priority.valuesToString() + ") for the registered team.\n";
	}
}
