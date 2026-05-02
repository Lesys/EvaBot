package zzc.discord.evabot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import zzc.discord.evabot.core.entity.HistoryName;
import zzc.discord.evabot.core.repository.HistoryNameRepository;
import zzc.discord.evabot.dto.HistoryNameDTO;
import zzc.discord.evabot.formatter.HistoryNameFormatter;

@Service
public class HistoryNameService extends AbstractService<HistoryName, HistoryNameDTO> {
	
	private final HistoryNameRepository historyNameRepository;
	
	private final HistoryNameFormatter historyNameFormatter;
	
	@Autowired
	public HistoryNameService(HistoryNameRepository historyNameRepository, HistoryNameFormatter historyNameFormatter) {
		this.historyNameRepository = historyNameRepository;
		this.historyNameFormatter = historyNameFormatter;
	}

	@Override
	public HistoryNameRepository getRepository() {
		return this.historyNameRepository;
	}

	@Override
	public HistoryNameFormatter getFormatter() {
		return this.historyNameFormatter;
	}
	
}
