package team.bham.service;

import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.bham.domain.*;
import team.bham.domain.enumeration.AccessStatus;
import team.bham.domain.enumeration.TeamRole;
import team.bham.repository.PlayerDataRepository;
import team.bham.repository.TeamPlayerRepository;
import team.bham.repository.TeamRepository;
import team.bham.repository.UserRepository;
import team.bham.security.SecurityUtils;
import team.bham.service.dto.JoinTeamDTO;
import team.bham.service.dto.TeamDTO;
import team.bham.service.mapper.TeamMapper;

/**
 * Service Implementation for managing {@link Team}.
 */
@Service
@Transactional
public class TeamService {

    private final Logger log = LoggerFactory.getLogger(TeamService.class);

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final PlayerDataRepository playerDataRepository;
    private final TeamPlayerRepository teamPlayerRepository;

    private final TeamMapper teamMapper;

    private final MatchService matchService;

    public TeamService(
        TeamRepository teamRepository,
        UserRepository userRepository,
        PlayerDataRepository playerDataRepository,
        TeamPlayerRepository teamPlayerRepository,
        TeamMapper teamMapper,
        MatchService matchService
    ) {
        this.teamRepository = teamRepository;
        this.teamMapper = teamMapper;
        this.userRepository = userRepository;
        this.playerDataRepository = playerDataRepository;
        this.teamPlayerRepository = teamPlayerRepository;
        this.matchService = matchService;
    }

    /**
     * Save a team.
     *
     * @param teamDTO the entity to save.
     * @return the persisted entity.
     */
    public TeamDTO save(TeamDTO teamDTO) {
        log.debug("Request to save Team : {}", teamDTO);
        Team team = teamMapper.toEntity(teamDTO);
        team = teamRepository.save(team);
        return teamMapper.toDto(team);
    }

    @Transactional
    public TeamDTO create(TeamDTO teamDTO) {
        log.debug("Request to create Team: {}", teamDTO);
        teamRepository
            .findOneByName(teamDTO.getName())
            .ifPresent(existingTeam -> {
                throw new TeamAlreadyExistException();
            });

        Team newTeam = teamMapper.toEntity(teamDTO);

        SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .flatMap(playerDataRepository::findOneByUser)
            .ifPresentOrElse(
                playerData -> {
                    TeamPlayer teamLeader = new TeamPlayer();

                    teamLeader.setTeam(newTeam);
                    teamLeader.setRole(TeamRole.LEADER);
                    teamLeader.setPlayer(playerData);
                    teamLeader.setAccepted(true);

                    teamPlayerRepository.save(teamLeader);
                    log.debug("Created team player {} for team {}", playerData.getName(), newTeam.getName());
                },
                () -> {
                    throw new UserMissingPlayerDataException();
                }
            );

        teamRepository.save(newTeam);
        log.debug("Created team: {}", newTeam);
        log.debug("Created team dto: {}", teamMapper.toDto(newTeam).toString());

        return teamMapper.toDto(newTeam);
    }

    @Transactional
    public void join(long id) {
        log.debug(SecurityUtils.getCurrentUserLogin().toString());
        SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .flatMap(playerDataRepository::findOneByUser)
            .ifPresent(playerData ->
                teamRepository
                    .findOneById(id)
                    .ifPresentOrElse(
                        team -> {
                            if (team.getAccessStatus() == AccessStatus.CLOSED) throw new TeamClosedException();

                            TeamPlayer teamPlayer = new TeamPlayer();
                            if (teamPlayerRepository.findOneByTeamAndPlayer(team, playerData).isPresent()) {
                                throw new TeamAlreadyJoinedException();
                            }
                            teamPlayer.setTeam(team);
                            teamPlayer.setRole(TeamRole.PLAYER);
                            teamPlayer.setPlayer(playerData);
                            teamPlayer.setAccepted(team.getAccessStatus() == AccessStatus.PUBLIC);

                            log.debug(teamPlayer.toString());

                            teamPlayerRepository.save(teamPlayer);
                            log.debug("Created team player {} for team {}", playerData.getName(), team.getName());
                        },
                        () -> {
                            throw new TeamDoesNotExistException();
                        }
                    )
            );
    }

