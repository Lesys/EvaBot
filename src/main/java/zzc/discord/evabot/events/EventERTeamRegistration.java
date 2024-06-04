package zzc.discord.evabot.events;

import java.util.*;

import org.jetbrains.annotations.NotNull;

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
 * Class of EventER that registers a Team for a scrim
 */
public class EventERTeamRegistration extends EventER {
	/**
	 * Constructor of EventGuildMessageTeamRegistration
	 */
	public EventERTeamRegistration() {
		this.commandName += "register";
	}
	
	/**
	 * Gets the Team name, the ERPlayer names and DAK, retrieves the exact MMR of each player (DAK name) from the ER API, and creates a Team with all those players
	 */
	@Override
	public void exeuteCommand(@NotNull MessageReceivedEvent event) {
		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();
		Bot.deserializeScrims();
		String channelName = event.getChannel().getName();
		List<String> names = event.getMessage().getContentRaw().lines().toList();
		if (names.get(0).length() > 1) {
			String teamName = names.get(0).split("(?i)".concat((Arrays.asList("+" , "*" , "?" , "^" , "$" , "(" , ")" , "[" , "]" , "{" , "}" , "|" , "\\").contains(this.commandName.substring(0, 1)) ? "\\" : "") + this.commandName + " "))[1];
			
			Scrim scrim = Bot.getScrim(event);
			
			if (Bot.getTeam(event, teamName) == null) {
				List<String> playerNames = new ArrayList<String>();
				
				for (int i = 1; i < names.size(); i++)
					playerNames.add(names.get(i));
				
	
				Team team = new Team(teamName);
				team.setCaptain(event.getMessage().getAuthor().getName());
				boolean registered = false;
				System.err.println("Players: " + playerNames.size() + "; " + names.get(1));
				playerNames.stream().forEach(p -> System.err.println(p + "; "));
				registered = playerNames.stream().allMatch(row -> {
					String playerName = row.split(" ")[0];
					String dak = row.split(" ")[1];
					System.err.println("Player: " + playerName + "; dak: " + dak + "; Captain name: " + event.getMessage().getAuthor().getName());
					ERPlayer player = new ERPlayer(playerName, dak);
					return !ERPlayer.alreadyRegistered(playerName, channelName) ?
							(team.addPlayer(player) ? true :
								(team.getSub() == null ? team.setSub(player)
								: false))
							: false;
				});
	
				if (registered) {
					if (scrim == null) {
						scrim = new Scrim(channelName);
						Bot.scrims.add(scrim);
					}
					scrim.addTeam(team);

					scrim.addLogs(new MessageLog(event.getMessage()));
					Bot.serializeScrims();
					
					event.getChannel().sendMessage("**" + teamName + "** has been registered for the " + channelName + " scrim.").queue();
	
					event.getMessage().addReaction(Emoji.fromUnicode("U+2705")).queue();
				} else {
					event.getChannel().sendMessage("Your team members couldn't get saved correctly in the Team. Make sure you don't exceed the maximum number of player for a Team and none of the players are registered in another Team.").queue();
	
					event.getMessage().addReaction(Emoji.fromUnicode("U+274C")).queue();
				}
			} else {
				event.getChannel().sendMessage("A team with the name **" + teamName + "** has already been registered for the " + channelName + " scrim. Please add players using the right command if you are the captain of the Team, or change your Team name and try again.").queue();

				event.getMessage().addReaction(Emoji.fromUnicode("U+274C")).queue();
			}
		} else {
			event.getChannel().sendMessage("Please enter the correct format of team registration (see help command for more information).").queue();
		}
		
		event.getMessage().removeReaction(Emoji.fromUnicode("U+1F504")).queue();
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " {TeamName} {Player1Name} {Player1DAK}... - Registers a team with the players for the scrim. Please return to line after TeamName argument and write only 1 player per row.\nCommand use example: !register TeamName\nPlayer1Name https.../Player1Name\nPlayer2Name https.../Player2Name\nPlayer2Name https.../Player2Name\n";
	}
}
