package zzc.discord.evabot.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.ERPlayer;
import zzc.discord.evabot.Team;

public interface ExcelExport {
	public final static StringBuilder builder = new StringBuilder();
	
	/**
	 * Exports the builder created across all the other functions into a file.
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public static void exportToFile(String fileName) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(fileName);
		OutputStreamWriter out = new OutputStreamWriter(fileOut);
		out.write(builder.toString());
		out.close();
		fileOut.close();
		System.out.println("Serialized data is saved in " + fileName);
	}
	
	/**
	 * Protected method to construct the string to send
	 * 
	 * @param team      The Team for which you want to create the string
	 */
	public static void exportTeamStringBuilder(Team team) {
		team.getPlayerNames().stream().map(p -> ERPlayer.getERPlayer(p)).forEach(
				player -> builder.append(team.getAverage() + ";" + team.getName() + ";" + player.getDakName() + ";" + player.getDiscordName() + ";" + player.getMmr() + "\n"));
		ERPlayer sub = ERPlayer.getERPlayer(team.getSub());
		if (sub != null) {
			builder.append(team.getAverage() + ";" + team.getName() + ";" + sub.getDakName() + ";" + sub.getDiscordName() + ";" + sub.getMmr() + "\n");
		}
	}
	
	/**
	 * Adds the header to the builder (better to use first)
	 */
	public static void exportHeader() {
		builder.append("averageMmr;teamName;playerIGName;playerDiscordName;playerMmr\n");
	}
	
	/**
	 * Calls every method needed to export the lobby to the file
	 * 
	 * @param lobbyName			Name to add at the start of the file name, especially if there are multiple files.
	 * @param teams				The list of Teams to export in the file
	 * @param event
	 * @throws IOException
	 */
	public static void exportLobby(String lobbyName, List<Team> teams, @NotNull MessageReceivedEvent event) throws IOException {
		// Resets the builder for new file to export
		builder.setLength(0);
		exportHeader();
		teams.stream().forEach(t -> exportTeamStringBuilder(t));
		
		exportToFile(exportStandardFileName(lobbyName, event));	
	}
	
	/**
	 * The default name of the file
	 * 
	 * @param event
	 * @return
	 */
	public static String exportStandardFileName(@NotNull MessageReceivedEvent event) {
		return event.getChannel().getName() + OffsetDateTime.now().format(DateTimeFormatter.ofPattern("_yyyy-MM-dd_HH-mm-ss")).toString() + ".csv";
	}
	
	/**
	 * Adds the lobby name to the default name
	 * 
	 * @param lobbyName
	 * @param event
	 * @return
	 */
	public static String exportStandardFileName(String lobbyName, @NotNull MessageReceivedEvent event) {
		return (!UtilEmpty.isEmptyOrNull(lobbyName) ? lobbyName + "_" : "") + exportStandardFileName(event);
	}
}