    @Transactional
    public void leave(long id) {
        log.debug(SecurityUtils.getCurrentUserLogin().toString());
        SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .flatMap(playerDataRepository::findOneByUser)
            .ifPresent(playerData ->
                teamRepository
                    .findOneById(id)
                    .ifPresentOrElse(
                        team -> {
                            teamPlayerRepository
                                .findOneByTeamAndPlayer(team, playerData)
                                .ifPresent(teamPlayer -> {
                                    teamPlayerRepository.delete(teamPlayer);
                                });
                            log.debug("Deleted team player {} for team {}", playerData.getName(), team.getName());
                        },
                        () -> {
                            throw new TeamDoesNotExistException();
                        }
                    )
            );
    }

    /**
     * Update a team.
     *
     * @param teamDTO the entity to save.
     * @return the persisted entity.
     */
    public TeamDTO update(TeamDTO teamDTO) {
        log.debug("Request to update Team : {}", teamDTO);
        Team team = teamMapper.toEntity(teamDTO);
        team = teamRepository.save(team);
        return teamMapper.toDto(team);
    }

    /**
     * Partially update a team.
     *
     * @param teamDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TeamDTO> partialUpdate(TeamDTO teamDTO) {
        log.debug("Request to partially update Team : {}", teamDTO);

        return teamRepository
            .findById(teamDTO.getId())
            .map(existingTeam -> {
                teamMapper.partialUpdate(existingTeam, teamDTO);

                return existingTeam;
            })
            .map(teamRepository::save)
            .map(teamMapper::toDto);
    }

    /**
     * Get all the teams.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TeamDTO> findAll() {
        log.debug("Request to get all Teams");
        return teamRepository.findAll().stream().map(teamMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Transactional(readOnly = true)
    public List<TeamDTO> findAllWithPlayer(long playerId) {
        log.debug("Request to get all Teams containing player " + playerId);
        List<TeamDTO> teams = new LinkedList<>();
        playerDataRepository
            .findOneById(playerId)
            .ifPresent(playerData -> {
                teamPlayerRepository
                    .findAllByPlayer(playerData)
                    .forEach(teamPlayer -> {
                        if (!teams.contains(teamMapper.toDto(teamPlayer.getTeam()))) {
                            teams.add(teamMapper.toDto(teamPlayer.getTeam()));
                        }
                    });
            });
        return teams;
    }

    @Transactional(readOnly = true)
    public List<TeamDTO> findTopTeams() {
        log.debug("Request to get top Teams");
        int n = 10;
        List<TeamDTO> teams = new LinkedList<>();
        teamRepository
            .findAll()
            .stream()
            .sorted((teamA, teamB) -> {
                int teamAVictories = this.matchService.findAllVictoryByTeamId(teamA.getId()).size();
                int teamBVictories = this.matchService.findAllVictoryByTeamId(teamB.getId()).size();
                return teamBVictories - teamAVictories;
            })
            .forEach(team -> {
                if (teams.size() < n) {
                    teams.add(teamMapper.toDto(team));
                }
            });
        return teams;
    }

    /**
     * Get one team by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TeamDTO> findOne(Long id) {
        log.debug("Request to get Team : {}", id);
        return teamRepository.findById(id).map(teamMapper::toDto);
    }

    /**
     * Delete the team by id.
     *
     * @param id the id of the entity.
     */
    @Transactional
    public void delete(Long id) {
        log.debug("Request to delete Team : {}", id);
        teamRepository
            .findOneById(id)
            .ifPresent(team -> {
                teamPlayerRepository.findAllByTeam(team).forEach(teamPlayerRepository::delete);
            });
        teamRepository.deleteById(id);
    }

    public List<TeamDTO> findTeamDataWithPlayerId(Long playerId) {
        return teamRepository.findAllByPlayerId(playerId).stream().map(teamMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Transactional(readOnly = true)
    public List<Team> getTeamByNameLike(String name) {
        String pattern = '%' + name + '%';
        return teamRepository.getTeamByNameLike(pattern);
    }
}
