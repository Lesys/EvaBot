package zzc.discord.evabot.formatter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import zzc.discord.evabot.core.entity.Spectator;
import zzc.discord.evabot.core.repository.SpectatorRepository;
import zzc.discord.evabot.dto.SpectatorDTO;
import zzc.discord.evabot.util.UtilEmpty;

@Service
public class SpectatorFormatter implements AbstractFormatter<Spectator, SpectatorDTO> {
	
	private SpectatorRepository spectatorRepository;
	
	@Autowired
	public SpectatorFormatter(SpectatorRepository spectatorRepository) {
		this.spectatorRepository = spectatorRepository;
	}

	@Override
	public List<Spectator> dtoToEntity(List<SpectatorDTO> dtoList) {
		List<Spectator> entityList = new ArrayList<>();
		
		if (!UtilEmpty.isEmptyOrNull(dtoList)) {
			for (SpectatorDTO dto : dtoList) {
				entityList.add(this.dtoToEntity(dto));
			}
		}
		
		return entityList;
	}

	@Override
	public List<SpectatorDTO> entityToDto(List<Spectator> entityList) {
		List<SpectatorDTO> dtoList = new ArrayList<>();
		
		if (!UtilEmpty.isEmptyOrNull(entityList)) {
			for (Spectator entity : entityList) {
				dtoList.add(this.entityToDto(entity));
			}
		}
		
		return dtoList;
	}

	@Override
	public Spectator dtoToEntity(SpectatorDTO dto) {
		Spectator entity = null;
		
		if (dto == null) return entity;
		
		if (dto.getId() != null) {
			entity = this.spectatorRepository.getById(dto.getId());
		} else {
			entity = new Spectator();
		}
		
		this.hydrateEntityFromDto(entity, dto);
		
		return entity;
	}

	@Override
	public SpectatorDTO entityToDto(Spectator entity) {
		SpectatorDTO dto = null;
		
		if (entity == null) return dto;
		
		dto = new SpectatorDTO();

		dto.setId(entity.getId());
		dto.setCreationTime(entity.getCreationTime());
		dto.setModificationTime(entity.getModificationTime());

		this.hydrateDtoFromEntity(entity, dto);
		
		return dto;
	}

	@Override
	public void hydrateEntityFromDto(Spectator entity, SpectatorDTO dto) {
		if (dto != null) {
			entity.setName(dto.getName());
		}
	}

	@Override
	public void hydrateDtoFromEntity(Spectator entity, SpectatorDTO dto) {
		if (entity != null) {
			dto.setName(entity.getName());
			
			if (entity.getScrim() != null) {
				dto.setScrim_id(entity.getScrim().getId());
			}
		}
	}
	
}
