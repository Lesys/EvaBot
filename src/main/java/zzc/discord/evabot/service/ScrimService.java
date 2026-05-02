package zzc.discord.evabot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.core.entity.Scrim;
import zzc.discord.evabot.core.repository.ScrimRepository;
import zzc.discord.evabot.dto.ScrimDTO;
import zzc.discord.evabot.formatter.ScrimFormatter;

@Service
public class ScrimService extends AbstractService<Scrim, ScrimDTO> {
	
	private final ScrimRepository scrimRepository;
	
	private final ScrimFormatter scrimFormatter;
	
	@Autowired
	public ScrimService(ScrimRepository scrimRepository, ScrimFormatter scrimFormatter) {
		this.scrimRepository = scrimRepository;
		this.scrimFormatter = scrimFormatter;
	}

	@Override
	public ScrimFormatter getFormatter() {
		return this.scrimFormatter;
	}

	@Override
	public ScrimRepository getRepository() {
		return this.scrimRepository;
	}
	
	@Transactional
	public ScrimDTO getByEvent(MessageReceivedEvent event) {
		if (event == null || event.getChannel() == null) return null;
		
		String channelId = event.getChannel().getId();

		System.out.println("[ScrimService] getByEvent " + channelId);
		
		return this.getByChannelId(channelId);
	}
	
	@Transactional
	public ScrimDTO getByChannelId(String channelId) {
		if (channelId == null) return null;

		System.out.println("[ScrimService] getByChannelId " + channelId);
		
		return this.getFormatter().entityToDto(this.getRepository().getByChannelId(channelId));
	}
	
	@Transactional
	public List<ScrimDTO> getAllByPlayerId(Integer playerId) {
		if (playerId == null) return new ArrayList<>();
		
		return this.getFormatter().entityToDto(this.getRepository().getAllByPlayerId(playerId));
	}
	
	@Transactional
	public void removeAllDeletedScrims(JDA jda) {
		List<Scrim> toDelete = new ArrayList<>();
		Iterable<Scrim> it = this.getRepository().findAll();
		
		it.forEach(scrim -> {
			if (jda.getTextChannelById(scrim.getChannelId()) == null) {
				toDelete.add(scrim);
			}
		});

		this.getRepository().deleteAll(toDelete);
	}
	
	@Transactional
	public void removeTeam(Scrim scrim, Integer teamId) {
		scrim.getTeams().removeIf(team -> team.getId().equals(teamId));
		
		this.getRepository().save(scrim);
	}
	
}
