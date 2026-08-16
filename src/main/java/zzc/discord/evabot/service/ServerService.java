package zzc.discord.evabot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.core.entity.Server;
import zzc.discord.evabot.core.repository.ServerRepository;
import zzc.discord.evabot.dto.HelperRoleDTO;
import zzc.discord.evabot.dto.ServerDTO;
import zzc.discord.evabot.formatter.ServerFormatter;

@Service
public class ServerService extends AbstractService<Server, ServerDTO> {
	
	private final ServerRepository servverRepository;
	
	private final ServerFormatter servverFormatter;
	
	@Autowired
	public ServerService(ServerRepository servverRepository, ServerFormatter servverFormatter) {
		this.servverRepository = servverRepository;
		this.servverFormatter = servverFormatter;
	}

	@Override
	public ServerFormatter getFormatter() {
		return this.servverFormatter;
	}

	@Override
	public ServerRepository getRepository() {
		return this.servverRepository;
	}
	
	@Transactional
	public ServerDTO getByEvent(MessageReceivedEvent event) {
		if (event == null || event.getGuild() == null) return null;
		
		String serverId = event.getGuild().getId();

		System.out.println("[ServerService] getByEvent " + serverId);
		
		return this.getByServerId(serverId);
	}
	
	@Transactional
	public ServerDTO getByServerId(String serverId) {
		if (serverId == null) return null;

		System.out.println("[ServerService] getByServerId " + serverId);
		
		return this.getFormatter().entityToDto(this.getRepository().getByServerId(serverId));
	}
	
	@Transactional
	public static boolean hasRoleInHelperList(MessageReceivedEvent event, ServerDTO server) {
		System.out.println("[ServerService] hasRoleInHelperList");
		boolean hasHelperRole = false;
		// Check if the sender has any role matching with the list of authorized helpers
		if (server != null && server.getHelperRoleList().size() > 0) {
			hasHelperRole = server.getHelperRoleList().stream().map(HelperRoleDTO::getEntityId).anyMatch(roleId -> event.getMessage().getMember().getRoles().stream().map(Role::getId).toList().contains(roleId));
		}
		
		return hasHelperRole;
	}	
}
