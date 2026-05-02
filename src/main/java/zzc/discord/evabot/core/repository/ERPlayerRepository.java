package zzc.discord.evabot.core.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import zzc.discord.evabot.core.entity.ERPlayer;

@Repository
public interface ERPlayerRepository extends AbstractRepository<ERPlayer> {

	@Override
	ERPlayer getById(Integer id);
	
	@Query(value = "SELECT * FROM er_player WHERE REVERSE(SUBSTRING_INDEX(REVERSE(dak), '/', 1)) LIKE :dakName LIMIT 1", nativeQuery = true)
	ERPlayer getByDakName(@Param("dakName") String dak);
	
	ERPlayer getByDiscordName(String discordName);
}
