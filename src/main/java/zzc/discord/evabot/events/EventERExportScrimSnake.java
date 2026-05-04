package zzc.discord.evabot.events;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.service.ScrimService;
import zzc.discord.evabot.util.ExcelExport;

/**
 * 
 * @author Lesys
 *
 * Class of EventER that exports a Scrim with snake method into CSV format
 */
@Service
public class EventERExportScrimSnake extends EventERGetSelectedTeamsSnake {
	/**
	 * Constructor of EventERExportScrimSnake
	 */
	public EventERExportScrimSnake(ScrimService scrimService) {
		super(scrimService);
		this.commandName = EventER.commandPrefix + "exportScrimSnake";
	}

	@Override
	public void postExecuteCommand(@NotNull MessageReceivedEvent event) {
		this.lobbies.forEach(lobby -> {
			try {
				ExcelExport.exportLobby("lobby_" + (this.lobbies.indexOf(lobby) + 1), lobby, event);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	
	@Override
	public String helpCommand() {
		return "**" + this.commandName + "**"
				+ " [numberOfLobbies] [minimumNumberOfTeamsPerLobby] - Exports all the teams selected via the snake method with their average MMR and players registered in the team with their own MMR. All teams are sorted by team average MMR.\n";
	}
}
