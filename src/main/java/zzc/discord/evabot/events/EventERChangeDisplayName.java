package zzc.discord.evabot.events;


import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.Bot;
import zzc.discord.evabot.ERPlayer;

/**
 * 
 * @author Lesys
 *
 * Class of EventER that changes the displayed name of a registered ERPlayer
 */
public class EventERChangeDisplayName extends EventER {
	/**
	 * Constructor of EventERChangeDisplayName
	 */
	public EventERChangeDisplayName() {
		this.commandName += "changePlayerDisplayName";
	}
	
	/**
	 * Check if the Player name exists in the registered teams, and changes the ERPlayer name if it exists in a Team
	 */
	@Override
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		Bot.deserializeScrims();

		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();
		
		String[] message = this.getMessageArray(event);
		
		if (message.length >= 2) {
			String newDisplayName = "";
			
			for (int i = 1; i < message.length; i++)
				newDisplayName += message[i];
			String discordName = event.getMessage().getMentions().getMembers().size() == 1 ? event.getMessage().getMentions().getMembers().get(0).getUser().getName() : message[0];
			
			ERPlayer player = ERPlayer.getERPlayerByDiscordName(discordName);
			
			if (player != null) {
				if (EventERManager.hasPermission(event, player)) {
					player.setDisplayName(newDisplayName);
					
					event.getMessage().addReaction(Emoji.fromUnicode("U+2705")).queue();
				} else {
					event.getChannel().sendMessage(event.getAuthor().getAsMention() + " does not have the rights to use this command. Only " + player.getDiscordName() + " can use it.").queue();
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
		return super.helpCommand() + " {CurrentPlayerDiscordName} {NewPlayerDiscordName} - Changes the name of a player registered.\n";
	}
}
