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
import zzc.discord.evabot.dto.ServerDTO;
import zzc.discord.evabot.dto.TeamDTO;
import zzc.discord.evabot.service.ERPlayerService;
import zzc.discord.evabot.service.ScrimService;
import zzc.discord.evabot.service.ServerService;
import zzc.discord.evabot.util.UtilEmpty;

/**
 * 
 * @author Lesys
 *
 *         Class of EventER that registers a Team for a scrim
 */
@Service
public class EventERRegisterTeam extends EventER {
	protected transient TeamDTO team;
	
	protected final transient ERPlayerService erPlayerService;

	protected final transient ScrimService scrimService;

	protected final transient ServerService serverService;
	/**
	 * Constructor of EventERRegisterTeam
	 */
	@Autowired
	public EventERRegisterTeam(ERPlayerService erPlayerService, ScrimService scrimService, ServerService serverService) {
		this.commandName += "register";

		this.erPlayerService = erPlayerService;
		this.scrimService = scrimService;
		this.serverService = serverService;
	}
	
	/**
	 * Gets the Team name, the ERPlayer names and DAK, retrieves the exact MMR of
	 * each player (DAK name) from the ER API, and creates a Team with all those
	 * players
	 */
	@Override
	@Transactional
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		String channelName = event.getChannel().getName();
		String discordServerName = event.getGuild().getName();
		List<String> names = event.getMessage().getContentRaw().lines().toList(); //Change to getContentDisplay
		List<User> members = new ArrayList<User>();
		if (names.size() > 1) {
			String teamName = names.get(0).split(
					"(?i)".concat((Arrays.asList("+", "*", "?", "^", "$", "(", ")", "[", "]", "{", "}", "|", "\\")
							.contains(this.commandName.substring(0, 1)) ? "\\" : "") + this.commandName + " "))[1]
					.trim();

			ServerDTO server = this.serverService.getByEvent(event);
			
			if (server == null) {
				server = new ServerDTO(event.getGuild().getName(), event.getGuild().getId());

				server = this.serverService.getFormatter().entityToDto(this.serverService.save(server));
			}

			ScrimDTO scrim = this.scrimService.getByEvent(event);
			final ScrimDTO scrimFinal = scrim != null ? scrim : new ScrimDTO();
			
			if (scrimFinal.getTeam(teamName) == null) {
				System.out.println("[EventERRegisterTeam] new team registration start");
				System.out.println("[EventERRegisterTeam] team name : " + teamName);
				List<String> playerNames = new ArrayList<String>();
				
				for (int i = 1; i < names.size(); i++)
					if (!names.get(i).trim().isEmpty() && names.get(i).trim().replaceAll(" +", " ").split(" ").length == 2)
						playerNames.add(names.get(i).trim().replaceAll(" +", " "));
					
				if (event.getMessage().getMentions().getUsers().size() == playerNames.size())
					members.addAll(event.getMessage().getMentions().getUsers());
				
				this.team = new TeamDTO(teamName);
				
				boolean registered = false;
				// System.err.println("Author: " + event.getMessage().getAuthor().getName());
				// System.err.println("Players: " + playerNames.size() + "; " +
				// playerNames.get(0));
				playerNames.stream().forEach(p -> System.err.println("[EventERRegisterTeam] player to add to the team : " + p));
				// members.forEach(m -> event.getMessage().getMentions().getRoles().forEach(r ->
				// event.getGuild().addRoleToMember(m, r)));
				try {
					registered = !playerNames.isEmpty() && playerNames.stream().allMatch(row -> {
						String discordName = "";
						try {
							User u = members.remove(0);
							discordName = u.getName();
						} catch (IndexOutOfBoundsException e) {
							discordName = row.split(" ")[0];
							if (discordName.startsWith("<@")) { // Check if the name is a Discord tag because one of the
																// others wasn't a Discord tag
								discordName = event.getGuild()
										.getMemberById(discordName.subSequence(2, discordName.length() - 1).toString())
										.getUser().getName(); // Get the name of the unique user tagged
							}
						}
						String dak = row.split(" ")[1];
						String ign = dak.split("/")[dak.split("/").length - 1];
						System.err.println("Player: " + discordName + "; dak: " + dak + "; ign: " + ign
								+ "; Captain name: " + event.getMessage().getAuthor().getName());
						
						// Do not allow a player to have an empty ingame name
						if (UtilEmpty.isEmptyOrNull(ign)) {
							System.err.println("[EventERRegisterTeam] cancelling team registration because player name is null or empty for row : " + row);
							return false;
						}

						ERPlayerDTO player = this.erPlayerService.getDtoByDak(ign);
						
						player.setDiscordName(discordName);
						player.setDak(dak);
						
						player = this.erPlayerService.saveToDto(player);
						
						return !scrimFinal.alreadyRegistered(ign)
								? (this.team.addPlayer(player) ? true
										: (this.team.getSub() == null ? this.team.setSubPlayer(player) : false))
								: false;
					});
				} catch (ArrayIndexOutOfBoundsException e) {
					registered = false;
				}
				
				if (registered) {
					// Retrieve the captain inside the team with the same name, else puts the captain to null
					this.team.setCaptain(this.team.getFullPlayerList().stream()
							.filter(player -> player.getDiscordName().equalsIgnoreCase(event.getMessage().getAuthor().getName()))
							.findFirst().orElse(null));
					this.preExecuteCommand(event);
					
					if (scrimFinal.getId() == null) {
						scrimFinal.setChannelId(event.getChannel().getId());
						scrimFinal.setServer(server);
						scrimFinal.setChannelName(channelName);
					}
					
					scrimFinal.getMessageLogList().add(new MessageLogDTO(event.getMessage()));
					
					// Adds the team to the scrim
					scrimFinal.getTeamList().add(this.team);

					this.scrimService.save(scrimFinal);
					  
					this.postExecuteCommand(event);
					
					event.getChannel()
							.sendMessage("**" + teamName + "** has been registered for the " + channelName + " scrim.")
							.queue();

			        this.commandIsSuccessful();
				} else {
					event.getChannel().sendMessage(
							"Your team members couldn't get saved correctly in the Team. Make sure you don't exceed the maximum number of player for a Team and none of the players are registered in another Team.")
							.queue();
				}
			} else {
				event.getChannel().sendMessage("A team with the name **" + teamName + "** has already been registered for the " + channelName
						+ " scrim. Please add players using the right command if you are the captain of the Team, or change your Team name and try again.")
						.queue();
			}
		} else {
			event.getChannel().sendMessage(
					"Please enter the correct format of team registration (see help command for more information).")
					.queue();
		}
	}
	
	@Override
	public String helpCommand() {
		return super.helpCommand()
				+ " {TeamName} {Player1DiscordTag} {Player1DAKLink}... - Registers a team with the players for the scrim. Please return to line after TeamName argument and write only 1 player per row. Tag can be the Discord unique name only if the player is not in the current server.\nCommand use example: "
				+ super.helpCommand()
				+ " TeamName\nPlayer1DiscordTag https.../Player1AccountName\nPlayer2DiscordTag https.../Player2AccountName\nPlayer3DiscordTag https.../Player3AccountName\n";
	}
}
