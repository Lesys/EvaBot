package zzc.discord.evabot.events;


import java.util.Arrays;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.Bot;
import zzc.discord.evabot.ERPlayer;
import zzc.discord.evabot.MessageLog;
import zzc.discord.evabot.Team;

/**
 * 
 * @author Lesys
 *
 * Class of EventER that removes an ERPlayer from a team
 */
public class EventERRemovePlayer extends EventER {
	/**
	 * Constructor of EventGuildMessageRemovePlayer
	 */
	public EventERRemovePlayer() {
		this.commandName += "removePlayer";
	}
	
	/**
	 * Check if the Team name exists in the registered teams, and removes the ERPlayer if it exists in the Team
	 */
	@Override
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		Bot.deserializeScrims();
		
		String[] message = this.getMessageArray(event);	

		String teamName = "";
		for (int i = 0; i < message.length - 1; i++)
			teamName += message[i] + (i < message.length - 2 ? " " : "");
		
		String playerName = message[message.length - 1];

		Team team = Bot.getTeam(event, teamName);
		if (team != null) {
			String discordName = playerName;
			if (discordName.startsWith("<@")) { // Check if the name is a Discord tag
				discordName = event.getGuild()
						.getMemberById(discordName.subSequence(2, discordName.length() - 1).toString())
						.getUser().getName(); // Get the name of the unique user tagged
			}
			
			final String finalPlayerName = discordName;
			ERPlayer playerFromDiscordName = ERPlayer.getERPlayerByDiscordName(discordName);
			
			//ERPlayer player = ERPlayer.getERPlayer((team.getSub() != null ? Stream.concat(team.getPlayerNames().stream(), Arrays.asList(team.getSub()).stream()) : team.getPlayerNames().stream()).filter(p -> p.equalsIgnoreCase(finalPlayerName)).findFirst().orElse(null));
			
			if (playerFromDiscordName != null) {
				if (EventERManager.hasPermission(event, teamName)) {
					if (team.removePlayer(playerFromDiscordName)) {
						Bot.getScrim(event).addLogs(new MessageLog(event.getMessage()));
						Bot.serializeScrims();
						
						event.getMessage().addReaction(Emoji.fromUnicode("U+2705")).queue();
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
