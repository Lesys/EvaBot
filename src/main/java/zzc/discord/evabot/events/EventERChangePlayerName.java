package zzc.discord.evabot.events;


import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.Bot;
import zzc.discord.evabot.ERPlayer;
import zzc.discord.evabot.Team;

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
		Bot.deserializeScrims();
		
		String[] message = event.getMessage().getContentRaw().split("(?i)".concat((Arrays.asList("+" , "*" , "?" , "^" , "$" , "(" , ")" , "[" , "]" , "{" , "}" , "|" , "\\").contains(this.commandName.substring(0, 1)) ? "\\" : "") + this.commandName + " "))[1].split(" ");

		String teamName = "";
		for (int i = 0; i < message.length - 2; i++)
			teamName += message[i] + (i < message.length - 3 ? " " : "");
		
		String playerName = message[message.length - 2];
		
		final String finalPlayerName = playerName;
		String newPlayerName = message[message.length - 1];

		Team team = Bot.getTeam(event, teamName);
		if (team != null) {
			ERPlayer player = team.getPlayers().stream().filter(p -> p.getName().equalsIgnoreCase(finalPlayerName)).findFirst().orElse(null);
			player = player == null ? (team.getSub().getName().equalsIgnoreCase(finalPlayerName) ? team.getSub() : null) : player;
			
			if (player != null) {
				if (EventERManager.hasPermission(event, teamName)) {
					player.setName(newPlayerName);
					
					Bot.serializeScrims();
					
					event.getMessage().addReaction(Emoji.fromUnicode("U+2705")).queue();
				} else {
					event.getChannel().sendMessage(event.getAuthor().getAsMention() + " does not have the rights to use this command. Only the captain of the team can use it.").queue();
				}
			} else {
				event.getChannel().sendMessage(finalPlayerName + " hasn't been registered in this Team.").queue();
			}
		} else {
			event.getChannel().sendMessage(teamName + " hasn't been registered in this scrim.").queue();
		}
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " {TeamName} {CurrentPlayerName} {NewPlayerName} - Changes the name of a player registered.\n";
	}
}
