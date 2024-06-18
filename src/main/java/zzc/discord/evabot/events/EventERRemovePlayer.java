package zzc.discord.evabot.events;


import org.jetbrains.annotations.NotNull;

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
	public void exeuteCommand(@NotNull MessageReceivedEvent event) {
		Bot.deserializeScrims();
		
		String[] message = this.getMessageArray(event);	

		String teamName = "";
		for (int i = 0; i < message.length - 1; i++)
			teamName += message[i] + (i < message.length - 2 ? " " : "");
		
		String playerName = message[message.length - 1];
		
		final String finalPlayerName = playerName;

		Team team = Bot.getTeam(event, teamName);
		if (team != null) {
			ERPlayer player = team.getPlayers().stream().filter(p -> p.getDiscordName().equalsIgnoreCase(finalPlayerName)).findFirst().orElse(null);
			
			if (player != null) {
				if (EventERManager.hasPermission(event, teamName)) {
					if (team.removePlayer(finalPlayerName)) {
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
				event.getChannel().sendMessage(finalPlayerName + " hasn't been registered in this Team for this scrim.").queue();
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
