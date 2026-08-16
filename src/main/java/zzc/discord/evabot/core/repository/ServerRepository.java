package zzc.discord.evabot.core.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import zzc.discord.evabot.core.entity.Server;

@Repository
public interface ServerRepository extends AbstractRepository<Server> {
	public Server getByServerId(@Param("serverId") String serverId);
}
