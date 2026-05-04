package zzc.discord.evabot.events;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.dto.ERPlayerDTO;
import zzc.discord.evabot.dto.ScrimDTO;
import zzc.discord.evabot.dto.TeamDTO;
import zzc.discord.evabot.service.ScrimService;
import zzc.discord.evabot.util.UtilERPlayer;
import zzc.discord.evabot.util.UtilEmpty;
import zzc.discord.evabot.util.enumeration.Priority;

/**
 * 
 * @author Lesys
 *
 *         Class of EventER when the user wants to get the selected teams for a
 *         scrim by the snake method
 */
@Service
public class EventERGetSelectedTeamsSnake extends EventER {
	public static Integer maximumTeamPerLobby = 8;
	protected Integer realNumberOfLobby;
	protected Integer realTeamPerLobby;
	protected List<List<TeamDTO>> lobbies;

	private final transient ScrimService scrimService;
	
	/**
	 * Constructor of EventERGetSelectedTeamsSnake
	 */
	public EventERGetSelectedTeamsSnake(ScrimService scrimService) {
		this.commandName += "selectedTeamsSnake";

		this.scrimService = scrimService;
	}
	
	/**
	 * Gets all the teams from the serialized variable and sort them by snake MMR. Changes the roles if a role has been mentioned
	 */
	@Override
	@Transactional
	public void executeCommand(@NotNull MessageReceivedEvent event) {		
		final StringBuilder builder = new StringBuilder();
		builder.append("Selected teams by the snake sorting \"" + event.getChannel().getName() + "\":\n");
		
		String[] message = event.getMessage().getContentRaw().trim().replaceAll(" +", " ").split(" ");
		
		try {
			Integer numberOfLobby = message.length > 1 ? Integer.valueOf(message[1]) : 1;			
			Integer minimumTeamPerLobby = message.length > 2 ? Integer.valueOf(message[2]) : maximumTeamPerLobby;
			
			if (numberOfLobby == null || numberOfLobby == 0 || minimumTeamPerLobby == null || minimumTeamPerLobby == 0) {
				throw new NumberFormatException();
			}
			String roleName = "";
			
			if (message.length > 3) {
				roleName += message[3];
			}
			
			for (int i = 4; i < message.length; i++)
				roleName += (roleName != "" ? " " : "") + message[i];
			
			ScrimDTO scrim = this.scrimService.getByEvent(event);
	
			if (scrim != null && !UtilEmpty.isEmptyOrNull(scrim.getTeamList())) {
				if (EventERManager.hasPermission(event)) {				
					realNumberOfLobby = numberOfLobby;
					realTeamPerLobby = maximumTeamPerLobby;
					
					int totalNumberOfTeams = manageNumberLobbyTeam(minimumTeamPerLobby, scrim);
					
					if (scrim.getTeamList().size() >= totalNumberOfTeams) {
						List<TeamDTO> filtered = filterTeams(scrim, totalNumberOfTeams);
						
						this.lobbies = snakeFilter(filtered, realNumberOfLobby, totalNumberOfTeams);
						
						this.lobbies.forEach(lobby -> {
							builder.append("**__Lobby n°" + (this.lobbies.indexOf(lobby) + 1) + "__**");
							sendLobbyToMessage(lobby, builder, event);
						});
						
						this.postExecuteCommand(event);
				        sendMessageWait(event, builder.toString());
					} else {
						event.getChannel().sendMessage("There are not enough teams to make at least 1 lobby.").queue();								
					}
				} else {
					event.getChannel().sendMessage("Only an Administrator can use this command.").queue();				
				}
			} else {			
				event.getChannel().sendMessage("No teams has yet to be registered for the scrim \"" + event.getChannel().getName() + "\".").queue();
			}
		} catch (NumberFormatException e) {
			event.getChannel().sendMessage("The parameters for lobbies or teams is null or less than 0. Please use the command correctly.").queue();
		}
		
		event.getMessage().removeReaction(Emoji.fromUnicode("U+1F504")).queueAfter(2, TimeUnit.SECONDS);
	}
	
	@Override
	public String helpCommand() {
		return super.helpCommand()
				+ " [numberOfLobbies] [minimumNumberOfTeamsPerLobby] [RolesMention] - Returns all the teams selected via the snake method with their average MMR and players registered in the team with their own MMR. All teams are sorted by team average MMR. If a role is mentioned (or fully written at the end of the command), removes the roles to everyone not in the selected teams or spectators and add it to them instead (won't return the selected teams).\n";
	}
	
	protected static List<TeamDTO> filterTeams(ScrimDTO scrim, int totalNumberOfTeams) {
		return scrim.getTeamList().stream().sorted().limit(totalNumberOfTeams).toList();
	}

	protected int manageNumberLobbyTeam(Integer minimumTeamPerLobby, ScrimDTO scrim) {
		boolean numbersDone = false;
		
		do {
			// If we have more than the max number of teams * the number of lobbies desired, all good
			if (scrim.getTeamList().size() >= realNumberOfLobby * realTeamPerLobby) {
				numbersDone = true;
			} else {
				// Reducing the number of teams per lobby until we reach the minimum sent
				if (realTeamPerLobby > minimumTeamPerLobby) {
					realTeamPerLobby--;
				} else if (realNumberOfLobby > 1) { // Reducing the number of lobbies until we reach 1
					// Resetting the number of teams per lobby so we can get another loop with number of lobbies reduced
					realTeamPerLobby = maximumTeamPerLobby;
					realNumberOfLobby--;
				} else { // If we get there, then there are not enough teams to make at least 1 lobby with the minimum teams required by the sender
					numbersDone = true;
				}
			}
		} while (!numbersDone);
		
		return realNumberOfLobby * realTeamPerLobby;
	}
	
