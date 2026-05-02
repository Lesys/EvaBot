package zzc.discord.evabot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.core.entity.Team;
import zzc.discord.evabot.core.repository.TeamRepository;
import zzc.discord.evabot.dto.ERPlayerDTO;
import zzc.discord.evabot.dto.ScrimDTO;
import zzc.discord.evabot.dto.TeamDTO;
import zzc.discord.evabot.formatter.TeamFormatter;

@Service
public class TeamService extends AbstractService<Team, TeamDTO> {
	
	private final TeamRepository teamRepository;
	
	private final TeamFormatter teamFormatter;
	
	private final ScrimService scrimService;
	
	@Autowired
	public TeamService(TeamRepository teamRepository, TeamFormatter teamFormatter, ScrimService scrimService) {
		this.teamRepository = teamRepository;
		this.teamFormatter = teamFormatter;
		this.scrimService = scrimService;
	}

	@Override
	public TeamRepository getRepository() {
		return this.teamRepository;
	}

	@Override
	public TeamFormatter getFormatter() {
		return this.teamFormatter;
	}
	
	@Transactional
	public TeamDTO getByPlayerAndScrim(ERPlayerDTO player, ScrimDTO scrim) {
		if (player == null || scrim == null) {
			return null;
		}
		
		Integer playerId = player.getId();
		Integer scrimId = scrim.getId();

		System.out.println("[TeamService] getByPlayerIdAndScrimId playerId " + playerId + "; scrimId " + scrimId);
		
		return this.getFormatter().entityToDto(this.getRepository().getByPlayerIdAndScrimId(playerId, scrimId));
	}

	@Transactional
	public TeamDTO getByEventAndTeamName(MessageReceivedEvent event, String teamName) {
		ScrimDTO scrim = this.scrimService.getByEvent(event);
		
		if (scrim == null) {
			return null;
		}
		
		return scrim.getTeam(teamName);
	}
	
	@Transactional
	public void removeCaptain(List<Team> list) {
		list.forEach(team -> {
			team.setCaptain(null);
			this.getRepository().save(team);
		});
	}
	
	@Transactional
	public void removeSub(List<Team> list) {
		list.forEach(team -> {
			team.setSub(null);
			this.getRepository().save(team);
		});
	}
	
	@Transactional
	public void removePlayer(List<Team> list, Integer playerId) {
		list.forEach(team -> {
			team.getPlayerList().removeIf(player -> player.getId().equals(playerId));
			this.getRepository().save(team);
		});
	}
	
	@Override
	@Transactional
	public Team onDelete(Team team) {
		this.scrimService.removeTeam(team.getScrim(), team.getId());
		
		// Removing manually the players in the team so that it doesn't remove the player itself
		team.getPlayerList().clear();
		
		return this.getRepository().save(team);
	}
}
