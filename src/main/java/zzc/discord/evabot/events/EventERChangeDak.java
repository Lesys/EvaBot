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
 * Class of EventER that changes the DAK of a registered ERPlayer
 */
public class EventERChangeDak extends EventER {
	/**
	 * Constructor of EventERChangeDak
	 */
	public EventERChangeDak() {
		this.commandName += "changePlayerDak";
	}
	
	/**
	 * Check if the Player name exists in the registered teams, and changes the ERPlayer DAK if it exists in a Team
	 */
	@Override
	public void exeuteCommand(@NotNull MessageReceivedEvent event) {
		Bot.deserializeScrims();

		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();
		
		String[] message = event.getMessage().getContentRaw().split("(?i)".concat((Arrays.asList("+" , "*" , "?" , "^" , "$" , "(" , ")" , "[" , "]" , "{" , "}" , "|" , "\\").contains(this.commandName.substring(0, 1)) ? "\\" : "") + this.commandName + " "))[1].split(" ");

		if (message.length == 2) {
			String discordName = message[message.length - 2];
			
			String newDak = message[message.length - 1];
			
			ERPlayer player = ERPlayer.getERPlayerByDiscordName(discordName);
			
			if (player != null) {
				if (EventERManager.hasPermission(event, player)) {
					player.setDak(newDak);

					Bot.getScrim(event).addLogs(new MessageLog(event.getMessage()));
					
					event.getMessage().addReaction(Emoji.fromUnicode("U+2705")).queue();
				} else {
					event.getChannel().sendMessage(event.getAuthor().getAsMention() + " does not have the rights to use this command. Only the captain of the team can use it.").queue();
				}
			} else {
				event.getChannel().sendMessage(discordName + " hasn't been registered in this Team.").queue();
			}
		} else {			
			event.getChannel().sendMessage("Please enter the discord name of the player followed by the dak link (or at least their in game name).").queue();
		}

		event.getMessage().removeReaction(Emoji.fromUnicode("U+1F504")).queue();
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " {PlayerDiscordName} {NewPlayerDakLink} - Changes the DAK of a player registered.\n";
	}
}
