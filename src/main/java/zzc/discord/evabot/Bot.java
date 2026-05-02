package zzc.discord.evabot;


import java.net.URISyntaxException;
import java.util.*;

import javax.security.auth.login.LoginException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
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
@SpringBootApplication
public class Bot {
	public static GatewayIntent[] INTENTS = {GatewayIntent.DIRECT_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES};

	public static ConfigurableApplicationContext app;
	
	public static void main(String[] args) throws LoginException {
		// Registers the spring app so that static methods can use "app.getBean" to get the spring initialized beans
		Bot.app = SpringApplication.run(Bot.class, args);

		if (args.length == 2) {
			Token.jdaToken = args[0];
			Token.erApiKey = args[1];
	
			String jdaToken = Token.jdaToken;
			@SuppressWarnings("unused")
			JDA jda = JDABuilder.create(jdaToken, Arrays.asList(INTENTS))
					.enableCache(CacheFlag.VOICE_STATE)
					.setStatus(OnlineStatus.ONLINE)
					.setActivity(Activity.customStatus("Use \"" + EventER.commandPrefix + "help\" to receive all usable commands."))
					.addEventListeners(Bot.app.getBean(EventERManager.class))
					.build();
			
			try {
				jda.awaitReady();
				
				// Removes all scrims that got their text channel deleted TODO
//				Bot.app.getBean(ScrimService.class).removeAllDeletedScrims(jda);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
}
