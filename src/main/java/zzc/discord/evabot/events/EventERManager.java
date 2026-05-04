package zzc.discord.evabot.events;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import zzc.discord.evabot.Bot;
import zzc.discord.evabot.dto.ERPlayerDTO;
import zzc.discord.evabot.dto.ScrimDTO;
import zzc.discord.evabot.dto.TeamDTO;
import zzc.discord.evabot.service.ScrimService;

/**
 * 
 * @author Lesys
 *
 * Class manager for all the other EventER classes. It chooses which event has to be triggered and checks permissions of changes.
 */
@Controller
public class EventERManager extends ListenerAdapter {
	protected List<String> keywords = Arrays.asList("scrim", "tournament", "union");
	
	private final transient ScrimService scrimService;
	
	private final transient EventERAddPlayer eventERAddPlayer;
	
	private final transient EventERChangeDak eventERChangeDak;
	
	private final transient EventERChangeDisplayName eventERChangeDisplayName;
	
	private final transient EventERChangePlayerName eventERChangePlayerName;
	
	private final transient EventERChangePriority eventERChangePriority;
	
	private final transient EventERDisplayAllPlayersInformations eventERDisplayAllPlayersInformations;
	
	private final transient EventERExportScrim eventERExportScrim;
	
	private final transient EventERExportScrimSnake eventERExportScrimSnake;
	
	private final transient EventERGetSelectedTeams eventERGetSelectedTeams;
	
	private final transient EventERGetSelectedTeamsSnake eventERGetSelectedTeamsSnake;
	
	private final transient EventERRegisterTeam eventERRegisterTeam;
	
	private final transient EventERRegisterTeamPriorityHigh eventERRegisterTeamPriorityHigh;
	
	private final transient EventERRegisterTeamPriorityLow eventERRegisterTeamPriorityLow;
	
	private final transient EventERRemovePlayer eventERRemovePlayer;
	
	private final transient EventERRemoveScrim eventERRemoveScrim;
	
	private final transient EventERRemoveTeam eventERRemoveTeam;
	
	/**
	 * The static variable with every EventER we want to be active
	 */
	public List<EventER> commands;

