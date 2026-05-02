package zzc.discord.evabot.formatter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import zzc.discord.evabot.core.entity.ERPlayer;
import zzc.discord.evabot.core.entity.HistoryName;
import zzc.discord.evabot.core.repository.HistoryNameRepository;
import zzc.discord.evabot.dto.HistoryNameDTO;
import zzc.discord.evabot.util.UtilEmpty;

@Service
public class HistoryNameFormatter implements AbstractFormatter<HistoryName, HistoryNameDTO> {
	
	private HistoryNameRepository historyNameRepository;
	
	@Autowired
	public HistoryNameFormatter(HistoryNameRepository historyNameRepository) {
		this.historyNameRepository = historyNameRepository;
	}

	@Override
	public List<HistoryName> dtoToEntity(List<HistoryNameDTO> dtoList) {
		List<HistoryName> entityList = new ArrayList<>();
		
		if (!UtilEmpty.isEmptyOrNull(dtoList)) {
			for (HistoryNameDTO dto : dtoList) {
				entityList.add(this.dtoToEntity(dto));
			}
		}
		
		return entityList;
	}

	@Override
	public List<HistoryNameDTO> entityToDto(List<HistoryName> entityList) {
		List<HistoryNameDTO> dtoList = new ArrayList<>();
		
		if (!UtilEmpty.isEmptyOrNull(entityList)) {
			for (HistoryName entity : entityList) {
				dtoList.add(this.entityToDto(entity));
			}
		}
		
		return dtoList;
	}

	@Override
	public HistoryName dtoToEntity(HistoryNameDTO dto) {
		HistoryName entity = null;
		
		if (dto == null) return entity;
		
		if (dto.getId() != null) {
			entity = this.historyNameRepository.getById(dto.getId());
		} else {
			entity = new HistoryName();
		}
		
		this.hydrateEntityFromDto(entity, dto);
		
		return entity;
	}

	@Override
	public HistoryNameDTO entityToDto(HistoryName entity) {
		HistoryNameDTO dto = null;
		
		if (entity == null) return dto;
		
		dto = new HistoryNameDTO();

		dto.setId(entity.getId());
		dto.setCreationTime(entity.getCreationTime());
		dto.setModificationTime(entity.getModificationTime());

		this.hydrateDtoFromEntity(entity, dto);
		
		return dto;
	}

	@Override
	public void hydrateEntityFromDto(HistoryName entity, HistoryNameDTO dto) {
		if (dto != null) {
			entity.setNickname(dto.getNickname());
		}
	}

	@Override
	public void hydrateDtoFromEntity(HistoryName entity, HistoryNameDTO dto) {
		if (entity != null) {
			dto.setNickname(entity.getNickname());
			
			if (entity.getPlayer() != null) {
				ERPlayer player = entity.getPlayer();
				dto.setPlayer_id(player.getId());
			}
		}
	}
	
}
