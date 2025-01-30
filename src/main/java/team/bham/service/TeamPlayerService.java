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
import team.bham.domain.TeamPlayer;
import team.bham.domain.enumeration.TeamRole;
import team.bham.repository.TeamPlayerRepository;
import team.bham.service.dto.PlayerDataDTO;
import team.bham.service.dto.TeamPlayerDTO;
import team.bham.service.mapper.PlayerDataMapper;
import team.bham.service.mapper.TeamPlayerMapper;

/**
 * Service Implementation for managing {@link TeamPlayer}.
 */
@Service
@Transactional
public class TeamPlayerService {

    private final Logger log = LoggerFactory.getLogger(TeamPlayerService.class);

    private final TeamPlayerRepository teamPlayerRepository;

    private final TeamPlayerMapper teamPlayerMapper;

    private final PlayerDataMapper playerDataMapper;

    public TeamPlayerService(
        TeamPlayerRepository teamPlayerRepository,
        TeamPlayerMapper teamPlayerMapper,
        PlayerDataMapper playerDataMapper
    ) {
        this.teamPlayerRepository = teamPlayerRepository;
        this.teamPlayerMapper = teamPlayerMapper;
        this.playerDataMapper = playerDataMapper;
    }

    /**
     * Save a teamPlayer.
     *
     * @param teamPlayerDTO the entity to save.
     * @return the persisted entity.
     */
    public TeamPlayerDTO save(TeamPlayerDTO teamPlayerDTO) {
        log.debug("Request to save TeamPlayer : {}", teamPlayerDTO);
        TeamPlayer teamPlayer = teamPlayerMapper.toEntity(teamPlayerDTO);
        teamPlayer = teamPlayerRepository.save(teamPlayer);
        return teamPlayerMapper.toDto(teamPlayer);
    }

    /**
     * Update a teamPlayer.
     *
     * @param teamPlayerDTO the entity to save.
     * @return the persisted entity.
     */
    public TeamPlayerDTO update(TeamPlayerDTO teamPlayerDTO) {
        log.debug("Request to update TeamPlayer : {}", teamPlayerDTO);
        TeamPlayer teamPlayer = teamPlayerMapper.toEntity(teamPlayerDTO);
        teamPlayer = teamPlayerRepository.save(teamPlayer);
        return teamPlayerMapper.toDto(teamPlayer);
    }

    /**
     * Partially update a teamPlayer.
     *
     * @param teamPlayerDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TeamPlayerDTO> partialUpdate(TeamPlayerDTO teamPlayerDTO) {
        log.debug("Request to partially update TeamPlayer : {}", teamPlayerDTO);

        return teamPlayerRepository
            .findById(teamPlayerDTO.getId())
            .map(existingTeamPlayer -> {
                teamPlayerMapper.partialUpdate(existingTeamPlayer, teamPlayerDTO);

                return existingTeamPlayer;
            })
            .map(teamPlayerRepository::save)
            .map(teamPlayerMapper::toDto);
    }

    /**
     * Get all the teamPlayers.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TeamPlayerDTO> findAll() {
        log.debug("Request to get all TeamPlayers");
        return teamPlayerRepository.findAll().stream().map(teamPlayerMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one teamPlayer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TeamPlayerDTO> findOne(Long id) {
        log.debug("Request to get TeamPlayer : {}", id);
        return teamPlayerRepository.findById(id).map(teamPlayerMapper::toDto);
    }

    /**
     * Get one teamPlayer by pdId and teamId.
     *
     * @param pdid the id of the PlayerData.
     * @param teamid the id of the Team.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TeamPlayerDTO> findOneByPlayerDataIdAndTeamId(Long pdid, Long teamid) {
        log.debug("Request to get TeamPlayer By PlayerDataId and TeamId : {}, {}", pdid, teamid);
        return teamPlayerRepository.findOneByPlayerIdAndTeamId(pdid, teamid).map(teamPlayerMapper::toDto);
    }

    public List<TeamPlayerDTO> findLeader(Long pdid) {
        return teamPlayerRepository
            .findAllByRoleAndPlayerId(TeamRole.LEADER, pdid)
            .stream()
            .map(teamPlayerMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all teamPlayer by teamId
     *
     * @param id the id of the teamId.
     * @return the list of teamPlayer entities.
     */
    @Transactional(readOnly = true)
    public List<TeamPlayerDTO> findPlayersInTeam(Long id) {
        log.debug("Request to get Players From TeamId: {}", id);
        return teamPlayerRepository
            .findAllByTeamId(id)
            .stream()
            .map(teamPlayerMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Transactional(readOnly = true)
    public List<TeamPlayerDTO> findPlayersInTeamWithPlayer(Long id) {
        log.debug("Request to get Players From TeamId: {}", id);
        return teamPlayerRepository
            .findAllByTeamIdWithPlayerData(id)
            .stream()
            .map(teamPlayerMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Transactional(readOnly = true)
    public Optional<TeamPlayerDTO> findTeamPlayerByPlayerId(Long id) {
        return teamPlayerRepository.findOneByPlayerId(id).map(teamPlayerMapper::toDto);
    }

    /**
     * Delete the teamPlayer by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete TeamPlayer : {}", id);
        teamPlayerRepository.deleteById(id);
    }
}
