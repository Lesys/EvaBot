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

/**
 * 
 * @author Lesys
 *
 * Class of EventER that exports a Scrim into CSV format
 */
public class EventERExportScrim extends EventER {
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

		final StringBuilder builder = new StringBuilder();
		System.out.println(builder.toString());
		builder.append("Registered teams for the scrim \"" + event.getChannel().getName() + "\"\n");
		builder.append("averageMmr;teamName;playerIGName;playerDiscordName;playerMmr\n");

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
					filtered = scrim.getTeams().stream().sorted(Comparator.reverseOrder()).toList();

				filtered.stream().forEach(t -> EventERExportScrim.teamStringBuilder(builder, t, event));

				try {
					String fileName = event.getChannel().getName() + OffsetDateTime.now().format(DateTimeFormatter.ofPattern("_yyyy-MM-dd_HH-mm-ss")).toString() + ".csv";
					FileOutputStream fileOut = new FileOutputStream(fileName);
					OutputStreamWriter out = new OutputStreamWriter(fileOut);
					out.write(builder.toString());
					out.close();
					fileOut.close();
					System.out.println("Serialized data is saved in " + fileName);

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

	/**
	 * Protected method to construct the string to send
	 * 
	 * @param builder   The string builder that is going to be displayed in the message
	 * @param team      The Team for which you want to create the string
	 * @param event     The event sent to be able to mention people
	 */
	protected static void teamStringBuilder(final StringBuilder builder, Team team, MessageReceivedEvent event) {
		team.getPlayerNames().stream().map(p -> ERPlayer.getERPlayer(p)).forEach(
				player -> builder.append(team.getAverage() + ";" + team.getName() + ";" + player.getDakName() + ";" + player.getDiscordName() + ";" + player.getMmr() + "\n"));
		ERPlayer sub = ERPlayer.getERPlayer(team.getSub());
		if (sub != null) {
			builder.append(team.getAverage() + ";" + team.getName() + ";" + sub.getDakName() + ";" + sub.getDiscordName() + ";" + sub.getMmr() + "\n");
		}
	}
}
