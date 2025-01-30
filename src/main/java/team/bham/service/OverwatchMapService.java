package team.bham.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.bham.domain.OverwatchMap;
import team.bham.repository.OverwatchMapRepository;
import team.bham.service.dto.OverwatchMapDTO;
import team.bham.service.mapper.OverwatchMapMapper;

/**
 * Service Implementation for managing {@link OverwatchMap}.
 */
@Service
@Transactional
public class OverwatchMapService {

    private final Logger log = LoggerFactory.getLogger(OverwatchMapService.class);

    private final OverwatchMapRepository overwatchMapRepository;

    private final OverwatchMapMapper overwatchMapMapper;

    public OverwatchMapService(OverwatchMapRepository overwatchMapRepository, OverwatchMapMapper overwatchMapMapper) {
        this.overwatchMapRepository = overwatchMapRepository;
        this.overwatchMapMapper = overwatchMapMapper;
    }

    /**
     * Save a overwatchMap.
     *
     * @param overwatchMapDTO the entity to save.
     * @return the persisted entity.
     */
    public OverwatchMapDTO save(OverwatchMapDTO overwatchMapDTO) {
        log.debug("Request to save OverwatchMap : {}", overwatchMapDTO);
        OverwatchMap overwatchMap = overwatchMapMapper.toEntity(overwatchMapDTO);
        overwatchMap = overwatchMapRepository.save(overwatchMap);
        return overwatchMapMapper.toDto(overwatchMap);
    }

    /**
     * Update a overwatchMap.
     *
     * @param overwatchMapDTO the entity to save.
     * @return the persisted entity.
     */
    public OverwatchMapDTO update(OverwatchMapDTO overwatchMapDTO) {
        log.debug("Request to update OverwatchMap : {}", overwatchMapDTO);
        OverwatchMap overwatchMap = overwatchMapMapper.toEntity(overwatchMapDTO);
        overwatchMap = overwatchMapRepository.save(overwatchMap);
        return overwatchMapMapper.toDto(overwatchMap);
    }

    /**
     * Partially update a overwatchMap.
     *
     * @param overwatchMapDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OverwatchMapDTO> partialUpdate(OverwatchMapDTO overwatchMapDTO) {
        log.debug("Request to partially update OverwatchMap : {}", overwatchMapDTO);

        return overwatchMapRepository
            .findById(overwatchMapDTO.getId())
            .map(existingOverwatchMap -> {
                overwatchMapMapper.partialUpdate(existingOverwatchMap, overwatchMapDTO);

                return existingOverwatchMap;
            })
            .map(overwatchMapRepository::save)
            .map(overwatchMapMapper::toDto);
    }

    /**
     * Get all the overwatchMaps.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<OverwatchMapDTO> findAll() {
        log.debug("Request to get all OverwatchMaps");
        return overwatchMapRepository.findAll().stream().map(overwatchMapMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get all the overwatchMaps where Game is {@code null}.
     *  @return the list of entities.
     
    @Transactional(readOnly = true)
    public List<OverwatchMapDTO> findAllWhereGameIsNull() {
        log.debug("Request to get all overwatchMaps where Game is null");
        return StreamSupport
            .stream(overwatchMapRepository.findAll().spliterator(), false)
            .filter(overwatchMap -> overwatchMap.getGame() == null)
            .map(overwatchMapMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }
    */

    /**
     * Get one overwatchMap by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OverwatchMapDTO> findOne(Long id) {
        log.debug("Request to get OverwatchMap : {}", id);
        return overwatchMapRepository.findById(id).map(overwatchMapMapper::toDto);
    }

    /**
     * Delete the overwatchMap by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete OverwatchMap : {}", id);
        overwatchMapRepository.deleteById(id);
    }
}
