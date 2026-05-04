package zzc.discord.evabot.events;


import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.dto.ERPlayerDTO;
import zzc.discord.evabot.dto.MessageLogDTO;
import zzc.discord.evabot.dto.ScrimDTO;
import zzc.discord.evabot.dto.TeamDTO;
import zzc.discord.evabot.service.ERPlayerService;
import zzc.discord.evabot.service.ScrimService;

/**
 * 
 * @author Lesys
 *
 * Class of EventER that adds an ERPlayer to a team
 */
@Service
public class EventERAddPlayer extends EventER {
	private final transient ERPlayerService erPlayerService;

	private final transient ScrimService scrimService;
	/**
	 * Constructor of EventERAddPlayer
	 */
	@Autowired
	public EventERAddPlayer(ERPlayerService erPlayerService, ScrimService scrimService) {
		this.commandName += "addPlayer";

		this.erPlayerService = erPlayerService;
		this.scrimService = scrimService;
	}
	
	/**
	 * Check if the Team name exists in the registered teams, and adds the ERPlayer to the Team
	 */
	@Override
	@Transactional
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		List<String> names = event.getMessage().getContentRaw().lines().toList();
		List<User> members = new ArrayList<User>();

		if (names.size() > 1) {
			String teamName = names.get(0).split("(?i)".concat((Arrays.asList("+" , "*" , "?" , "^" , "$" , "(" , ")" , "[" , "]" , "{" , "}" , "|" , "\\").contains(this.commandName.substring(0, 1)) ? "\\" : "") + this.commandName + " "))[1].trim();
	
			ScrimDTO scrim = this.scrimService.getByEvent(event);
			
			if (scrim != null) {
				TeamDTO team = scrim.getTeam(teamName);
		
				if (team != null) {
					if (EventERManager.hasPermission(event, team)) {
						List<String> playerNames = new ArrayList<String>();
						
						for (int i = 1; i < names.size(); i++)
							if (!names.get(i).trim().isEmpty() && names.get(i).replaceAll(" +", " ").split(" ").length == 2)
								playerNames.add(names.get(i).replaceAll(" +", " "));
						
						if (event.getMessage().getMentions().getUsers().size() == playerNames.size())
							members.addAll(event.getMessage().getMentions().getUsers());
						
						boolean registered = false;
						System.err.println("Players: " + playerNames.size() + "; " + names.get(1));
						playerNames.stream().forEach(p -> System.err.println(p + "; "));
						try {
							registered = !playerNames.isEmpty() && playerNames.stream().allMatch(row -> {
								String discordName = "";
								try {
									User u = members.remove(0);
									discordName = u.getName();
								} catch (IndexOutOfBoundsException | UnsupportedOperationException e) {
									discordName = row.split(" ")[0];
								}
								String dak = row.split(" ")[1];
								String ign = dak.split("/")[dak.split("/").length - 1];
								System.err.println("Player: " + discordName + "; dak: " + dak + "; IGN: " + ign);
								
								ERPlayerDTO player = this.erPlayerService.getDtoByDak(ign);
								player.setDiscordName(discordName);
								player.setDak(dak);
								
								player = this.erPlayerService.saveToDto(player);
								
								return !scrim.alreadyRegistered(discordName) ?
										(team.addPlayer(player) ? true :
											(team.getSub() == null ? team.setSubPlayer(player)
											: false))
										: false;
							});
						} catch (ArrayIndexOutOfBoundsException e) {
							registered = false;
						}
			
						if (registered) {
							scrim.getMessageLogList().add(new MessageLogDTO(event.getMessage()));
							this.scrimService.save(scrim);

					        this.commandIsSuccessful();
						} else {
							event.getChannel().sendMessage("Your team members couldn't get saved correctly in the Team. Make sure you don't exceed the maximum number of player with those already registered in the team and none of the new players are registered in another Team.").queue();
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
		} else {
			event.getChannel().sendMessage("Please enter the correct format of adding team mates (see help command for more information).").queue();
		}
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " {TeamName} {Player1DiscordTag} {Player1DAKLink}... - Adds players to a team registered for the scrim. Please return to line after TeamName argument and write only 1 player per row.\nCommand use example: " + super.helpCommand() + " TeamName\nPlayer1DiscordTag https.../Player1Name\nPlayer2DiscordTag https.../Player2Name\n";
	}
}
