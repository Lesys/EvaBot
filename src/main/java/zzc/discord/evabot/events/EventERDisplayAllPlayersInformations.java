package zzc.discord.evabot.events;

import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.dto.ERPlayerDTO;
import zzc.discord.evabot.dto.HistoryNameDTO;
import zzc.discord.evabot.service.ERPlayerService;
import zzc.discord.evabot.util.UtilERPlayer;
import zzc.discord.evabot.util.UtilEmpty;

/**
 * 
 * @author Lesys
 *
 * Class of EventER to display all the informations of every players registered in database
 */
@Service
public class EventERDisplayAllPlayersInformations extends EventER {
	private final transient ERPlayerService erPlayerService;
	/**
	 * Constructor of EventERDisplayAllPlayersInformations
	 */
	public EventERDisplayAllPlayersInformations(ERPlayerService erPlayerService) {
		this.commandName += "getAllPlayersInfo";
		
		this.erPlayerService = erPlayerService;
	}
	
	/**
	 * Gets all the players in the database and displays their registered information
	 */
	@Override
	@Transactional
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		final List<String> messages = new ArrayList<String>();
		
		final StringBuilder builder = new StringBuilder();
		builder.append("All players currently in database:\n");
		
		List<ERPlayerDTO> allPlayers = this.erPlayerService.getAll();
		
		if (!UtilEmpty.isEmptyOrNull(allPlayers)) {
			allPlayers = allPlayers.stream().filter(player -> player.getDiscordName() != null).sorted(Comparator.comparing(ERPlayerDTO::getDiscordName)).toList();
			allPlayers.forEach(player -> {
				System.out.println("Player discord name: " + player.getDiscordName() + " / DAK name: " + player.getDakName() + "\n");
				if (builder.length() > 0 && builder.length() >= 1800) {
					messages.add(builder.toString());
					builder.delete(0, builder.length());
				}
				builder.append("Player discord name: " + UtilERPlayer.getNameWithoutSpecialChar(player::getDiscordName) + " / DAK name: " + player.getDakName() + "\n");
				if (!UtilEmpty.isEmptyOrNull(player.getHistoryPlayerNameList())) {
					builder.append("\t Nickname historic: ").append(String.join(" / ", player.getHistoryPlayerNameList().stream().map(HistoryNameDTO::getNickname).toList())).append("\n");
				}
			});
			
			messages.add(builder.toString());
	        messages.forEach(m -> sendMessageWait(event, m));
	        
	        this.commandIsSuccessful();
		} else {			
			event.getChannel().sendMessage("There are currently no player in the database.").queue();
		}
	}
	
	@Override
	public String helpCommand() {
		return super.helpCommand()
				+ " - Returns all the players currently registered in the database with the informations about them (Discord name and DAK name)).\n";
	}
}
