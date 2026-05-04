package zzc.discord.evabot.events;


import java.util.Arrays;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
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
 * Class of EventER that changes the name of the captain of the Team
 */
@Service
public class EventERChangeCaptain extends EventER {
	private final transient TeamService teamService;

	private final transient ScrimService scrimService;
	/**
	 * Constructor of EventERChangeCaptain
	 */
	@Autowired
	public EventERChangeCaptain(TeamService teamService, ScrimService scrimService) {
		this.commandName += "giveCaptain";

		this.scrimService = scrimService;
		this.teamService = teamService;
	}
	
	/**
	 * Check if the	messages contains a mention, and put this User as a captain of the Team if it exists
	 */
	@Override
	@Transactional
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		String[] message = event.getMessage().getContentRaw().split("(?i)".concat((Arrays.asList("+" , "*" , "?" , "^" , "$" , "(" , ")" , "[" , "]" , "{" , "}" , "|" , "\\").contains(this.commandName.substring(0, 1)) ? "\\" : "") + this.commandName + " "))[1].split(" ");

		String teamName = "";
		for (int i = 0; i < message.length - 1; i++)
			teamName += message[i] + (i < message.length - 2 ? " " : "");

		String newCaptain = event.getMessage().getMentions().getMembers().size() == 1 ? event.getMessage().getMentions().getMembers().get(0).getUser().getName() : message[message.length - 1];
			
		System.err.println("Captain name: " + newCaptain);

		TeamDTO team = this.teamService.getByEventAndTeamName(event, teamName);
		
		if (team != null) {
			ERPlayerDTO player = team.getPlayerByDiscordName(newCaptain);
			
			if (player != null) {
				if (team.getCaptain() == null || !team.getCaptain().getDiscordName().equalsIgnoreCase(newCaptain)) {
					if (EventERManager.hasPermission(event, teamName)) {
						System.out.println("[EventERChangeCaptain] captain change start");
						
						team.setCaptain(player);
						
						this.teamService.save(team);
						
						ScrimDTO scrim = this.scrimService.getByChannelId(event.getChannel().getId());
	
						scrim.getMessageLogList().add(new MessageLogDTO(event.getMessage()));

						this.scrimService.save(scrim);

				        this.commandIsSuccessful();
						
						System.out.println("[EventERChangeCaptain] captain change end");
					} else {
						event.getChannel().sendMessage(event.getAuthor().getAsMention() + " does not have the rights to use this command. Only the captain of the team can use it.").queue();
					}
				} else {
					event.getChannel().sendMessage(newCaptain + " is already the captain of the team.").queue();
				}
			} else {
				event.getChannel().sendMessage(newCaptain + " hasn't been registered in this team.").queue();
			}
		} else {
			event.getChannel().sendMessage(teamName + " hasn't been registered in this scrim.").queue();
		}/*
		} else {
			event.getChannel().sendMessage("Please put the mentioned user at the end of the command (see help command for more informations).").queue();
		}*/
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " {TeamName} @{NewCaptain} - Give the captain role of the registered team to the mentionned user.\n";
	}
}
