package zzc.discord.evabot;


import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

import javax.security.auth.login.LoginException;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.*;
import net.dv8tion.jda.api.utils.cache.*;
import zzc.discord.evabot.events.EventER;
import zzc.discord.evabot.events.EventERManager;

/**
 * 
 * @author Lesys
 *
 * Main class of the project. Initializes the Discord Bot and saves the scrims in files.
 */
public class Bot {
	public static GatewayIntent[] INTENTS = {GatewayIntent.DIRECT_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES};

	public static Map<String, List<Team>> scrims = new HashMap<String, List<Team>>();
	public static List<ERPlayer> allPlayers = new ArrayList<ERPlayer>();

	public static void main(String[] args) throws LoginException {
		if (args.length == 2) {
			Token.jdaToken = args[0];
			Token.erApiKey = args[1];
	
			String jdaToken = Token.jdaToken;
			@SuppressWarnings("unused")
			JDA jda = JDABuilder.create(jdaToken, Arrays.asList(INTENTS))
					.enableCache(CacheFlag.VOICE_STATE)
					.setStatus(OnlineStatus.ONLINE)
					.setActivity(Activity.customStatus("Use \"" + EventER.commandPrefix + "help\" to receive all usable commands."))
					.addEventListeners(new EventERManager())
					.build();
		} else {
			try {
				String path = Bot.class.getProtectionDomain()
						.getCodeSource()
						.getLocation()
						.toURI()
						.getPath();
				System.out.println("Please put 2 arguments on the command line as displayed:\n" + path.substring(path.lastIndexOf("/") + 1) + " \"discordBotToken\" \"eternalReturnApiKey\"");
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Serializes scrims to a local file so we keep them even when the bot stops
	 */
	public static void serializeScrims() {
		try {
			Bot.serializePlayers();
			FileOutputStream fileOut = new FileOutputStream("ERCS_EU_teams.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
//			Map<String, Integer> test = new HashMap<String, Integer>();
//			Bot.scrims.forEach((k, v) -> test.put(k, v.size()));
			out.writeObject(Bot.scrims);
//			out.writeObject(Bot.teams);
			out.close();
			fileOut.close();
			System.out.println("Serialized data is saved in ERCS_EU_teams.ser");
		} catch (NotSerializableException nse) {
			nse.printStackTrace();
			//Bot.scrims = new HashMap<String, List<Team>>();
			return;
		} catch (IOException i) {
			i.printStackTrace();
		}
	}
	
	/**
	 * Retrieves the saved scrims from the file
	 */
	@SuppressWarnings("unchecked")
	public static void deserializeScrims() {
		try {
			FileInputStream fileIn = new FileInputStream("ERCS_EU_teams.ser"); //TODO Faire en sorte que les ERPlayer soient pris depuis allPlayer
			ObjectInputStream in = new ObjectInputStream(fileIn);
			Bot.scrims = (HashMap<String, List<Team>>) in.readObject();
//			Bot.teams = (List<Team>) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException i) {
			//i.printStackTrace();
			return;
		} catch (ClassNotFoundException c) {
			System.out.println("Map string list-teams class not found");
			Bot.scrims = new HashMap<String, List<Team>>();
//			Bot.teams = new ArrayList<Team>();
			c.printStackTrace();
			return;
		}
	}

	/**
	 * Serializes players to a local file so we keep them even when the bot stops
	 */
	public static void serializePlayers() {
		try {
			FileOutputStream fileOut = new FileOutputStream("ERCS_EU_players.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
//			Map<String, Integer> test = new HashMap<String, Integer>();
//			Bot.scrims.forEach((k, v) -> test.put(k, v.size()));
			out.writeObject(Bot.allPlayers);
//			out.writeObject(Bot.teams);
			out.close();
			fileOut.close();
			System.out.println("Serialized data is saved in ERCS_EU_players.ser");
		} catch (NotSerializableException nse) {
			nse.printStackTrace();
			//Bot.scrims = new HashMap<String, List<Team>>();
			return;
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	/**
	 * Retrieves the saved players from the file
	 */
	@SuppressWarnings("unchecked")
	public static void deserializePlayers() {
		try {
			FileInputStream fileIn = new FileInputStream("ERCS_EU_players.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			Bot.allPlayers = (List<ERPlayer>) in.readObject();
//			Bot.teams = (List<Team>) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException i) {
			//i.printStackTrace();
			return;
		} catch (ClassNotFoundException c) {
			System.out.println("List ERPlayers class not found");
			Bot.allPlayers = new ArrayList<ERPlayer>();
//			Bot.teams = new ArrayList<Team>();
			c.printStackTrace();
			return;
		}
	}
	
	/**
	 * Gets the team from a scrim
	 * @param event		The event retrieved from Discord (to get the channel name)
	 * @param teamName	The team name
	 * @return			The team registered in the scrim (null if no team found)
	 */
	public static Team getTeam(@NotNull MessageReceivedEvent event, String teamName) {
		List<Team> teams = Bot.getScrim(event);
		return teams != null ? teams.stream().filter(t -> t.getName().equalsIgnoreCase(teamName)).findFirst().orElse(null) : null;
	}
	
	/**
	 * Gets the scrim related to the event
	 * @param event		The event retrieved from Discord (to get the channel name)
	 * @return			The list of teams registered for the scrim (can be null)
	 */
	public static List<Team> getScrim(@NotNull MessageReceivedEvent event) {
		return Bot.scrims.get(event.getChannel().getName());
	}
}
