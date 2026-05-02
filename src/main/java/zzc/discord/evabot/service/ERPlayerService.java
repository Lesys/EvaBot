package zzc.discord.evabot.service;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import zzc.discord.evabot.core.entity.ERPlayer;
import zzc.discord.evabot.core.repository.ERPlayerRepository;
import zzc.discord.evabot.dto.ERPlayerDTO;
import zzc.discord.evabot.formatter.ERPlayerFormatter;
import zzc.discord.evabot.util.UtilConvert;
import zzc.discord.evabot.util.UtilEmpty;

@Service
public class ERPlayerService extends AbstractService<ERPlayer, ERPlayerDTO> {
	
	private transient ERPlayerRepository erPlayerRepository;
	
	private transient ERPlayerFormatter erPlayerFormatter;
	
	private transient TeamService teamService;
	
	@Autowired
	public ERPlayerService(ERPlayerRepository erPlayerRepository, ERPlayerFormatter erPlayerFormatter, TeamService teamService) {
		this.erPlayerRepository = erPlayerRepository;
		this.erPlayerFormatter = erPlayerFormatter;
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


	public static String getNameWithoutSpecialChar(Supplier<String> getString) {
		if (getString == null || getString.get() == null) return null;
		return getString.get().replaceAll("[*_]", "");
	}	
}
