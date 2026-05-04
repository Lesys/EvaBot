package zzc.discord.evabot.events;

import java.io.IOException;
import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.dto.ScrimDTO;
import zzc.discord.evabot.dto.TeamDTO;
import zzc.discord.evabot.service.ScrimService;
import zzc.discord.evabot.util.ExcelExport;

/**
 * 
 * @author Lesys
 *
 * Class of EventER that exports a Scrim into CSV format
 */
@Service
public class EventERExportScrim extends EventER {
	private final transient ScrimService scrimService;
	/**
	 * Constructor of EventERGetSelectedTeams
	 */
	public EventERExportScrim(ScrimService scrimService) {
		this.commandName += "exportScrim";

		this.scrimService = scrimService;
	}

	/**
	 * Gets all the teams from the serialized variable and sort them by MMR if the option was added in the command line, then exports all the teams/players to CSV file.
	 */
	@Override
	@Transactional
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		boolean byMmr = event.getMessage().getContentRaw().trim().replaceAll(" +", " ").split(" ").length > 1
				&& event.getMessage().getContentRaw().trim().replaceAll(" +", " ").split(" ")[1]
						.equalsIgnoreCase("byMmr");
		System.out.println("ByMmr ? " + byMmr);

		ScrimDTO scrim = this.scrimService.getByEvent(event);

		if (scrim != null) {
			if (EventERManager.hasPermission(event)) {
				List<TeamDTO> filtered = null;
				if (!byMmr)
					filtered = scrim.getTeamList().stream().toList();
				else
					filtered = scrim.getTeamList().stream().sorted().toList();

				try {
					ExcelExport.exportLobby(null, filtered, event);

					this.commandIsSuccessful();
				} catch (IOException i) {
					i.printStackTrace();
				}				
			} else {
				event.getChannel().sendMessage("Only an Administrator can use this command.").queue();
			}
		} else {
			event.getChannel().sendMessage("No teams has yet to be registered for the scrim \"" + event.getChannel().getName() + "\".").queue();
		}
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " [byMmr] - Exports all the teams registered for the scrim to a CSV file with the name of the channel. Using the option \"byMmr\" orders teams by MMR average, else returns by registration order..\n";
	}
}
