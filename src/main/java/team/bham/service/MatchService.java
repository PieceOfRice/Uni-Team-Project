package team.bham.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.bham.domain.Match;
import team.bham.repository.GamePlayerRepository;
import team.bham.repository.MatchRepository;
import team.bham.repository.PlayerDataRepository;
import team.bham.repository.TeamRepository;
import team.bham.repository.TournamentRepository;
import team.bham.service.dto.MatchDTO;
import team.bham.service.mapper.MatchMapper;

/**
 * Service Implementation for managing {@link Match}.
 */
@Service
@Transactional
public class MatchService {

    private final Logger log = LoggerFactory.getLogger(MatchService.class);

    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;
    private final TeamRepository teamRepository;
    private final PlayerDataRepository playerDataRepository;
    private final GamePlayerRepository gamePlayerRepository;
    private final TournamentRepository tournamentRepository;

    public MatchService(
        MatchRepository matchRepository,
        MatchMapper matchMapper,
        TeamRepository teamRepository,
        PlayerDataRepository playerDataRepository,
        GamePlayerRepository gamePlayerRepository,
        TournamentRepository tournamentRepository
    ) {
        this.matchRepository = matchRepository;
        this.matchMapper = matchMapper;
        this.teamRepository = teamRepository;
        this.playerDataRepository = playerDataRepository;
        this.gamePlayerRepository = gamePlayerRepository;
        this.tournamentRepository = tournamentRepository;
    }

    /**
     * Save a match.
     *
     * @param matchDTO the entity to save.
     * @return the persisted entity.
     */
    public MatchDTO save(MatchDTO matchDTO) {
        log.debug("Request to save Match : {}", matchDTO);
        Match match = matchMapper.toEntity(matchDTO);
        match = matchRepository.save(match);
        return matchMapper.toDto(match);
    }

    /**
     * Update a match.
     *
     * @param matchDTO the entity to save.
     * @return the persisted entity.
     */
    public MatchDTO update(MatchDTO matchDTO) {
        log.debug("Request to update Match : {}", matchDTO);
        Match match = matchMapper.toEntity(matchDTO);
        match = matchRepository.save(match);
        return matchMapper.toDto(match);
    }

    /**
     * Partially update a match.
     *
     * @param matchDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MatchDTO> partialUpdate(MatchDTO matchDTO) {
        log.debug("Request to partially update Match : {}", matchDTO);

        return matchRepository
            .findById(matchDTO.getId())
            .map(existingMatch -> {
                matchMapper.partialUpdate(existingMatch, matchDTO);

                return existingMatch;
            })
            .map(matchRepository::save)
            .map(matchMapper::toDto);
    }

    /**
     * Get all the matches.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MatchDTO> findAll() {
        log.debug("Request to get all Matches");
        return matchRepository.findAll().stream().map(matchMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Transactional(readOnly = true)
    public List<MatchDTO> findAllByTeamId(long teamId) {
        log.debug("Request to get all Matches by team id {}", teamId);
        List<MatchDTO> matches = new LinkedList<>();
        teamRepository
            .findOneById(teamId)
            .ifPresent(team -> {
                matchRepository.findAllByTeamOneOrTeamTwo(team, team).stream().map(matchMapper::toDto).forEach(matches::add);
            });

        return matches;
    }

    @Transactional(readOnly = true)
    public List<MatchDTO> findAllVictoryByTeamId(long teamId) {
        log.debug("Request to get all victory Matches by team id {}", teamId);
        List<MatchDTO> matches = new LinkedList<>();
        teamRepository
            .findOneById(teamId)
            .ifPresent(team -> {
                matchRepository
                    .findAllByTeamOneOrTeamTwo(team, team)
                    .stream()
                    .filter(match -> {
                        if (!match.getScoreSubmitted()) {
                            return false;
                        } // check match score submitted
                        Long winnerTeamId = (match.getOneScore() > match.getTwoScore())
                            ? match.getTeamOne().getId()
                            : match.getTeamTwo().getId();
                        return winnerTeamId == teamId;
                    })
                    .map(matchMapper::toDto)
                    .forEach(matches::add);
            });

        return matches;
    }

    @Transactional(readOnly = true)
    public List<MatchDTO> findAllByPlayerId(long playerId) {
        log.debug("Request to get all Matches by player id {}", playerId);
        List<MatchDTO> matches = new LinkedList<>();
        playerDataRepository
            .findOneById(playerId)
            .ifPresent(playerData -> {
                gamePlayerRepository
                    .findAllByPlayerData(playerData)
                    .stream()
                    .map(gamePlayer -> gamePlayer.getGame())
                    .forEach(game -> {
                        Match match = game.getMatch();
                        if (!matches.contains(matchMapper.toDto(match))) {
                            matches.add(matchMapper.toDto(match));
                        }
                    });
            });

        return matches;
    }

    @Transactional(readOnly = true)
    public List<MatchDTO> findFinalThreeByTournamentId(Long id) {
        return matchRepository
            .findFinalThreeMatchesByTournamentId(id)
            .stream()
            .map(matchMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Transactional(readOnly = true)
    public Optional<MatchDTO> findFinalByTournamentId(Long id) {
        return matchRepository.findFinalMatchByTournamentId(id).map(matchMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<MatchDTO> findAllByTournamentId(long tournamentId) {
        log.debug("Request to get all Matches by tournament id {}", tournamentId);
        List<MatchDTO> matches = new LinkedList<>();
        tournamentRepository
            .findOneById(tournamentId)
            .ifPresent(tournament -> {
                List<Match> matchlist = matchRepository.findAllByTournament(tournament);
                matchlist
                    .stream()
                    .map(matchMapper::toDto)
                    .forEach(matchDto -> {
                        matches.add(matchDto);
                    });
            });
        return matches;
    }

    /**
     * Get one match by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MatchDTO> findOne(Long id) {
        log.debug("Request to get Match : {}", id);
        return matchRepository.findById(id).map(matchMapper::toDto);
    }

    /**
     * Delete the match by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Match : {}", id);
        matchRepository.deleteById(id);
    }
}
