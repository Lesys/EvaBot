package zzc.discord.evabot.events;


import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.Bot;
import zzc.discord.evabot.ERPlayer;
import zzc.discord.evabot.MessageLog;

/**
 * 
 * @author Lesys
 *
 * Class of EventER that changes the name of a registered ERPlayer
 */
public class EventERChangePlayerName extends EventER {
	/**
	 * Constructor of EventERChangePlayerName
	 */
	public EventERChangePlayerName() {
		this.commandName += "changePlayerName";
	}
	
	/**
	 * Check if the Player name exists in the registered teams, and changes the ERPlayer name if it exists in a Team
	 */
	@Override
	public void exeuteCommand(@NotNull MessageReceivedEvent event) {
		Bot.deserializePlayers();
		Bot.deserializeScrims();

		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();
		
		String[] message = event.getMessage().getContentRaw().split("(?i)".concat((Arrays.asList("+" , "*" , "?" , "^" , "$" , "(" , ")" , "[" , "]" , "{" , "}" , "|" , "\\").contains(this.commandName.substring(0, 1)) ? "\\" : "") + this.commandName + " "))[1].split(" ");

		if (message.length == 2) {
			String playerDiscordName = message[message.length - 2];
			
			final String finalPlayerName = playerDiscordName;
			String newPlayerDiscordName = message[message.length - 1];
			
			ERPlayer player = ERPlayer.getERPlayerByDiscordName(playerDiscordName);
			
			if (player != null) {
				if (EventERManager.hasPermission(event, player)) {
					player.setDiscordName(newPlayerDiscordName);
					
					Bot.scrims.stream().flatMap(scrim -> scrim.getTeams().stream()).filter(team -> team.getPlayerNames().stream().anyMatch(p -> p.equalsIgnoreCase(playerDiscordName)) || (team.getSub() != null && team.getSub().equalsIgnoreCase(playerDiscordName)))
						.forEach(team -> {
							if (team.getSub() != null && team.getSub().equalsIgnoreCase(playerDiscordName))
								team.setSub(player);
							else {
								team.removePlayer(playerDiscordName);
								team.addPlayer(player);
							}
						});
					Bot.getScrim(event).addLogs(new MessageLog(event.getMessage()));
					Bot.serializeScrims();
					Bot.serializePlayers();
					
					event.getMessage().addReaction(Emoji.fromUnicode("U+2705")).queue();
				} else {
					event.getChannel().sendMessage(event.getAuthor().getAsMention() + " does not have the rights to use this command. Only the captain of the team can use it.").queue();
				}
			} else {
				event.getChannel().sendMessage(finalPlayerName + " hasn't been registered in this Team.").queue();
			}
		} else {			
			event.getChannel().sendMessage("Please enter the discord name of the player followed by their new discord name.").queue();
		}

		event.getMessage().removeReaction(Emoji.fromUnicode("U+1F504")).queue();
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " {CurrentPlayerDiscordName} {NewPlayerDiscordName} - Changes the name of a player registered.\n";
	}
}
