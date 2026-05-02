package zzc.discord.evabot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import zzc.discord.evabot.core.entity.MessageLog;
import zzc.discord.evabot.core.repository.MessageLogRepository;
import zzc.discord.evabot.dto.MessageLogDTO;
import zzc.discord.evabot.formatter.MessageLogFormatter;

@Service
public class MessageLogService extends AbstractService<MessageLog, MessageLogDTO> {
	
	private final MessageLogRepository messageLogRepository;
	
	private final MessageLogFormatter messageLogFormatter;
	
	@Autowired
	public MessageLogService(MessageLogRepository messageLogRepository, MessageLogFormatter messageLogFormatter) {
		this.messageLogRepository = messageLogRepository;
		this.messageLogFormatter = messageLogFormatter;
	}

	@Override
	public MessageLogRepository getRepository() {
		return this.messageLogRepository;
	}

	@Override
	public MessageLogFormatter getFormatter() {
		return this.messageLogFormatter;
	}
	
}
