package zzc.discord.evabot.events;

import java.util.*;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.User;
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
 *         Class of EventER that registers a Team for a scrim
 */
public class EventERTeamRegistration extends EventER {
	protected Team team;
	/**
	 * Constructor of EventGuildMessageTeamRegistration
	 */
	public EventERTeamRegistration() {
		this.commandName += "register";
	}
	
	/**
	 * Gets the Team name, the ERPlayer names and DAK, retrieves the exact MMR of
	 * each player (DAK name) from the ER API, and creates a Team with all those
	 * players
	 */
	@Override
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();
		Bot.deserializeScrims();
		String channelName = event.getChannel().getName();
		String discordServerName = event.getGuild().getName();
		List<String> names = event.getMessage().getContentRaw().lines().toList(); //Change to getContentDisplay
		List<User> members = new ArrayList<User>();
		if (names.size() > 1) {
			String teamName = names.get(0).split(
					"(?i)".concat((Arrays.asList("+", "*", "?", "^", "$", "(", ")", "[", "]", "{", "}", "|", "\\")
							.contains(this.commandName.substring(0, 1)) ? "\\" : "") + this.commandName + " "))[1]
					.trim();
			
			Scrim scrim = Bot.getScrim(event);
			
			if (Bot.getTeam(event, teamName) == null) {
				List<String> playerNames = new ArrayList<String>();
				
				for (int i = 1; i < names.size(); i++)
					if (!names.get(i).trim().isEmpty() && names.get(i).trim().replaceAll(" +", " ").split(" ").length == 2)
						playerNames.add(names.get(i).trim().replaceAll(" +", " "));
					
				if (event.getMessage().getMentions().getUsers().size() == playerNames.size())
					members.addAll(event.getMessage().getMentions().getUsers());
				
				this.team = new Team(teamName);
				this.team.setCaptain(event.getMessage().getAuthor().getName());
				boolean registered = false;
				// System.err.println("Author: " + event.getMessage().getAuthor().getName());
				// System.err.println("Players: " + playerNames.size() + "; " +
				// playerNames.get(0));
				playerNames.stream().forEach(p -> System.err.println(p + "; "));
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
						ERPlayer player = ERPlayer.getERPlayer(ign);
						
						player.setDiscordName(discordName);
						// new ERPlayer(playerName, dak);
						return !ERPlayer.alreadyRegistered(ign, discordServerName, channelName)
								? (this.team.addPlayer(player) ? true
										: (this.team.getSub() == null ? this.team.setSub(player) : false))
								: false;
					});
				} catch (ArrayIndexOutOfBoundsException e) {
					registered = false;
				}
				
				if (registered) {
					this.preExecuteCommand(event);
					Bot.serializePlayers();
					if (scrim == null) {
						scrim = new Scrim(event.getGuild().getName(), channelName);
						Bot.scrims.add(scrim);
					}
					scrim.addTeam(this.team);
					
					scrim.addLogs(new MessageLog(event.getMessage()));
					Bot.serializeScrims();
					this.postExecuteCommand(event);
					
					event.getChannel()
							.sendMessage("**" + teamName + "** has been registered for the " + channelName + " scrim.")
							.queue();
					
					event.getMessage().addReaction(Emoji.fromUnicode("U+2705")).queue();
				} else {
					event.getChannel().sendMessage(
							"Your team members couldn't get saved correctly in the Team. Make sure you don't exceed the maximum number of player for a Team and none of the players are registered in another Team.")
							.queue();
					
					event.getMessage().addReaction(Emoji.fromUnicode("U+274C")).queue();
				}
			} else {
				event.getChannel().sendMessage("A team with the name **" + teamName
						+ "** has already been registered for the " + channelName
						+ " scrim. Please add players using the right command if you are the captain of the Team, or change your Team name and try again.")
						.queue();
				
				event.getMessage().addReaction(Emoji.fromUnicode("U+274C")).queue();
			}
		} else {
			event.getChannel().sendMessage(
					"Please enter the correct format of team registration (see help command for more information).")
					.queue();
		}
		
		event.getMessage().removeReaction(Emoji.fromUnicode("U+1F504")).queue();
	}
	
	@Override
	public String helpCommand() {
		return super.helpCommand()
				+ " {TeamName} {Player1DiscordTag} {Player1DAKLink}... - Registers a team with the players for the scrim. Please return to line after TeamName argument and write only 1 player per row. Tag can be the Discord unique name only if the player is not in the current server.\nCommand use example: "
				+ super.helpCommand()
				+ " TeamName\nPlayer1DiscordTag https.../Player1AccountName\nPlayer2DiscordTag https.../Player2AccountName\nPlayer3DiscordTag https.../Player3AccountName\n";
	}
}
