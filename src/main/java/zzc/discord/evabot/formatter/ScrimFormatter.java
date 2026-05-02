package zzc.discord.evabot.formatter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import zzc.discord.evabot.core.entity.Scrim;
import zzc.discord.evabot.core.repository.ScrimRepository;
import zzc.discord.evabot.dto.ScrimDTO;
import zzc.discord.evabot.util.UtilEmpty;

@Service
public class ScrimFormatter implements AbstractFormatter<Scrim, ScrimDTO> {
	
	private transient ScrimRepository scrimRepository;
	
	private transient TeamFormatter teamFormatter;
	
	private transient SpectatorFormatter spectatorFormatter;
	
	private transient MessageLogFormatter messageLogFormatter;
	
	@Autowired
	public ScrimFormatter(ScrimRepository scrimRepository, TeamFormatter teamFormatter, SpectatorFormatter spectatorFormatter,
			MessageLogFormatter messageLogFormatter) {
		this.scrimRepository = scrimRepository;
		this.teamFormatter = teamFormatter;
		this.spectatorFormatter = spectatorFormatter;
		this.messageLogFormatter = messageLogFormatter;
	}

	@Override
	public List<Scrim> dtoToEntity(List<ScrimDTO> dtoList) {
		List<Scrim> entityList = new ArrayList<>();
		
		if (!UtilEmpty.isEmptyOrNull(dtoList)) {
			for (ScrimDTO dto : dtoList) {
				entityList.add(this.dtoToEntity(dto));
			}
		}
		
		return entityList;
	}

	@Override
	public List<ScrimDTO> entityToDto(List<Scrim> entityList) {
		List<ScrimDTO> dtoList = new ArrayList<>();
		
		if (!UtilEmpty.isEmptyOrNull(entityList)) {
			for (Scrim entity : entityList) {
				dtoList.add(this.entityToDto(entity));
			}
		}
		
		return dtoList;
	}

	@Override
	public Scrim dtoToEntity(ScrimDTO dto) {
		Scrim entity = null;
		
		if (dto == null) return entity;
		
		if (dto.getId() != null) {
			entity = this.scrimRepository.getById(dto.getId());
		} else {
			entity = new Scrim();
		}
		
		this.hydrateEntityFromDto(entity, dto);
		
		return entity;
	}

	@Override
	public ScrimDTO entityToDto(Scrim entity) {
		ScrimDTO dto = null;
		
		if (entity == null) return dto;
		
		dto = new ScrimDTO();
		
		dto.setId(entity.getId());
		dto.setCreationTime(entity.getCreationTime());
		dto.setModificationTime(entity.getModificationTime());

		this.hydrateDtoFromEntity(entity, dto);
		
		return dto;
	}

	@Override
	public void hydrateEntityFromDto(Scrim entity, ScrimDTO dto) {
		if (dto != null) {
			entity.setChannelName(dto.getChannelName());
			entity.setDiscordServerName(dto.getDiscordServerName());
			entity.setChannelId(dto.getChannelId());
			
			if (!UtilEmpty.isEmptyOrNull(dto.getTeamList())) {
				entity.setTeams(this.teamFormatter.dtoToEntity(dto.getTeamList()));
				entity.getTeams().forEach(team -> team.setScrim(entity));
//				entity.setHistoryPlayerNameList(this.historyNameFormatter.dtoToEntity(dto.getHistoryPlayerNameList()));
//				entity.getHistoryPlayerNameList().forEach(hpn -> hpn.setPlayer(entity));
			} else {
				entity.setTeams(new ArrayList<>());			
			}
			
			if (!UtilEmpty.isEmptyOrNull(dto.getSpectatorList())) {
				entity.setSpectators(this.spectatorFormatter.dtoToEntity(dto.getSpectatorList()));
				entity.getSpectators().forEach(spec -> spec.setScrim(entity));
			} else {
				entity.setSpectators(new ArrayList<>());				
			}
			
			if (!UtilEmpty.isEmptyOrNull(dto.getMessageLogList())) {
				entity.setMessageLogs(this.messageLogFormatter.dtoToEntity(dto.getMessageLogList()));
				entity.getMessageLogs().forEach(mess -> mess.setScrim(entity));
			} else {
				entity.setMessageLogs(new ArrayList<>());				
			}
			
//			if (entity.getHistoryPlayerNameList().stream()
//					.map(HistoryName::get)
//					.filter(name -> name.equalsIgnoreCase(dto.getDakName())) != null) {
//				entity.getHistoryPlayerNameList().add(entity.getDakName());
//			}
		}
	}

	@Override
	public void hydrateDtoFromEntity(Scrim entity, ScrimDTO dto) {
		if (entity != null) {
			dto.setChannelName(entity.getChannelName());
			dto.setDiscordServerName(entity.getDiscordServerName());
			dto.setChannelId(entity.getChannelId());
			
			dto.setTeamList(this.teamFormatter.entityToDto(entity.getTeams()));
			dto.setSpectatorList(this.spectatorFormatter.entityToDto(entity.getSpectators()));
			dto.setMessageLogList(this.messageLogFormatter.entityToDto(entity.getMessageLogs()));
		}
	}
	
}
