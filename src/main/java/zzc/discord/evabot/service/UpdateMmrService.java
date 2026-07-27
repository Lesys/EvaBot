package zzc.discord.evabot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import zzc.discord.evabot.dto.ScrimDTO;
import zzc.discord.evabot.dto.TeamDTO;

/**
 * Service to update the MMR of all the players in a team or a scrim
 * @author Lesys
 *
 */
@Service
public class UpdateMmrService {
	private final ERPlayerService erPlayerService;
	
	@Autowired
	public UpdateMmrService(ERPlayerService erPlayerService) {
		this.erPlayerService = erPlayerService;
	}
	
	@Transactional
	public void updateTeamsMmr(ScrimDTO scrim, boolean forceUpdate) {
		if (scrim != null) {
			System.out.println("[UpdateMmrService] updateTeamsMmr updating MMR for all players in the " + scrim.getChannelName() + " scrim");
			scrim.getTeamList().forEach(team -> this.updateTeamMmr(team, forceUpdate));
		}
	}
	
	@Transactional
	public void updateTeamMmr(TeamDTO team, boolean forceUpdate) {
		if (team != null) {
			team.getFullPlayerList().forEach(player -> {				
				System.out.println("[UpdateMmrService] updateTeamMmr updating MMR for all players in the " + team.getName() + " team");
				if (forceUpdate == true) {
					this.erPlayerService.updateForceMmr(player);				
				} else {
					this.erPlayerService.updateMmr(player);
				}
			});
		}
	}
}
