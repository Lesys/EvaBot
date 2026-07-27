package zzc.discord.evabot.service;

import org.springframework.stereotype.Service;

import zzc.discord.evabot.core.entity.Season;
import zzc.discord.evabot.core.repository.SeasonRepository;
import zzc.discord.evabot.dto.SeasonDTO;
import zzc.discord.evabot.formatter.SeasonFormatter;

@Service
public class SeasonService extends AbstractService<Season, SeasonDTO> {
	private final SeasonRepository seasonRepository;

	private final SeasonFormatter seasonFormatter;
	
	public SeasonService(SeasonRepository seasonRepository, SeasonFormatter seasonFormatter) {
		this.seasonRepository = seasonRepository;
		this.seasonFormatter = seasonFormatter;
	}

	@Override
	public SeasonRepository getRepository() {
		return this.seasonRepository;
	}

	@Override
	public SeasonFormatter getFormatter() {
		return this.seasonFormatter;
	}	
}
