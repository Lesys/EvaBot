package zzc.discord.evabot.events;


import java.util.List;
import java.util.NoSuchElementException;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import zzc.discord.evabot.dto.ScrimDTO;
import zzc.discord.evabot.dto.ServerDTO;
import zzc.discord.evabot.service.ScrimService;
import zzc.discord.evabot.service.ServerService;
import zzc.discord.evabot.util.UtilBoolean;

/**
 * 
 * @author Lesys
 *
 * Class of EventER that adds a helper role to the server so they can use admin commands themselves (except this one)
 */
@Service
public class EventERAddHelperRole extends EventER {
	private final transient ServerService serverService;
	/**
	 * Constructor of EventERAddHelperRole
	 */
	public EventERAddHelperRole(ServerService serverService) {
		this.commandName += "addHelperRole";
		
		this.serverService = serverService;
	}
	
	/**
	 * Check if the	helping role is already allowed to for the server and if not, adds it
	 */
	@Override
	@Transactional
	public void executeCommand(@NotNull MessageReceivedEvent event) {
		String[] message = this.getMessageArray(event);

		String helperRoleName = "";
		List<Role> roles = event.getMessage().getMentions().getRoles();
		
		Role role = null;
		
		if (roles.size() == 1) {
			role = roles.getFirst();
			helperRoleName = role.getName();
		} else {			
			for (int i = 0; i < message.length; i++)
				helperRoleName += (helperRoleName != "" ? " " : "") + message[i];
			
			roles = event.getGuild().getRolesByName(helperRoleName, true);
			
			if (roles.size() > 0) {
				role = roles.getFirst();
			}
		}
		
		if (role != null) {				
			ServerDTO server = this.serverService.getByEvent(event);
			
			if (server == null) {
				server = new ServerDTO(event.getGuild().getName(), event.getGuild().getId());

				server = this.serverService.getFormatter().entityToDto(this.serverService.save(server));
			}
			
			if (UtilBoolean.isFalse(server.alreadyHelperRole(role))) {
				if (EventERManager.hasPermissionAdminOnly(event)) {
					server.addHelperRole(role);
					
					this.serverService.save(server);
					
					this.commandIsSuccessful();
				} else {
					event.getChannel().sendMessage(event.getAuthor().getAsMention() + " does not have the rights to use this command. Only an admin of the server can use it.").queue();
				}
			} else {
				event.getChannel().sendMessage(helperRoleName + " is already in the helper role list for this server.").queue();
			}
		} else {
			event.getChannel().sendMessage(helperRoleName + " has not been found as a role in this server, please check the syntaxe or if the role exists.").queue();
		}
	}

	@Override
	public String helpCommand() {
		return super.helpCommand() + " @{RoleMention} - Adds the role to the helper role list that are able to use commands as an admin (except this one). Can be a string with the unique discord name instead of a mention.\n";
	}
}
