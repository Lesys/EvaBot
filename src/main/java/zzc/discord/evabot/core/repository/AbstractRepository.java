package zzc.discord.evabot.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AbstractRepository<ENTITY> extends JpaRepository<ENTITY, Integer> {	
	ENTITY getById(Integer id);
}