	@Autowired
	public EventERManager(ScrimService scrimService,
			EventERAddPlayer eventERAddPlayer, EventERChangeDak eventERChangeDak, EventERChangeDisplayName eventERChangeDisplayName, EventERChangePlayerName eventERChangePlayerName,
			EventERChangePriority eventERChangePriority, EventERDisplayAllPlayersInformations eventERDisplayAllPlayersInformations,
			EventERExportScrim eventERExportScrim, EventERExportScrimSnake eventERExportScrimSnake, EventERGetSelectedTeams eventERGetSelectedTeams, EventERGetSelectedTeamsSnake eventERGetSelectedTeamsSnake,
			EventERRegisterTeam eventERRegisterTeam, EventERRegisterTeamPriorityHigh eventERRegisterTeamPriorityHigh, EventERRegisterTeamPriorityLow eventERRegisterTeamPriorityLow,
			EventERRemovePlayer eventERRemovePlayer, EventERRemoveScrim eventERRemoveScrim, EventERRemoveTeam eventERRemoveTeam) {
		
		this.scrimService = scrimService;
		
		this.eventERAddPlayer = eventERAddPlayer;
		this.eventERChangeDak = eventERChangeDak;
		this.eventERChangeDisplayName = eventERChangeDisplayName;
		this.eventERChangePlayerName = eventERChangePlayerName;
		this.eventERChangePriority = eventERChangePriority;
		this.eventERDisplayAllPlayersInformations = eventERDisplayAllPlayersInformations;
		this.eventERExportScrim = eventERExportScrim;
		this.eventERExportScrimSnake = eventERExportScrimSnake;
		this.eventERGetSelectedTeams = eventERGetSelectedTeams;
		this.eventERGetSelectedTeamsSnake = eventERGetSelectedTeamsSnake;
		this.eventERRegisterTeam = eventERRegisterTeam;		
		this.eventERRegisterTeamPriorityHigh = eventERRegisterTeamPriorityHigh;
		this.eventERRegisterTeamPriorityLow = eventERRegisterTeamPriorityLow;
		this.eventERRemovePlayer = eventERRemovePlayer;
		this.eventERRemoveTeam = eventERRemoveTeam;
		this.eventERRemoveScrim = eventERRemoveScrim;

		this.commands = Arrays.asList(
			this.eventERAddPlayer,
//			this.eventERAddSpectator,
			this.eventERChangeDak,
			this.eventERChangeDisplayName,
			this.eventERChangePlayerName,
			this.eventERChangePriority,
//			this.eventERClearPlayer,
			this.eventERDisplayAllPlayersInformations,
			this.eventERExportScrim,
			this.eventERExportScrimSnake,
//			this.eventERGetCommonGames,
//			this.eventERGetBestCharacter,
//			this.eventERGetBestTeammate,
//			this.eventERGetNicknameHistory,
//			this.eventERGetLogs,
//			this.eventERGetRank,
//			this.eventERGetServerDistribution,
//			this.eventERChangeCaptain,
//			this.eventERGetUnionStats,
//			this.eventERHelpCommand,
//			this.eventERPutToSub,
			this.eventERRegisterTeam,
			this.eventERRegisterTeamPriorityHigh,
			this.eventERRegisterTeamPriorityLow,
//			this.eventERGetRegisteredTeams,
//			this.eventERGetRegisteredTeamsForceUpdate,
			this.eventERGetSelectedTeams,
			this.eventERGetSelectedTeamsSnake,
			this.eventERRemovePlayer,
			this.eventERRemoveScrim,
			this.eventERRemoveTeam
		);
	}
	
	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (!event.isFromType(ChannelType.TEXT) || !this.keywords.stream().anyMatch(event.getChannel().getName()::contains)) {
			return;
		} else {
			EventER command = this.commands.stream().filter(comm -> comm.matchingName(event.getMessage().getContentRaw().split(" ")[0])).findFirst().orElse(null);

			if (command != null) {
				// Reset the commandSuccessful value to false
				command.setCommandSuccessful(false);
				
				System.out.println("[EventERManager] starting command for " + command.getClass().getSimpleName());
				this.addThinkingReaction(event);
				
				command.executeCommand(event);
				
				System.out.println("[EventERManager] ending command for " + command.getClass().getSimpleName());
				this.removeThinkingReaction(event);
				
				// Sends the final reaction depending if the command was successful or not
				command.sendFinalReaction(event);
			} else if (event.getMessage().getContentRaw().startsWith(EventER.commandPrefix)) {
				event.getChannel().sendMessage("The command **" + event.getMessage().getContentRaw().split(" ")[0] + "** is not recognized. Please use the correct spelling or use **" + EventER.commandPrefix + "help** to see the commands.").queue();
			}
		}
	}
	
	/**
	 * If a channel is deleted and it's a scrim channel, removes the scrim from the list in case the Administrator forgot to do so manually
	 */
	@Override
	public void onChannelDelete(@NotNull ChannelDeleteEvent event) {
		if (!event.isFromType(ChannelType.TEXT) || !this.keywords.stream().anyMatch(event.getChannel().getName()::contains)) {
			return;
		} else {
			String channelName = event.getChannel().getName();
			String discordName = event.getGuild().getName();
			String channelId = event.getChannel().getId();
			
			System.out.println("[EventERManager] starting deletion of scrim channel for : " + channelName + " of ID " + channelId);
			
			ScrimDTO scrim = this.scrimService.getByChannelId(channelId);
			
			if (scrim != null) {
				this.scrimService.delete(scrim);
				System.err.println("[EventERManager] Scrim " + channelName + " from server " + discordName + " has been removed after the closure of the channel.");
			}
			
			System.out.println("[EventERManager] ending deletion of scrim channel for : " + channelName + " of ID " + channelId);
		}
	}
	
	public void addThinkingReaction(@NotNull MessageReceivedEvent event) {
		// First time sending a reaction, no need to queue with time
		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();
	}
	
	public void removeThinkingReaction(@NotNull MessageReceivedEvent event) {
		// Queue with 2s timer to be sure to remove after the add has been finished 
		event.getMessage().removeReaction(Emoji.fromUnicode("U+1F504")).queueAfter(2, TimeUnit.SECONDS);
	}

	/**
	 * Check if the sender of the event has the permissions to change something about the Team registered
	 * @param event		The event sent (to get the Author and the channel name (== scrim name))
	 * @param teamName	The name of the Team on which the changes will occur
	 * @return			true if the Author of the event has the rights (either the captain of an Administrator), false if not
	 */
	public static boolean hasPermission(@NotNull MessageReceivedEvent event, String teamName) {
		ScrimDTO scrim = Bot.app.getBean(ScrimService.class).getByEvent(event);
		
		if (scrim == null) return false;
		
		return EventERManager.hasPermission(event, scrim.getTeam(teamName));
	}

	/**
	 * Check if the sender of the event has the permissions to change something about the Team registered
	 * @param event		The event sent (to get the Author and the channel name (== scrim name))
	 * @param team		The Team on which the changes will occur
	 * @return			true if the Author of the event has the rights (either the captain or an Administrator), false if not
	 */
	public static boolean hasPermission(@NotNull MessageReceivedEvent event, TeamDTO team) {
		return (team != null && team.getCaptain() != null && event.getAuthor().getName().equalsIgnoreCase(team.getCaptain().getDiscordName()))
			|| (event.getGuild().getMemberById(event.getMessage().getAuthor().getId()).getPermissions().contains(Permission.ADMINISTRATOR));
	}

	/**
	 * Check if the sender of the event has the permissions to change something about a ERPlayer registered
	 * @param event		The event sent (to get the Author and the channel name (== scrim name))
	 * @param player	The ERPlayer on which the changes will occur
	 * @return			true if the Author of the event has the rights (either the player himself or an Administrator), false if not
	 */
	public static boolean hasPermission(@NotNull MessageReceivedEvent event, ERPlayerDTO player) {
		return (player != null && player.getDiscordName().equalsIgnoreCase(event.getAuthor().getName()))
			|| (event.getGuild().getMemberById(event.getMessage().getAuthor().getId()).getPermissions().contains(Permission.ADMINISTRATOR));
	}

	/**
	 * Check if the sender of the event has the permissions to change something about the command
	 * @param event		The event sent (to get the Author and the channel name (== scrim name))
	 * @return			true if the Author of the event has the rights (== Administrator), false if not
	 */
	public static boolean hasPermission(MessageReceivedEvent event) {
		return event.getGuild().getMemberById(event.getMessage().getAuthor().getId()).getPermissions().contains(Permission.ADMINISTRATOR);
	}
}
