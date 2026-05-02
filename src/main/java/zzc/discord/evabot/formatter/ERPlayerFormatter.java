package zzc.discord.evabot.formatter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import zzc.discord.evabot.core.entity.ERPlayer;
import zzc.discord.evabot.core.repository.ERPlayerRepository;
import zzc.discord.evabot.core.repository.TeamRepository;
import zzc.discord.evabot.dto.ERPlayerDTO;
import zzc.discord.evabot.util.UtilEmpty;

@Service
public class ERPlayerFormatter implements AbstractFormatter<ERPlayer, ERPlayerDTO> {
	
	private transient ERPlayerRepository erPlayerRepository;
	
	private transient TeamRepository teamRepository;
	
	private transient HistoryNameFormatter historyNameFormatter;
	
	@Autowired
	public ERPlayerFormatter(ERPlayerRepository erPlayerRepository, TeamRepository teamRepository, HistoryNameFormatter historyNameFormatter) {
		this.erPlayerRepository = erPlayerRepository;
		this.teamRepository = teamRepository;
		this.historyNameFormatter = historyNameFormatter;
	}

	@Override
	public List<ERPlayer> dtoToEntity(List<ERPlayerDTO> dtoList) {
		List<ERPlayer> entityList = new ArrayList<>();
		
		if (!UtilEmpty.isEmptyOrNull(dtoList)) {
			for (ERPlayerDTO dto : dtoList) {
				entityList.add(this.dtoToEntity(dto));
			}
		}
		
		return entityList;
	}

	@Override
	public List<ERPlayerDTO> entityToDto(List<ERPlayer> entityList) {
		List<ERPlayerDTO> dtoList = new ArrayList<>();
		
		if (!UtilEmpty.isEmptyOrNull(entityList)) {
			for (ERPlayer entity : entityList) {
				dtoList.add(this.entityToDto(entity));
			}
		}
		
		return dtoList;
	}

	@Override
	public ERPlayer dtoToEntity(ERPlayerDTO dto) {
		ERPlayer entity = null;
		
		if (dto == null) return entity;
		
		if (dto.getId() != null) {
			entity = this.erPlayerRepository.getById(dto.getId());
		} else {
			entity = new ERPlayer();
		}
		
		this.hydrateEntityFromDto(entity, dto);
		
		return entity;
	}

	@Override
	public ERPlayerDTO entityToDto(ERPlayer entity) {
		ERPlayerDTO dto = null;
		
		if (entity == null) return dto;
		
		dto = new ERPlayerDTO();
		
		dto.setId(entity.getId());
		dto.setCreationTime(entity.getCreationTime());
		dto.setModificationTime(entity.getModificationTime());

		this.hydrateDtoFromEntity(entity, dto);
		
		return dto;
	}

	@Override
	public void hydrateEntityFromDto(ERPlayer entity, ERPlayerDTO dto) {
		if (dto != null) {
			entity.setDiscordName(dto.getDiscordName());
			entity.setDisplayName(dto.getDisplayName());
			entity.setMmr(dto.getMmr());
			entity.setGlobalRank(dto.getGlobalRank());
			entity.setDak(dto.getDak());
			entity.setLastUpdateTime(dto.getLastUpdateTime());
			
			entity.setHistoryPlayerNameList(new ArrayList<>());		
			
			if (!UtilEmpty.isEmptyOrNull(dto.getHistoryPlayerNameList())) {				
				entity.setHistoryPlayerNameList(this.historyNameFormatter.dtoToEntity(dto.getHistoryPlayerNameList()));
				entity.getHistoryPlayerNameList().forEach(hpn -> hpn.setPlayer(entity));
			} else {
				entity.setHistoryPlayerNameList(new ArrayList<>());				
			}
			
//			if (!UtilEmpty.isEmptyOrNull(dto.getCaptainTeams())) {
//				entity.setCaptainTeams(new ArrayList<>());		
//				dto.getCaptainTeams().forEach(teamId -> entity.getCaptainTeams().add(this.teamRepository.getById(teamId)));
//			} else {
//				entity.setCaptainTeams(new ArrayList<>());				
//			}
//			
//			if (!UtilEmpty.isEmptyOrNull(dto.getSubTeams())) {
//				entity.setSubTeams(new ArrayList<>());	
//				dto.getSubTeams().forEach(teamId -> entity.getSubTeams().add(this.teamRepository.getById(teamId)));
//			} else {
//				entity.setSubTeams(new ArrayList<>());				
//			}
//			
//			if (!UtilEmpty.isEmptyOrNull(dto.getTeamList())) {
//				entity.setTeamList(new ArrayList<>());			
//				dto.getTeamList().forEach(teamId -> entity.getTeamList().add(this.teamRepository.getById(teamId)));
//			} else {
//				entity.setTeamList(new ArrayList<>());				
//			}
			
//			if (entity.getHistoryPlayerNameList().stream()
//					.map(HistoryName::get)
//					.filter(name -> name.equalsIgnoreCase(dto.getDakName())) != null) {
//				entity.getHistoryPlayerNameList().add(entity.getDakName());
//			}
		}
	}

	@Override
	public void hydrateDtoFromEntity(ERPlayer entity, ERPlayerDTO dto) {
		if (entity != null) {
			dto.setDiscordName(entity.getDiscordName());
			dto.setDisplayName(entity.getDisplayName());
			dto.setMmr(entity.getMmr());
			dto.setGlobalRank(entity.getGlobalRank());
			dto.setDak(entity.getDak());
			dto.setLastUpdateTime(entity.getLastUpdateTime());
			
			if (!UtilEmpty.isEmptyOrNull(entity.getHistoryPlayerNameList())) {
				dto.setHistoryPlayerNameList(this.historyNameFormatter.entityToDto(entity.getHistoryPlayerNameList()));
			} else {
				dto.setHistoryPlayerNameList(new ArrayList<>());				
			}
			
//			if (!UtilEmpty.isEmptyOrNull(entity.getCaptainTeams())) {
//				dto.setCaptainTeams(new ArrayList<>());		
//				entity.getCaptainTeams().forEach(team -> dto.getCaptainTeams().add(team.getId()));
//			} else {
//				entity.setCaptainTeams(new ArrayList<>());				
//			}
//			
//			if (!UtilEmpty.isEmptyOrNull(entity.getSubTeams())) {
//				dto.setSubTeams(new ArrayList<>());	
//				entity.getSubTeams().forEach(team -> dto.getSubTeams().add(team.getId()));
//			} else {
//				entity.setSubTeams(new ArrayList<>());				
//			}
//			
//			if (!UtilEmpty.isEmptyOrNull(entity.getTeamList())) {
//				dto.setTeamList(new ArrayList<>());			
//				entity.getTeamList().forEach(team -> dto.getTeamList().add(team.getId()));
//			} else {
//				entity.setTeamList(new ArrayList<>());				
//			}
		}
	}
	
}
