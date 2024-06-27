package zzc.discord.evabot.events;


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
		
		String[] message = this.getMessageArray(event);
		
		if (message.length == 2) {
			String discordName = event.getMessage().getMentions().getMembers().size() == 2 ? event.getMessage().getMentions().getMembers().get(0).getUser().getName() : message[message.length - 2];
			
			final String finalPlayerName = discordName;
			String newPlayerDiscordName = event.getMessage().getMentions().getMembers().size() == 2 ? event.getMessage().getMentions().getMembers().get(1).getUser().getName() : message[message.length - 1];
			ERPlayer player = ERPlayer.getERPlayerByDiscordName(discordName);
			
			if (player != null) {
				if (!ERPlayer.alreadyRegistered(newPlayerDiscordName, event.getGuild().getName(), event.getChannel().getName())) {
					if (EventERManager.hasPermission(event, player)) {
						player.setDiscordName(newPlayerDiscordName);
						
						Bot.scrims.stream().flatMap(scrim -> scrim.getTeams().stream()).filter(team -> team.getPlayerNames().stream().anyMatch(p -> p.equalsIgnoreCase(discordName)) || (team.getSub() != null && team.getSub().equalsIgnoreCase(discordName)))
							.forEach(team -> {
								if (team.getSub() != null && team.getSub().equalsIgnoreCase(discordName))
									team.setSub(player);
								else {
									team.removePlayer(discordName);
									team.addPlayer(player);
								}
							});
						Bot.getScrim(event).addLogs(new MessageLog(event.getMessage()));
						Bot.serializeScrims();
						
						event.getMessage().addReaction(Emoji.fromUnicode("U+2705")).queue();
					} else {
						event.getChannel().sendMessage(event.getAuthor().getAsMention() + " does not have the rights to use this command. Only " + player.getDiscordName() + " can use it.").queue();
					}
				} else {
					event.getChannel().sendMessage(newPlayerDiscordName + " has already been registered for this scrim.").queue();
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
