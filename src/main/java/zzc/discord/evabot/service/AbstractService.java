package zzc.discord.evabot.service;


import jakarta.transaction.Transactional;
import zzc.discord.evabot.core.entity.AbstractEntity;
import zzc.discord.evabot.core.repository.AbstractRepository;
import zzc.discord.evabot.dto.AbstractDTO;
import zzc.discord.evabot.formatter.AbstractFormatter;

public abstract class AbstractService<ENTITY extends AbstractEntity, DTO extends AbstractDTO> {
	
	public abstract AbstractRepository<ENTITY> getRepository();
	
	public abstract AbstractFormatter<ENTITY, DTO> getFormatter();
	
	@Transactional
	public ENTITY getById(Integer id) {
		System.out.println("[" + this.getClass().getSimpleName() + "] getById " + id);
		return this.getRepository().findById(id).orElse(null);
	}
	
	@Transactional
	public DTO getDtoById(Integer id) {
		System.out.println("[" + this.getClass().getSimpleName() + "] getDtoById " + id);
		return this.getFormatter().entityToDto(this.getById(id));
	}
	
	@Transactional
	public ENTITY save(DTO dto) {
		ENTITY entity = this.getFormatter().dtoToEntity(dto);
		
		if (entity != null) {
			System.out.println("[" + this.getClass().getSimpleName() + "] save");
			entity = this.getRepository().saveAndFlush(entity);
		}
		return entity;
	}	
	
	@Transactional
	public DTO saveToDto(DTO dto) {
		return this.getFormatter().entityToDto(this.save(dto));
	}	
	
	@Transactional
	public ENTITY onDelete(ENTITY entity) {
		return entity;
	}
	
	@Transactional
	public void deleteById(Integer id) {		
		ENTITY entity = this.getRepository().getById(id);
		
		if (entity != null) {
			entity = this.onDelete(entity);
			System.out.println("[" + this.getClass().getSimpleName() + "] delete from id " + entity.getId());
			this.getRepository().deleteById(entity.getId());
			
		}
	}
	
	@Transactional
	public void delete(DTO dto) {		
		ENTITY entity = this.getFormatter().dtoToEntity(dto);
		
		if (entity != null) {
			entity = this.onDelete(entity);
			System.out.println("[" + this.getClass().getSimpleName() + "] delete from id " + entity.getId());
			this.getRepository().delete(entity);
		}
	}
}
