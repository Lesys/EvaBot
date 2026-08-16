package zzc.discord.evabot.formatter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import zzc.discord.evabot.core.entity.Server;
import zzc.discord.evabot.core.repository.ServerRepository;
import zzc.discord.evabot.dto.ServerDTO;
import zzc.discord.evabot.util.UtilEmpty;

@Service
public class ServerFormatter implements AbstractFormatter<Server, ServerDTO> {
	
	private transient ServerRepository serverRepository;

	private transient HelperRoleFormatter helperRoleFormatter;
	
	@Autowired
	public ServerFormatter(ServerRepository serverRepository, HelperRoleFormatter helperRoleFormatter) {
		this.serverRepository = serverRepository;
		this.helperRoleFormatter = helperRoleFormatter;
	}

	@Override
	public List<Server> dtoToEntity(List<ServerDTO> dtoList) {
		List<Server> entityList = new ArrayList<>();
		
		if (!UtilEmpty.isEmptyOrNull(dtoList)) {
			for (ServerDTO dto : dtoList) {
				entityList.add(this.dtoToEntity(dto));
			}
		}
		
		return entityList;
	}

	@Override
	public List<ServerDTO> entityToDto(List<Server> entityList) {
		List<ServerDTO> dtoList = new ArrayList<>();
		
		if (!UtilEmpty.isEmptyOrNull(entityList)) {
			for (Server entity : entityList) {
				dtoList.add(this.entityToDto(entity));
			}
		}
		
		return dtoList;
	}

	@Override
	public Server dtoToEntity(ServerDTO dto) {
		Server entity = null;
		
		if (dto == null) return entity;
		
		if (dto.getId() != null) {
			entity = this.serverRepository.getById(dto.getId());
		} else {
			entity = new Server();
		}
		
		this.hydrateEntityFromDto(entity, dto);
		
		return entity;
	}

	@Override
	public ServerDTO entityToDto(Server entity) {
		ServerDTO dto = null;
		
		if (entity == null) return dto;
		
		dto = new ServerDTO();
		
		dto.setId(entity.getId());
		dto.setCreationTime(entity.getCreationTime());
		dto.setModificationTime(entity.getModificationTime());

		this.hydrateDtoFromEntity(entity, dto);
		
		return dto;
	}

	@Override
	public void hydrateEntityFromDto(Server entity, ServerDTO dto) {
		if (dto != null) {
			entity.setName(dto.getName());
			entity.setServerId(dto.getServerId());
			
			if (!UtilEmpty.isEmptyOrNull(dto.getHelperRoleList())) {
				entity.setHelperRole(this.helperRoleFormatter.dtoToEntity(dto.getHelperRoleList()));
				entity.getHelperRole().forEach(helperRole -> helperRole.setServer(entity));
			} else {
				entity.setHelperRole(new ArrayList<>());
			}
		}
	}

	@Override
	public void hydrateDtoFromEntity(Server entity, ServerDTO dto) {
		if (entity != null) {
			dto.setName(entity.getName());
			dto.setServerId(entity.getServerId());
			
			dto.setHelperRoleList(this.helperRoleFormatter.entityToDto(entity.getHelperRole()));
		}
	}
	
}
