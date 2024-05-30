package zzc.discord.evabot.events;


import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.Bot;
import zzc.discord.evabot.Team;

/**
 * 
 * @author Lesys
 *
 * Class of EventER when the user wants to get the registered teams for a scrim 
 */
public class EventERGetRegisteredTeamsForceUpdate extends EventERGetRegisteredTeams {
	/**
	 * Constructor of EventGuildMessageGetRegisteredTeams
	 */
	public EventERGetRegisteredTeamsForceUpdate() {
		this.commandName += "registeredTeamsForce";
	}
	
	/**
	 * Gets all the teams from the serialized variable, force the update of their MMR and sort them by MMR
	 */
	@Override
	public void exeuteCommand(@NotNull MessageReceivedEvent event) {
		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();
		Bot.deserializeScrims();

        final StringBuilder builder = new StringBuilder();
        builder.append("Registered teams for the scrim \"" + event.getChannel().getName() + "\":\n");

		List<Team> teams = Bot.getScrim(event);
		if (teams != null) {
			if (event.getGuild().getMemberById(event.getMessage().getAuthor().getId()).getPermissions().contains(Permission.ADMINISTRATOR)) {
				teams.stream().forEach(team -> {
					team.updateMmrForce();
				});
				
				teams.stream().sorted(Comparator.reverseOrder()).forEach(team -> {
					EventERGetRegisteredTeamsForceUpdate.teamStringBuilder(builder, team);
				});
		
				Bot.serializeScrims();
				
				event.getChannel().sendMessage(builder).queue();
			} else {
				event.getChannel().sendMessage("Only an Administrator can use this command.").queue();				
			}
		} else {			
			event.getChannel().sendMessage("No teams has yet to be registered for the scrim \"" + event.getChannel().getName() + "\".").queue();
		}
		
		event.getMessage().removeReaction(Emoji.fromUnicode("U+1F504")).queue();
	}

	@Override
	public String helpCommand() {
		return "**" + this.commandName + "**" + " - Forces the MMR to update and returns all the teams registered with their average MMR and players registered in the team with their own MMR.\n";
	}
}
