package zzc.discord.evabot.formatter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import zzc.discord.evabot.core.entity.Team;
import zzc.discord.evabot.core.repository.ScrimRepository;
import zzc.discord.evabot.core.repository.TeamRepository;
import zzc.discord.evabot.dto.TeamDTO;
import zzc.discord.evabot.util.UtilEmpty;

@Service
public class TeamFormatter implements AbstractFormatter<Team, TeamDTO> {
	
	private TeamRepository teamRepository;
	
	private ERPlayerFormatter erPlayerFormatter;
	
	private ScrimRepository scrimRepository;
	
	@Autowired
	public TeamFormatter(TeamRepository teamRepository, ERPlayerFormatter erPlayerFormatter, ScrimRepository scrimRepository) {
		this.teamRepository = teamRepository;
		this.erPlayerFormatter = erPlayerFormatter;
		this.scrimRepository = scrimRepository;
	}

	@Override
	public List<Team> dtoToEntity(List<TeamDTO> dtoList) {
		List<Team> entityList = new ArrayList<>();
		
		if (!UtilEmpty.isEmptyOrNull(dtoList)) {
			for (TeamDTO dto : dtoList) {
				entityList.add(this.dtoToEntity(dto));
			}
		}
		
		return entityList;
	}

	@Override
	public List<TeamDTO> entityToDto(List<Team> entityList) {
		List<TeamDTO> dtoList = new ArrayList<>();
		
		if (!UtilEmpty.isEmptyOrNull(entityList)) {
			for (Team entity : entityList) {
				dtoList.add(this.entityToDto(entity));
			}
		}
		
		return dtoList;
	}

	@Override
	public Team dtoToEntity(TeamDTO dto) {
		Team entity = null;
		
		if (dto == null) return entity;
		
		if (dto.getId() != null) {
			entity = this.teamRepository.getById(dto.getId());
		} else {
			entity = new Team();
		}
		
		this.hydrateEntityFromDto(entity, dto);
		
		return entity;
	}

	@Override
	public TeamDTO entityToDto(Team entity) {
		TeamDTO dto = null;
		
		if (entity == null) return dto;
		
		dto = new TeamDTO();
		
		dto.setId(entity.getId());
		dto.setCreationTime(entity.getCreationTime());
		dto.setModificationTime(entity.getModificationTime());

		this.hydrateDtoFromEntity(entity, dto);
		
		return dto;
	}

	@Override
	public void hydrateEntityFromDto(Team entity, TeamDTO dto) {
		if (dto != null) {
			entity.setName(dto.getName());			
			entity.setCaptain(this.erPlayerFormatter.dtoToEntity(dto.getCaptain()));
			entity.setSub(this.erPlayerFormatter.dtoToEntity(dto.getSub()));
			entity.setPriority(dto.getPriority());
			
			if (dto.getScrim_id() != null) {
				entity.setScrim(this.scrimRepository.getById(dto.getScrim_id()));
			}

			if (!UtilEmpty.isEmptyOrNull(dto.getPlayerList())) {
				entity.setPlayerList(this.erPlayerFormatter.dtoToEntity(dto.getPlayerList()));
//				entity.getPlayerList().forEach(player -> {
//					if (!player.getTeamList().contains(entity)) {
//						player.getTeamList().add(entity);
//					}
//				});
//				entity.setPlayerList(new ArrayList<>());
//				dto.getPlayerList().forEach(player -> entity.getPlayerList().add(this.erPlayerFormatter.dtoToEntity(player)));
			} else {
				entity.setPlayerList(new ArrayList<>());
			}
		}
	}

	@Override
	public void hydrateDtoFromEntity(Team entity, TeamDTO dto) {
		if (entity != null) {
			dto.setName(entity.getName());		
			dto.setCaptain(this.erPlayerFormatter.entityToDto(entity.getCaptain()));
			dto.setSub(this.erPlayerFormatter.entityToDto(entity.getSub()));
			dto.setPriority(entity.getPriority());
			
			if (entity.getScrim() != null) {
				dto.setScrim_id(entity.getScrim().getId());
			}
			
			if (!UtilEmpty.isEmptyOrNull(entity.getPlayerList())) {
				dto.setPlayerList(this.erPlayerFormatter.entityToDto(entity.getPlayerList()));
//				dto.setPlayerList(new ArrayList<>());
//				entity.getPlayerList().forEach(player -> dto.getPlayerList().add(this.erPlayerFormatter.entityToDto(player)));
			} else {
				dto.setPlayerList(new ArrayList<>());
			}
		}
	}
	
}
