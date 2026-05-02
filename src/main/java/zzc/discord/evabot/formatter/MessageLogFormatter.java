package zzc.discord.evabot.formatter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import zzc.discord.evabot.core.entity.MessageLog;
import zzc.discord.evabot.core.repository.MessageLogRepository;
import zzc.discord.evabot.dto.MessageLogDTO;
import zzc.discord.evabot.util.UtilEmpty;

@Service
public class MessageLogFormatter implements AbstractFormatter<MessageLog, MessageLogDTO> {
	
	private MessageLogRepository messageLogRepository;
	
	@Autowired
	public MessageLogFormatter(MessageLogRepository messageLogRepository) {
		this.messageLogRepository = messageLogRepository;
	}

	@Override
	public List<MessageLog> dtoToEntity(List<MessageLogDTO> dtoList) {
		List<MessageLog> entityList = new ArrayList<>();
		
		if (!UtilEmpty.isEmptyOrNull(dtoList)) {
			for (MessageLogDTO dto : dtoList) {
				entityList.add(this.dtoToEntity(dto));
			}
		}
		
		return entityList;
	}

	@Override
	public List<MessageLogDTO> entityToDto(List<MessageLog> entityList) {
		List<MessageLogDTO> dtoList = new ArrayList<>();
		
		if (!UtilEmpty.isEmptyOrNull(entityList)) {
			for (MessageLog entity : entityList) {
				dtoList.add(this.entityToDto(entity));
			}
		}
		
		return dtoList;
	}

	@Override
	public MessageLog dtoToEntity(MessageLogDTO dto) {
		MessageLog entity = null;
		
		if (dto == null) return entity;
		
		if (dto.getId() != null) {
			entity = this.messageLogRepository.getById(dto.getId());
		} else {
			entity = new MessageLog();
		}
		
		this.hydrateEntityFromDto(entity, dto);
		
		return entity;
	}

	@Override
	public MessageLogDTO entityToDto(MessageLog entity) {
		MessageLogDTO dto = null;
		
		if (entity == null) return dto;
		
		dto = new MessageLogDTO();
		
		dto.setId(entity.getId());
		dto.setCreationTime(entity.getCreationTime());
		dto.setModificationTime(entity.getModificationTime());

		this.hydrateDtoFromEntity(entity, dto);
		
		return dto;
	}

	@Override
	public void hydrateEntityFromDto(MessageLog entity, MessageLogDTO dto) {
		if (dto != null) {
			entity.setCreationTime(dto.getCreationTime());
			entity.setMessage(dto.getMessage());
			entity.setAuthorName(dto.getAuthorName());
		}
	}

	@Override
	public void hydrateDtoFromEntity(MessageLog entity, MessageLogDTO dto) {
		if (entity != null) {
			dto.setAuthorName(entity.getAuthorName());
			dto.setMessage(entity.getMessage());
			
			if (entity.getScrim() != null) {
				dto.setScrim_id(entity.getScrim().getId());
			}
		}
	}
	
}
