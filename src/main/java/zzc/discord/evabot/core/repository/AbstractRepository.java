package zzc.discord.evabot.core.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AbstractRepository<ENTITY> extends CrudRepository<ENTITY, Integer> {	
	ENTITY getById(Integer id);
}
