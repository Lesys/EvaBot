package zzc.discord.evabot.formatter;

import java.util.List;

/**
 * Abstract class to format an entity to a DTO and vice versa
 * @author Lesys
 *
 * @param <ENTITY>		The class corresponding to the entity to use
 * @param <DTO>			The class corresponding to the DTO to use
 */
public interface AbstractFormatter<ENTITY, DTO> {
	/**
	 * Formats a list of DTO to a list of entity
	 * @param dtoList		The list to format to entities
	 * @return				The formatted list of DTO
	 */
	public List<ENTITY> dtoToEntity(List<DTO> dtoList);

	/**
	 * Formats a list of entities to a list of DTO
	 * @param entityList	The list to format to DTO
	 * @return				The formatted list of entities
	 */
	public List<DTO> entityToDto(List<ENTITY> entityList);
	
	/**
	 * Formats the DTO to the entity
	 * @param dto	The DTO to format
	 * @return		The formatted entity
	 */
	public ENTITY dtoToEntity(DTO dto);
	
	/**
	 * Formats the entity to the DTO
	 * @param entity	The entity to format
	 * @return			The formatted DTO
	 */
	public DTO entityToDto(ENTITY entity);
	
	/**
	 * Fills the entity of all the DTO values
	 * @param entity		The entity to fill
	 * @param dto			The DTO from where the values come
	 */
	public void hydrateEntityFromDto(ENTITY entity, DTO dto);
	
	/**
	 * Fills the DTO of all the entity values
	 * @param entity		The entity from where the values come
	 * @param dto			The DTO to fill
	 */	
	public void hydrateDtoFromEntity(ENTITY entity, DTO dto);
}
