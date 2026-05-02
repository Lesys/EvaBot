package zzc.discord.evabot.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import zzc.discord.evabot.core.entity.Scrim;

@Repository
public interface ScrimRepository extends AbstractRepository<Scrim> {
	
	@Query(value = "SELECT s.* FROM scrim s WHERE discord_server_name = :discordName AND channel_name = :channelName ORDER BY creation_time DESC", nativeQuery = true)
	public List<Scrim> getByDiscordNameAndChannelName(@Param("discordName") String discordName, @Param("channelName") String channelName);

	static String getAllByPlayerIdString = """
			SELECT s.*
				FROM scrim s
				LEFT JOIN team t ON t.scrim_id = s.id
				LEFT JOIN team_players tp ON tp.team_id = t.id
			WHERE tp.player_id = :playerId
			""";
	@Query(value = getAllByPlayerIdString, nativeQuery = true)
	public List<Scrim> getAllByPlayerId(@Param("playerId") Integer playerId);
	
//	@Query(value = "SELECT p.* FROM scrim s LEFT JOIN team t ON t.scrim_id = s.id LEFT JOIN team_players tp ON tp.team_id = t.id LEFT JOIN er_player p ON p.id = tp.player_id WHERE channel_id = :channelId", nativeQuery = true)
	public Scrim getByChannelId(@Param("channelId") String channelId);
}
