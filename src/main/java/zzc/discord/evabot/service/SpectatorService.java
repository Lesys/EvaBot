package zzc.discord.evabot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import zzc.discord.evabot.core.entity.Spectator;
import zzc.discord.evabot.core.repository.SpectatorRepository;
import zzc.discord.evabot.dto.SpectatorDTO;
import zzc.discord.evabot.formatter.SpectatorFormatter;

@Service
public class SpectatorService extends AbstractService<Spectator, SpectatorDTO> {
	
	private final SpectatorRepository spectatorRepository;
	
	private final SpectatorFormatter spectatorFormatter;
	
	@Autowired
	public SpectatorService(SpectatorRepository spectatorRepository, SpectatorFormatter spectatorFormatter) {
		this.spectatorRepository = spectatorRepository;
		this.spectatorFormatter = spectatorFormatter;
	}

	@Override
	public SpectatorRepository getRepository() {
		return this.spectatorRepository;
	}

	@Override
	public SpectatorFormatter getFormatter() {
		return this.spectatorFormatter;
	}
	
}
