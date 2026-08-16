package zzc.discord.evabot.formatter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import zzc.discord.evabot.core.entity.HelperRole;
import zzc.discord.evabot.core.repository.HelperRoleRepository;
import zzc.discord.evabot.core.repository.ServerRepository;
import zzc.discord.evabot.dto.HelperRoleDTO;
import zzc.discord.evabot.util.UtilEmpty;

@Service
public class HelperRoleFormatter implements AbstractFormatter<HelperRole, HelperRoleDTO> {
	
	private HelperRoleRepository helperRoleRepository;
	
	private ServerRepository serverRepository;
	
	@Autowired
	public HelperRoleFormatter(HelperRoleRepository helperRoleRepository, ServerRepository serverRepository) {
		this.helperRoleRepository = helperRoleRepository;
		this.serverRepository = serverRepository;
	}

	@Override
	public List<HelperRole> dtoToEntity(List<HelperRoleDTO> dtoList) {
		List<HelperRole> entityList = new ArrayList<>();
		
		if (!UtilEmpty.isEmptyOrNull(dtoList)) {
			for (HelperRoleDTO dto : dtoList) {
				entityList.add(this.dtoToEntity(dto));
			}
		}
		
		return entityList;
	}

	@Override
	public List<HelperRoleDTO> entityToDto(List<HelperRole> entityList) {
		List<HelperRoleDTO> dtoList = new ArrayList<>();
		
		if (!UtilEmpty.isEmptyOrNull(entityList)) {
			for (HelperRole entity : entityList) {
				dtoList.add(this.entityToDto(entity));
			}
		}
		
		return dtoList;
	}

	@Override
	public HelperRole dtoToEntity(HelperRoleDTO dto) {
		HelperRole entity = null;
		
		if (dto == null) return entity;
		
		if (dto.getId() != null) {
			entity = this.helperRoleRepository.getById(dto.getId());
		} else {
			entity = new HelperRole();
		}
		
		this.hydrateEntityFromDto(entity, dto);
		
		return entity;
	}

	@Override
	public HelperRoleDTO entityToDto(HelperRole entity) {
		HelperRoleDTO dto = null;
		
		if (entity == null) return dto;
		
		dto = new HelperRoleDTO();

		dto.setId(entity.getId());
		dto.setCreationTime(entity.getCreationTime());
		dto.setModificationTime(entity.getModificationTime());

		this.hydrateDtoFromEntity(entity, dto);
		
		return dto;
	}

	@Override
	public void hydrateEntityFromDto(HelperRole entity, HelperRoleDTO dto) {
		if (dto != null) {
			entity.setEntityId(dto.getEntityId());
			entity.setName(dto.getName());
			
			if (dto.getServer_id() != null) {
				entity.setServer(this.serverRepository.getById(dto.getServer_id()));
			}
		}
	}

	@Override
	public void hydrateDtoFromEntity(HelperRole entity, HelperRoleDTO dto) {
		if (entity != null) {
			dto.setEntityId(entity.getEntityId());
			dto.setName(entity.getName());
			
			if (entity.getServer() != null) {
				dto.setServer_id(entity.getServer().getId());
			}
		}
	}
	
}
