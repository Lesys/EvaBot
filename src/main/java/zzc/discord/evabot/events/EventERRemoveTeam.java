package zzc.discord.evabot.events;


import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.dto.MessageLogDTO;
import zzc.discord.evabot.dto.ScrimDTO;
import zzc.discord.evabot.dto.TeamDTO;
import zzc.discord.evabot.service.ERPlayerService;
import zzc.discord.evabot.service.ScrimService;
import zzc.discord.evabot.service.TeamService;

/**
 * 
 * @author Lesys
 *
 * Class of EventER that removes a Team registered for a Scrim
 */
@Service
public class EventERRemoveTeam extends EventER {
	protected final transient ERPlayerService erPlayerService;

	protected final transient TeamService teamService;

	protected final transient ScrimService scrimService;
	/**
	 * Constructor of EventERRemoveTeam
	 */
	public EventERRemoveTeam(ERPlayerService erPlayerService, ScrimService scrimService, TeamService teamService) {
		this.commandName += "removeTeam";

		this.erPlayerService = erPlayerService;
		this.teamService = teamService;
		this.scrimService = scrimService;
	}
	
	/**
	 * Check if the team exists in the scrim registration and removes it
	 */
	@Override
	@Transactional
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		String teamName = this.getMessage(event);

		ScrimDTO scrim = this.scrimService.getByEvent(event);
		
		if (scrim != null) {
			TeamDTO team = scrim.getTeam(teamName);
			
			if (team != null) {
				if (EventERManager.hasPermission(event, team)) {
					if (scrim.getTeamList().remove(team)) {
						
						scrim.getMessageLogList().add(new MessageLogDTO(event.getMessage()));

						this.scrimService.save(scrim);
						
						this.teamService.deleteById(team.getId());
						
						event.getChannel().sendMessage(teamName + " has successfully been removed from this scrim.").queue();

				        this.commandIsSuccessful();
					} else {
						event.getChannel().sendMessage(teamName + " hasn't been removed from this scrim.").queue();
					}
				} else {
					event.getChannel().sendMessage(event.getAuthor().getAsMention() + " does not have the rights to use this command. Only the captain of the team can use it.").queue();
				}
			} else {
				event.getChannel().sendMessage(teamName + " hasn't been registered in this scrim.").queue();
			}
		} else {
			event.getChannel().sendMessage(event.getChannel().getName() + " hasn't been registered as a scrim yet.").queue();
		}
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " {TeamName} - Removes a team from the registered teams.\n";
	}
}
