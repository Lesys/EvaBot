package zzc.discord.evabot.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import zzc.discord.evabot.core.entity.ERPlayer;
import zzc.discord.evabot.core.repository.ERPlayerRepository;
import zzc.discord.evabot.dto.ERPlayerDTO;
import zzc.discord.evabot.exception.UserIdNotLinkedException;
import zzc.discord.evabot.formatter.ERPlayerFormatter;
import zzc.discord.evabot.util.UtilConvert;
import zzc.discord.evabot.util.UtilDate;
import zzc.discord.evabot.util.UtilEmpty;

@Service
public class ERPlayerService extends AbstractService<ERPlayer, ERPlayerDTO> {
	
	private final transient ERPlayerRepository erPlayerRepository;
	
	private final transient ERPlayerFormatter erPlayerFormatter;
	
	private final transient ERApiService erApiService;
	
	private final transient TeamService teamService;
	
	@Autowired
	public ERPlayerService(ERPlayerRepository erPlayerRepository, ERPlayerFormatter erPlayerFormatter, ERApiService erApiService, TeamService teamService) {
		this.erPlayerRepository = erPlayerRepository;
		this.erPlayerFormatter = erPlayerFormatter;
		this.erApiService = erApiService;
		this.teamService = teamService;
	}

	@Override
	public ERPlayerRepository getRepository() {
		return this.erPlayerRepository;
	}
	
	@Override
	public ERPlayerFormatter getFormatter() {
		return this.erPlayerFormatter;
	}
	
	@Override
	@Transactional
	public ERPlayer onDelete(ERPlayer player) {
		this.teamService.removeCaptain(player.getCaptainTeams());
		this.teamService.removeSub(player.getSubTeams());
		this.teamService.removePlayer(player.getTeamList(), player.getId());
		
		return this.getRepository().save(player);
	}
	
	@Transactional
	public ERPlayerDTO getDtoByDak(String dak) {
		String dakName = dak != null ? dak.split("/")[dak.split("/").length - 1] : null;
		
		if (UtilEmpty.isEmptyOrNull(dakName)) {
			return null;
		}

		System.out.println("[ERPlayerService] getDtoByDak " + dakName);
		
		ERPlayer player = this.erPlayerRepository.getByDakName(dakName);
		
		return player == null ? new ERPlayerDTO() : this.erPlayerFormatter.entityToDto(player);
	}
	
	@Transactional
	public ERPlayerDTO getDtoByDiscordName(String discordName) {
		if (UtilEmpty.isEmptyOrNull(discordName)) {
			return null;
		}

		System.out.println("[ERPlayerService] getDtoByDiscordName " + discordName);
		
		return this.erPlayerFormatter.entityToDto(this.erPlayerRepository.getByDiscordName(discordName));
	}
	
	@Transactional
	public List<ERPlayerDTO> getAll() {
		return this.getFormatter().entityToDto(UtilConvert.toList(this.getRepository().findAll()));
	}

	/**
	 * Updates the userId of the player in parameter if it's null, else does nothing
	 * @param player	The player whom needs to get its userId
	 */
	@Transactional
	public ERPlayerDTO updateUserId(ERPlayerDTO player) {
		if (player == null) return player;
		
		System.out.println("[ERPlayerService] updateUserId " + player.getDakName());

		if (player.getUserId() == null) {		
			String userId = this.erApiService.getUserId(player.getDakName());
			
			if (userId != null) {
				player.setUserId(userId);
				this.save(player);
			}
		}
		
		return player;
	}

	/**
	 * Forces the update of the userId of the player in parameter by putting it to null
	 * @param player	The player whom needs to get its userId reinitialized
	 */
	@Transactional
	public ERPlayerDTO updateForceUserId(ERPlayerDTO player) {
		if (player == null) return player;
		
		System.out.println("[ERPlayerService] updateForceUserId " + player.getDakName());

		player.setUserId(null);
		return this.updateUserId(player);
	}

