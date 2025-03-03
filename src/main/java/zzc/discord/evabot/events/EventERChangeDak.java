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
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		Bot.deserializeScrims();

		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();
		
		String[] message = this.getMessageArray(event);

		if (message.length == 2) {
			String discordName = event.getMessage().getMentions().getMembers().size() == 1 ? event.getMessage().getMentions().getMembers().get(0).getUser().getName() : null;
			
			String oldDak = discordName == null ? message[message.length - 2] : null;
			
			String newDak = message[message.length - 1];
			
			ERPlayer player = discordName == null ? ERPlayer.getERPlayer(oldDak) : ERPlayer.getERPlayerByDiscordName(discordName);
			
			Bot.getScrim(event);
			Team team = Bot.getScrim(event).getTeams().stream().filter(t -> t.getPlayerNames().stream().anyMatch(pn -> pn.equalsIgnoreCase(player.getDiscordName())) ||
					(t.getSub() != null && t.getSub().equalsIgnoreCase(player.getDiscordName()))).findFirst().orElse(null); // Retrieve team to check if the player is on a team and the captain is the one doing the request
			
			if (player != null) {
				if (EventERManager.hasPermission(event, player) || EventERManager.hasPermission(event, team)) {
					player.setDak(newDak);

					Bot.getScrim(event).addLogs(new MessageLog(event.getMessage()));
					
					event.getMessage().addReaction(Emoji.fromUnicode("U+2705")).queue();
				} else {
					event.getChannel().sendMessage(event.getAuthor().getAsMention() + " does not have the rights to use this command. Only " + player.getDiscordName() + " can use it.").queue();
				}
			} else {
				event.getChannel().sendMessage(discordName == null ? oldDak : discordName + " hasn't been registered in this Team.").queue();
			}
		} else {			
			event.getChannel().sendMessage("Please enter the discord name of the player followed by the dak link (or at least their in game name).").queue();
		}

		this.removeReaction(event, "U+1F504");
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " {PlayerDiscordMention | OldPlayerDakName} {NewPlayerDakLink} - Changes the DAK of a player registered.\n";
	}
}
