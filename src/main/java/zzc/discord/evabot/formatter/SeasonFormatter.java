package zzc.discord.evabot.formatter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import zzc.discord.evabot.core.entity.Season;
import zzc.discord.evabot.core.repository.SeasonRepository;
import zzc.discord.evabot.dto.SeasonDTO;
import zzc.discord.evabot.util.UtilEmpty;

@Service
public class SeasonFormatter implements AbstractFormatter<Season, SeasonDTO> {
	
	private SeasonRepository seasonRepository;
	
	@Autowired
	public SeasonFormatter(SeasonRepository seasonRepository) {
		this.seasonRepository = seasonRepository;
	}

	@Override
	public List<Season> dtoToEntity(List<SeasonDTO> dtoList) {
		List<Season> entityList = new ArrayList<>();
		
		if (!UtilEmpty.isEmptyOrNull(dtoList)) {
			for (SeasonDTO dto : dtoList) {
				entityList.add(this.dtoToEntity(dto));
			}
		}
		
		return entityList;
	}

	@Override
	public List<SeasonDTO> entityToDto(List<Season> entityList) {
		List<SeasonDTO> dtoList = new ArrayList<>();
		
		if (!UtilEmpty.isEmptyOrNull(entityList)) {
			for (Season entity : entityList) {
				dtoList.add(this.entityToDto(entity));
			}
		}
		
		return dtoList;
	}

	@Override
	public Season dtoToEntity(SeasonDTO dto) {
		Season entity = null;
		
		if (dto == null) return entity;
		
		if (dto.getId() != null) {
			entity = this.seasonRepository.getById(dto.getId());
		} else {
			entity = new Season();
		}
		
		this.hydrateEntityFromDto(entity, dto);
		
		return entity;
	}

	@Override
	public SeasonDTO entityToDto(Season entity) {
		SeasonDTO dto = null;
		
		if (entity == null) return dto;
		
		dto = new SeasonDTO();
		
		dto.setId(entity.getId());
		dto.setCreationTime(entity.getCreationTime());
		dto.setModificationTime(entity.getModificationTime());

		this.hydrateDtoFromEntity(entity, dto);
		
		return dto;
	}

	@Override
	public void hydrateEntityFromDto(Season entity, SeasonDTO dto) {
		if (dto != null) {
			entity.setActiveSeasonId(dto.getActiveSeasonId());
			entity.setRealSeasonId(dto.getRealSeasonId());
			entity.setLastDayUpdated(dto.getLastDayUpdate());
		}
	}

	@Override
	public void hydrateDtoFromEntity(Season entity, SeasonDTO dto) {
		if (entity != null) {
			dto.setActiveSeasonId(entity.getActiveSeasonId());
			dto.setRealSeasonId(entity.getRealSeasonId());
			dto.setLastDayUpdate(entity.getLastDayUpdated());
		}
	}
	
}
