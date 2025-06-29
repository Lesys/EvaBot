package zzc.discord.evabot.events;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.Bot;
import zzc.discord.evabot.ERPlayer;
import zzc.discord.evabot.MessageLog;
import zzc.discord.evabot.Scrim;
import zzc.discord.evabot.Team;

/**
 * 
 * @author Lesys
 *
 *         Class of EventER when the user wants to get the selected teams for a
 *         scrim
 */
public class EventERDisplayAllPlayersInformations extends EventER {
	/**
	 * Constructor of EventERGetSelectedTeams
	 */
	public EventERDisplayAllPlayersInformations() {
		this.commandName += "getAllPlayersInfo";
	}
	
	/**
	 * Gets all the teams from the serialized variable and sort them by MMR if the option was added in the command line. Changes the roles if a role has been mentioned
	 */
	@Override
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		final List<String> messages = new ArrayList<String>();
		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();
		Bot.deserializePlayers();
		
		final StringBuilder builder = new StringBuilder();
		builder.append("All players currently in database:\n");
		
		List<ERPlayer> allPlayers = Bot.allPlayers;
		/*System.err.println("Members: ");
		event.getGuild().getMembers().forEach(t-> System.err.println(t.getUser().getName()));*/
		if (allPlayers != null) {
			allPlayers.forEach(player -> {
				System.err.println("Player discord name: " + player.getDiscordName() + " / DAK name: " + player.getDakName() + "\n");
				if (builder.length() > 0 && builder.length() >= 1800) {
					messages.add(builder.toString());
					builder.delete(0, builder.length());
				}
				builder.append("Player discord name: " + ERPlayer.getNameWithoutSpecialChar(player::getDiscordName) + " / DAK name: " + player.getDakName() + "\n");
			});
			
			messages.add(builder.toString());
	        messages.forEach(m -> sendMessageWait(event, m));
		} else {			
			event.getChannel().sendMessage("There are currently no player in the database.").queue();
		}
		
		event.getMessage().removeReaction(Emoji.fromUnicode("U+1F504")).queueAfter(2, TimeUnit.SECONDS);
	}
	
	@Override
	public String helpCommand() {
		return super.helpCommand()
				+ " - Returns all the players currently registered in the database with the informations about them (Discord name and DAK name)).\n";
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
	protected static void teamStringBuilder(final StringBuilder builder, Team team, AtomicInteger placement,
			MessageReceivedEvent event) {
		builder.append("\n" + placement.getAndIncrement() + "°) **__" + team.getName() + "__** (" + team.getAverage()
		+ "):\n");
		team.getPlayerNames().stream().map(p -> ERPlayer.getERPlayer(p))
		.forEach(
				player -> {System.err.println("Player getselectedteams: " + player.getDak() + "; " + player.getDisplayName());builder.append((team.getCaptain().equalsIgnoreCase(player.getDiscordName()) ? "__" : "")
						+ EventERDisplayAllPlayersInformations.getMention(event, player)
						+ (team.getCaptain().equalsIgnoreCase(player.getDiscordName()) ? "__" : "")
						+ " (" + ERPlayer.getNameWithoutSpecialChar(player::getDakName) + " - " + player.getMmr() + "); ");});
		ERPlayer sub = ERPlayer.getERPlayer(team.getSub());
		if (sub != null) {
			builder.append("[Sub: " + (team.getCaptain().equalsIgnoreCase(sub.getDiscordName()) ? "__" : "")
					+ EventERDisplayAllPlayersInformations.getMention(event, sub)
					+ (team.getCaptain().equalsIgnoreCase(sub.getDiscordName()) ? "__" : "")
					+ " (" + ERPlayer.getNameWithoutSpecialChar(sub::getDakName) + " - " + sub.getMmr() + ")]");
		}
	}
	
	/**
	 * Returns the string corresponding to the mention of the member
	 * 
	 * @param event		The event sent to be able to mention people
	 * @param player 	The player to mention
	 * @return The mention (or a String if member is null)
	 */
	protected static String getMention(MessageReceivedEvent event, ERPlayer player) {
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
}