	/**
	 * Retrieves the teams signed up in a format of snake lobbies
	 * 
	 * @param filtered
	 * @param realNumberOfLobby
	 * @param totalNumberOfTeams
	 * @return
	 */
	protected static List<List<TeamDTO>> snakeFilter(List<TeamDTO> filtered, int realNumberOfLobby, int totalNumberOfTeams) {
		List<List<TeamDTO>> lobbies = new ArrayList<List<TeamDTO>>();
		// Add a list for each lobby
		for (int i = 0; i < realNumberOfLobby; i++) {
			lobbies.add(new ArrayList<TeamDTO>());
		}
		
		int lobbyNumber = 1;
		int cursor = 1;
		
		// Starting at 1 to prevent 0 % x and to be able to have the condition at the end of the loop
		// (if we had for example realNumberOfLobby == 2, i == 0 1 and 2 would have done 3 teams because cursor is modified only at i == 2)
		for (int i = 1; i <= totalNumberOfTeams; i++) {
			lobbies.get(lobbyNumber - 1).add(filtered.get(i - 1));
			
			// If we have put a team across all the lobbies, we go back starting with the current lobby
			if (i % realNumberOfLobby == 0) {
				cursor *= -1;						
			} else {
				lobbyNumber += cursor;
			}
		}
		
		return lobbies;
	}
	
	/**
	 * Builds the StringBuilder with the full lobby, and sends if the builder has too much characters
	 * 
	 * @param filtered
	 * @param builder
	 * @param event
	 */
	protected static void sendLobbyToMessage(List<TeamDTO> filtered, StringBuilder builder, @NotNull MessageReceivedEvent event) {
		final List<String> messages = new ArrayList<String>();
		AtomicInteger placement = new AtomicInteger(1);
		
		filtered.stream().forEach(team -> {
			if (builder.length() > 0 && builder.length() >= 1800) {
		        messages.forEach(m -> sendMessageWait(event, m));
				messages.add(builder.toString());
				builder.delete(0, builder.length());
			}
			teamStringBuilder(builder, team, placement, event);
		});

        messages.forEach(m -> sendMessageWait(event, m));
		builder.append("\n");
	}
	
	/**
	 * Protected method to construct the string to send
	 * 
	 * @param builder   The string builder that is going to be displayed in the
	 *                  message
	 * @param team      The Team for which you want to create the string
	 * @param placement The integer corresponding to the place of the team
	 * @param event     The event sent to be able to mention people
	 */
	protected static void teamStringBuilder(final StringBuilder builder, TeamDTO team, AtomicInteger placement, MessageReceivedEvent event) {
		builder.append("\n")
			.append(placement.getAndIncrement()).append("°) **__").append(team.getName()).append("__** (").append(team.getAverage()).append(")")
			.append((!Priority.NEUTRAL.equals(team.getPriority()) ? " **" + team.getPriority().toString() + " priority** " : "")).append(":\n");
		team.getPlayerList().forEach(player -> {
				System.out.println("Player getSelectedTeams: " + player.getDak() + "; " + player.getDisplayName());

				// If player is sub, open bracket
				if (player.equals(team.getSub())) {
					builder.append("[Sub: ");
				}
				
				String underline = UtilERPlayer.hasSameDiscordName(team.getCaptain(), player) ? "__" : "";
				
				builder.append(underline);
				builder.append(EventERGetSelectedTeamsSnake.getMention(event, player));
				builder.append(underline);
				builder.append(" (").append(UtilERPlayer.getNameWithoutSpecialChar(player::getDakName)).append(" - ").append(player.getMmr()).append(")");

				// If player is sub, close bracket
				if (player.equals(team.getSub())) {
					builder.append("]");
				} else {
					builder.append("; ");
				}
		});
	}
	
	/**
	 * Returns the string corresponding to the mention of the member
	 * 
	 * @param event		The event sent to be able to mention people
	 * @param player 	The player to mention
	 * @return The mention (or a String if member is null)
	 */
	protected static String getMention(MessageReceivedEvent event, ERPlayerDTO player) {
		String mention = "";
		try {
			Member m = event.getGuild().getMembersByName(player.getDiscordName(), true).stream().findFirst().orElse(null);
			if (m != null) {
				mention = m.getAsMention();
			} else {
				mention = player.getDiscordName().replaceAll("[*_]", "");
			}
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
			mention = player.getDiscordName().replaceAll("[*_]", "");
		}
		return mention;
	}
	
	protected static Member getMemberFromGuild(MessageReceivedEvent event, ERPlayerDTO player) {
		Member m = null;
		try {
			m = event.getGuild().getMembersByName(player.getDiscordName(), true).stream().findFirst().orElse(null);
			if (m == null) {
				m = null;
			}
		} catch (IllegalArgumentException e) {
			System.err.println("getMemberFromGuild for player " + player.getDiscordName() + ": " + e.getMessage());
			m = null;
		}
		
		return m;
	}
	
	protected static Member getMemberFromGuild(MessageReceivedEvent event, String playerName) {
		Member m = null;
		try {
			m = event.getGuild().getMembersByName(playerName, true).stream().findFirst().orElse(null);
			if (m == null) {
				m = null;
			}
		} catch (IllegalArgumentException e) {
			System.err.println("getMemberFromGuild for name " + playerName + ": " + e.getMessage());
			m = null;
		}
		
		return m;
	}
}
