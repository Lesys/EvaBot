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
 * Class of EventER that swaps a player of the main roster for the sub (if there is one, else just put the player to sub).
 */
public class EventERPutToSub extends EventER {
	/**
	 * Constructor of EventGuildMessageRemovePlayer
	 */
	public EventERPutToSub() {
		this.commandName += "putToSub";
	}
	
	/**
	 * Check if the Team name exists, check if the Player name exists in this Team and swap it with the sub Player if there is one, else puts it in sub with an empty place in main roster
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
					if (team.getSub() != null) {
						ERPlayer sub = ERPlayer.getERPlayerByDiscordName(team.getSub());
						team.setSub(player);
						team.addPlayer(sub);
					} else {
						team.setSub(player);
					}

					Bot.getScrim(event).addLogs(new MessageLog(event.getMessage()));
					Bot.serializeScrims();
					
					event.getMessage().addReaction(Emoji.fromUnicode("U+2705")).queue();
				} else {
					event.getChannel().sendMessage(event.getAuthor().getAsMention() + " does not have the rights to use this command. Only the captain of the team can use it.").queue();
				}
			} else {
				event.getChannel().sendMessage("The player " + finalPlayerName + " is not registered in this Team main roster.").queue();
			}
		} else {
			event.getChannel().sendMessage(teamName + " hasn't been registered in this scrim.").queue();
		}
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " {TeamName} {PlayerDiscordNameToSub} - Changes a player from the main Team to sub. If there is a sub player registered, puts the sub to the main Team.\n";
	}
}
