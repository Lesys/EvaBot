package zzc.discord.evabot.events;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.Bot;
import zzc.discord.evabot.ERPlayer;
import zzc.discord.evabot.Scrim;
import zzc.discord.evabot.Team;
import zzc.discord.evabot.util.ExcelExport;

/**
 * 
 * @author Lesys
 *
 * Class of EventER that exports a Scrim into CSV format
 */
public class EventERExportScrim extends EventER implements ExcelExport {
	/**
	 * Constructor of EventERGetSelectedTeams
	 */
	public EventERExportScrim() {
		this.commandName += "exportScrim";
	}

	/**
	 * Gets all the teams from the serialized variable and sort them by MMR if the option was added in the command line, then exports all the teams/players to CSV file.
	 */
	@Override
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		event.getMessage().addReaction(Emoji.fromUnicode("U+1F504")).queue();
		Bot.deserializeScrims();
//		builder.append("Registered teams for the scrim \"" + event.getChannel().getName() + "\"\n");

		boolean byMmr = event.getMessage().getContentRaw().trim().replaceAll(" +", " ").split(" ").length > 1
				&& event.getMessage().getContentRaw().trim().replaceAll(" +", " ").split(" ")[1]
						.equalsIgnoreCase("byMmr");
		System.err.println("ByMmr ? " + byMmr);

		Scrim scrim = Bot.getScrim(event);
		/*
		 * System.err.println("Members: "); event.getGuild().getMembers().forEach(t->
		 * System.err.println(t.getUser().getName()));
		 */
		if (scrim != null) {
			if (EventERManager.hasPermission(event)) {

				List<Team> filtered = null;
				if (!byMmr)
					filtered = scrim.getTeams().stream().toList();
				else
					filtered = scrim.getTeams().stream().sorted().toList();

				try {
					ExcelExport.exportLobby(null, filtered, event);

					event.getMessage().addReaction(Emoji.fromUnicode("U+2705")).queue();
				} catch (IOException i) {
					i.printStackTrace();
				}				
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
		return super.helpCommand() + " [byMmr] - Exports all the teams registered for the scrim to a CSV file with the name of the channel. Using the option \"byMmr\" orders teams by MMR average, else returns by registration order..\n";
	}
}
