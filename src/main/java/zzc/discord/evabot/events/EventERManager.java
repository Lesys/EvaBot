package zzc.discord.evabot.events;

import java.util.*;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import zzc.discord.evabot.Bot;
import zzc.discord.evabot.ERPlayer;
import zzc.discord.evabot.Scrim;
import zzc.discord.evabot.Team;

/**
 * 
 * @author Lesys
 *
 * Class manager for all the other EventER classes. It chooses which event has to be triggered and checks permissions of changes.
 */
public class EventERManager extends ListenerAdapter {
	/**
	 * The static variable with every EventER we want to be active
	 */
	public static List<EventER> commands = Arrays.asList(
			new EventERAddPlayer(),
			new EventERAddSpectator(),
			new EventERChangeDak(),
			new EventERChangeDisplayName(),
			new EventERChangePlayerName(),
			new EventERClearPlayer(),
			new EventERDisplayAllPlayersInformations(),
			new EventERExportScrim(),
			new EventERGetCommonGames(),
			new EventERGetBestCharacter(),
			new EventERGetBestTeammate(),
			new EventERGetGameInfo(),
			new EventERGetLogs(),
			new EventERGetRank(),
			new EventERGetServerDistribution(),
			new EventERChangeCaptain(),
			new EventERHelpCommand(),
			new EventERPutToSub(),
			new EventERTeamRegistration(),
			new EventERGetRegisteredTeams(),
			new EventERGetRegisteredTeamsForceUpdate(),
			new EventERGetSelectedTeams(),
			new EventERRemovePlayer(),
			new EventERRemoveScrim(),
			new EventERRemoveTeam()
		);

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (!event.isFromType(ChannelType.TEXT) || !event.getChannel().getName().contains("scrim")) {
			return;
		} else {
			EventER command = EventERManager.commands.stream().filter(comm -> comm.matchingName(event.getMessage().getContentRaw().split(" ")[0])).findFirst().orElse(null);

			if (command != null) {
				command.executeCommand(event);
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
		if (!event.isFromType(ChannelType.TEXT) || !event.getChannel().getName().contains("scrim")) {
			return;
		} else {
			System.out.println("Deleting scrim channel; name: " + event.getChannel().getName());
			Bot.deserializeScrims();
			
			Scrim scrim = Bot.getScrim(event.getGuild().getName(), event.getChannel().getName());
			if (scrim != null) {
				Bot.scrims.remove(scrim);
				Bot.serializeScrims();
				System.err.println("Scrim " + scrim.getName() + " has been removed after the closure of the channel.");
			}
		}
	}

	/**
	 * Check if the sender of the event has the permissions to change something about the Team registered
	 * @param event		The event sent (to get the Author and the channel name (== scrim name))
	 * @param teamName	The name of the Team on which the changes will occur
	 * @return			true if the Author of the event has the rights (either the captain of an Administrator), false if not
	 */
	public static boolean hasPermission(@NotNull MessageReceivedEvent event, String teamName) {
		Team team = Bot.getTeam(event, teamName);
		return (team != null && team.getCaptain() != null && team.getCaptain().equals(event.getAuthor().getName()))
			|| (event.getGuild().getMemberById(event.getMessage().getAuthor().getId()).getPermissions().contains(Permission.ADMINISTRATOR));
	}

	/**
	 * Check if the sender of the event has the permissions to change something about the Team registered
	 * @param event		The event sent (to get the Author and the channel name (== scrim name))
	 * @param team		The Team on which the changes will occur
	 * @return			true if the Author of the event has the rights (either the captain or an Administrator), false if not
	 */
	public static boolean hasPermission(@NotNull MessageReceivedEvent event, Team team) {
		return (team != null && team.getCaptain() != null && team.getCaptain().equals(event.getAuthor().getName()))
			|| (event.getGuild().getMemberById(event.getMessage().getAuthor().getId()).getPermissions().contains(Permission.ADMINISTRATOR));
	}

	/**
	 * Check if the sender of the event has the permissions to change something about a ERPlayer registered
	 * @param event		The event sent (to get the Author and the channel name (== scrim name))
	 * @param player	The ERPlayer on which the changes will occur
	 * @return			true if the Author of the event has the rights (either the player himself or an Administrator), false if not
	 */
	public static boolean hasPermission(@NotNull MessageReceivedEvent event, ERPlayer player) {
		return (player != null && player.getDiscordName().equals(event.getAuthor().getName()))
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
