package team.bham.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.bham.domain.PlayerData;
import team.bham.domain.User;
import team.bham.repository.PlayerDataRepository;
import team.bham.service.dto.PlayerDataDTO;
import team.bham.service.mapper.PlayerDataMapper;

/**
 * Service Implementation for managing {@link PlayerData}.
 */
@Service
@Transactional
public class PlayerDataService {

    private final Logger log = LoggerFactory.getLogger(PlayerDataService.class);

    private final PlayerDataRepository playerDataRepository;

    private final PlayerDataMapper playerDataMapper;

    public PlayerDataService(PlayerDataRepository playerDataRepository, PlayerDataMapper playerDataMapper) {
        this.playerDataRepository = playerDataRepository;
        this.playerDataMapper = playerDataMapper;
    }

    /**
     * Save a playerData.
     *
     * @param playerDataDTO the entity to save.
     * @return the persisted entity.
     */
    public PlayerDataDTO save(PlayerDataDTO playerDataDTO) {
        log.debug("Request to save PlayerData : {}", playerDataDTO);
        PlayerData playerData = playerDataMapper.toEntity(playerDataDTO);
        playerData = playerDataRepository.save(playerData);
        return playerDataMapper.toDto(playerData);
    }

    public PlayerData createDefault(User user) {
        PlayerData newPlayerData = new PlayerData();
        newPlayerData.setName("default name");
        newPlayerData.setOverwatchUsername("default#0000");
        newPlayerData.setMatchesPlayed(0);
        newPlayerData.setTournamentsPlayed(0);
        newPlayerData.setMatchWins(0);
        newPlayerData.setTournamentWins(0);
        newPlayerData.setTournamentTop10s(0);
        newPlayerData.setUser(user);
        playerDataRepository.save(newPlayerData);
        return newPlayerData;
    }

    /**
     * Update a playerData.
     *
     * @param playerDataDTO the entity to save.
     * @return the persisted entity.
     */
    public PlayerDataDTO update(PlayerDataDTO playerDataDTO) {
        log.debug("Request to update PlayerData : {}", playerDataDTO);
        PlayerData playerData = playerDataMapper.toEntity(playerDataDTO);
        playerData = playerDataRepository.save(playerData);
        return playerDataMapper.toDto(playerData);
    }

    /**
     * Partially update a playerData.
     *
     * @param playerDataDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PlayerDataDTO> partialUpdate(PlayerDataDTO playerDataDTO) {
        log.debug("Request to partially update PlayerData : {}", playerDataDTO);

        return playerDataRepository
            .findById(playerDataDTO.getId())
            .map(existingPlayerData -> {
                playerDataMapper.partialUpdate(existingPlayerData, playerDataDTO);

                return existingPlayerData;
            })
            .map(playerDataRepository::save)
            .map(playerDataMapper::toDto);
    }

    /**
     * Get all the playerData.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PlayerDataDTO> findAll() {
        log.debug("Request to get all PlayerData");
        return playerDataRepository.findAll().stream().map(playerDataMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the playerData in List.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PlayerData> findAllByIds(List<Long> playerIds) {
        return playerDataRepository.findAllByIdIn(playerIds);
    }

    /**
     * Get all the playerData in Team :id
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Object[]> findAllByTeamId(Long id) {
        return playerDataRepository.findAllByTeamIdWithRole(id);
    }

    /**
     * Get the playerData of given UserId.
     *
     * @return playerData DTO of UserId.
     */
    @Transactional(readOnly = true)
    public Optional<PlayerDataDTO> findPlayerDataByUserId(Long id) {
        return playerDataRepository.findOneByUserId(id).map(playerDataMapper::toDto);
    }

    /**
     * Get one playerData by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PlayerDataDTO> findOne(Long id) {
        log.debug("Request to get PlayerData : {}", id);
        return playerDataRepository.findById(id).map(playerDataMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<PlayerDataDTO> findOneByName(String name) {
        return playerDataRepository.findOneByName(name).map(playerDataMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<PlayerData> findAllByNameLikeOrOverwatchUsernameLike(String name) {
        String pattern = '%' + name + '%';
        return playerDataRepository.findAllByNameLikeOrOverwatchUsernameLike(pattern, pattern);
    }

    @Transactional(readOnly = true)
    public List<Object[]> findAllByNameLikeOrOverwatchUsernameLikeWithTeams(String name) {
        String pattern = '%' + name + '%';
        return playerDataRepository.findAllByNameLikeOrOverwatchUsernameLikeWithTeams(pattern, pattern);
    }

    /**
     * Delete the playerData by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete PlayerData : {}", id);
        playerDataRepository.deleteById(id);
    }
}