	/**
	 * Updates the MMR of the player in parameter if it has been more than
	 * @param player
	 */
	@Transactional
	public void updateMmr(ERPlayerDTO player) {
		if (player == null) return;

		Date oneHourEarlier = UtilDate.addHours(new Date(), -1);
		
		if (player.getLastUpdateTime() == null || UtilDate.isAfter(oneHourEarlier, player.getLastUpdateTime())) { 
			System.out.println("[ERPlayerService] updateMmr " + player.getDakName());
			
			this.updateForceMmr(player);
		} else {
			System.out.println("[ERPlayerService] updateMmr - MMR has been updated in less than an hours (" + player.getLastUpdateTime() + "), nothing to do for " + player.getDakName());
		}
	}

	@Transactional
	public void updateForceMmr(ERPlayerDTO player) {
		if (player == null) return;
		
		boolean canContinue = true;
		
		System.out.println("[ERPlayerService] updateForceMmr " + player.getDakName());

		// Checks if the userId is not null, else fills it
		this.updateUserId(player);
		
		if (player.getUserId() != null) {
			JSONObject rankInfo = null;
			try {
				rankInfo = this.erApiService.getPlayerRankInfoByUserId(player.getUserId());
			} catch (UserIdNotLinkedException e) {
				System.err.println("[ERPlayerService] updateForceMmr - error with userId while trying to retrieve the rank info, now trying to get fresh userId...");
				
				// Retry with new userId if possible ...
				if (e.getStatusCode() == 404) {
					player = this.updateForceUserId(player);

					try {
						rankInfo = this.erApiService.getPlayerRankInfoByUserId(player.getUserId());
					} catch (UserIdNotLinkedException e2) {
						canContinue = false;
						System.err.println("[ERPlayerService] updateForceMmr - error while retrying the rank info, here is the stack trace.");
						e.printStackTrace();
					}
				}
			}
			
			if (canContinue) {
				if (rankInfo != null) {
					player.setMmr(rankInfo.getInt("mmr"));
					player.setGlobalRank(rankInfo.getInt("rank"));
					player.setLastUpdateTime(new Date());
				} else {
					System.err.println("The JSONResponse is null, setting MMR to 0 for " + player.getDakName());
					player.setMmr(0);
					player.setGlobalRank(0);
					player.setLastUpdateTime(null);
				}
	
				this.save(player);
			}
		} else {
			canContinue = false;			
		}
		
		if (!canContinue) {
			System.err.println("[ERPlayerService] updateForceMmr - The userId cannot be retrieved, setting MMR to 0 for " + player.getDakName());
			player.setMmr(0);
			player.setGlobalRank(0);
			player.setLastUpdateTime(null);
			
			this.save(player);
		}
	}
	

//	/**
//	 * Function allowing this player to update their MMR if the last update was more than an hour ago
//	 */
//	public void updateMmr() {
//		LocalDateTime date = LocalDateTime.now();
//		//System.out.println("Date: " + date + "Last: " + this.lastUpdateTime + " -1: " + date.minusHours(1) + "; Comparaison: " + date.minusHours(1).isAfter(this.lastUpdateTime));
//		if (this.lastUpdateTime == null || date.minusSeconds(1).isAfter(this.lastUpdateTime)) {
//			this.updateMmrForce();
//		}
//	}
//
//	/**
//	 * Function forcing this player to update their MMR
//	 */
//	public void updateMmrForce() {
//		this.lastUpdateTime = LocalDateTime.now();
//		JSONObject userRank = null;
//		
//		if (this.getUserId() != null) {
//			try {
//				userRank = GetPlayerStats.getPlayerStatsByUserId(this.getUserId());
//			} catch (UserIdNotLinkedException e) {
//				e.printStackTrace();
//			}
//		}
//		
//		// If the userRank couldn't be retrieved with the userId
//		if (userRank == null) {
//			userRank = GetPlayerStats.getPlayerStats(this);
//		}
//		
//		if (userRank != null) {
//			this.setMmr(userRank.getInt("mmr"));
//			this.rank = userRank.getInt("rank");
//		} else {
//			System.err.println("The JSONResponse is null, setting MMR to 0 for " + this.getDakName());
//			this.setMmr(0);
//			this.rank = 0;
//			this.lastUpdateTime = null;
//		}
//		
//		System.err.println("Serialization players...");
//		Bot.serializePlayers();
//	}
}
