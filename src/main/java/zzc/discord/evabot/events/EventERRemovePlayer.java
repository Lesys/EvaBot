package zzc.discord.evabot.events;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.dto.ERPlayerDTO;
import zzc.discord.evabot.dto.MessageLogDTO;
import zzc.discord.evabot.dto.ScrimDTO;
import zzc.discord.evabot.dto.TeamDTO;
import zzc.discord.evabot.service.ScrimService;
import zzc.discord.evabot.service.TeamService;

/**
 * 
 * @author Lesys
 *
 * Class of EventER that removes an ERPlayer from a team
 */
@Service
public class EventERRemovePlayer extends EventER {
	protected final transient TeamService teamService;

	protected final transient ScrimService scrimService;
	/**
	 * Constructor of EventGuildMessageRemovePlayer
	 */
	public EventERRemovePlayer(TeamService teamService, ScrimService scrimService) {
		this.commandName += "removePlayer";

		this.teamService = teamService;
		this.scrimService = scrimService;
	}
	
	/**
	 * Check if the Team name exists in the registered teams, and removes the ERPlayer if it exists in the Team
	 */
	@Override
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		String[] message = this.getMessageArray(event);	

		String teamName = "";
		for (int i = 0; i < message.length - 1; i++)
			teamName += message[i] + (i < message.length - 2 ? " " : "");
		
		String playerName = message[message.length - 1];

		TeamDTO team = this.teamService.getByEventAndTeamName(event, teamName);
		
		if (team != null) {
			String discordName = playerName;
			if (discordName.startsWith("<@")) { // Check if the name is a Discord tag
				discordName = event.getGuild()
						.getMemberById(discordName.subSequence(2, discordName.length() - 1).toString())
						.getUser().getName(); // Get the name of the unique user tagged
			}
			
			final String finalPlayerName = discordName;
			ERPlayerDTO playerFromDiscordName = team.getPlayerByDiscordName(discordName);
						
			if (playerFromDiscordName != null) {
				if (EventERManager.hasPermission(event, teamName)) {
					if (team.removePlayer(playerFromDiscordName)) {
						this.teamService.save(team);
						
						ScrimDTO scrim = this.scrimService.getByEvent(event);

						scrim.getMessageLogList().add(new MessageLogDTO(event.getMessage()));

						this.scrimService.save(scrim);

				        this.commandIsSuccessful();
					} else {
						event.getChannel().sendMessage(finalPlayerName + " is not part of the " + teamName + " team registered in this scrim.").queue();
					}
				} else {
					event.getChannel().sendMessage(event.getAuthor().getAsMention() + " does not have the rights to use this command. Only the captain of the team can use it.").queue();
				}
			} else {
				event.getChannel().sendMessage(finalPlayerName + " hasn't been registered in Team \"" + team.getName() + "\" for this scrim.").queue();
			}				
		} else {
			event.getChannel().sendMessage(teamName + " hasn't been registered in this scrim.").queue();
		}
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " {TeamName} {PlayerDiscordName} - Removes a player from a team registered.\n";
	}
}
