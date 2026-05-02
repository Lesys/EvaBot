package zzc.discord.evabot.core.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import zzc.discord.evabot.core.entity.Team;

@Repository
public interface TeamRepository extends AbstractRepository<Team> {	
	static String getByPlayerIdAndScrimIdString = """
			SELECT t.*
				FROM team t
				LEFT JOIN team_players tp ON tp.team_id = t.id
				LEFT JOIN scrim s ON t.scrim_id = s.id
			WHERE s.id = :scrimId
				AND tp.player_id = :playerId
			""";
	
	@Query(value = TeamRepository.getByPlayerIdAndScrimIdString, nativeQuery = true)
	Team getByPlayerIdAndScrimId(@Param("playerId") Integer playerId, @Param("scrimId") Integer scrimId);
}
