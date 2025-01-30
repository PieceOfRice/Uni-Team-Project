package team.bham.service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.bham.domain.Match;
import team.bham.domain.Participant;
import team.bham.domain.Team;
import team.bham.domain.Tournament;
import team.bham.repository.MatchRepository;
import team.bham.repository.ParticipantRepository;
import team.bham.repository.PlayerDataRepository;
import team.bham.repository.TournamentRepository;
import team.bham.repository.UserRepository;
import team.bham.security.SecurityUtils;
import team.bham.service.dto.MatchDTO;
import team.bham.service.dto.TournamentDTO;
import team.bham.service.mapper.MatchMapper;
import team.bham.service.mapper.TeamMapper;
import team.bham.service.mapper.TournamentMapper;

/**
 * Service Implementation for managing {@link Tournament}.
 */
@Service
@Transactional
public class TournamentService {

    private final Logger log = LoggerFactory.getLogger(TournamentService.class);

    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;
    private final PlayerDataRepository playerDataRepository;
    private final TournamentMapper tournamentMapper;
    private final MatchRepository matchRepository;
    private final MatchService matchService;
    private final MatchMapper matchMapper;
    private final ParticipantRepository participantRepository;
    private final TeamMapper teamMapper;

    public TournamentService(
        TournamentRepository tournamentRepository,
        TournamentMapper tournamentMapper,
        UserRepository userRepository,
        PlayerDataRepository playerDataRepository,
        MatchRepository matchRepository,
        MatchService matchService,
        ParticipantRepository participantRepository,
        TeamMapper teamMapper,
        MatchMapper matchMapper
    ) {
        this.tournamentRepository = tournamentRepository;
        this.userRepository = userRepository;
        this.playerDataRepository = playerDataRepository;
        this.matchRepository = matchRepository;
        this.matchService = matchService;
        this.matchMapper = matchMapper;
        this.tournamentMapper = tournamentMapper;
        this.participantRepository = participantRepository;
        this.teamMapper = teamMapper;
    }

    private Integer bracketsPerParticipant(Integer participantCount) {
        if (participantCount < 2) {
            return participantCount;
        }
        return participantCount - 1;
    }

    public Integer matchIndexToBracketIndex(Integer matchIndex, Integer participantCount) {
        return this.bracketsPerParticipant(participantCount) + 1 - matchIndex;
    }

    private Integer bracketIndexToMatchIndex(Integer bracketIndex, Integer participantCount) {
        return this.bracketsPerParticipant(participantCount) + 1 - bracketIndex;
    }

    private Match findMatchWithMatchIndex(List<Match> matches, Integer matchIndex) {
        for (Match match : matches) {
            if (match.getMatchIndex() == matchIndex) {
                return match;
            }
        }
        return null;
    }

    private Participant findParticipantWithSignUpRank(List<Participant> participants, Integer signUpRank) {
        for (Participant participant : participants) {
            if (participant.getSignUpRank() == signUpRank) {
                return participant;
            }
        }
        return null;
    }

    // Re-evaluates all the matches of a given tournament and creates, updates or
    // deletes matches to be equivalent to what matches SHOULD exist.
    // Should be called from the tournament join, leave and score submission hooks
    public void reconcileTournamentMatches(Long tournamentId) {
        log.debug("Running match reconciler for tournament {}", tournamentId);
        tournamentRepository
            .findById(tournamentId)
            .ifPresent(tournament -> {
                List<Match> matches = this.matchRepository.findAllByTournament(tournament);
                List<Participant> participants = this.participantRepository.findAllByTournamentId(tournamentId);

                Integer numberOfParticipants = participants.size();

                if (tournament.getIsLive()) {
                    // once a tournament has begun, we must create any new matches for brackets that
                    // have completed (or add participants to it).

                    // as simple as checking every match, and checking if it should have a "parent"
                    // match
                    // (match has 2 participants, has scores submitted and approved, and isn't the
                    // final match)
                    // if it doesn't, create it
                    boolean needToCreateParentMatches = false;
                    HashMap<Integer, Integer> createdMatchIndices = new HashMap<>();
                    do {
                        needToCreateParentMatches = false;

                        // performing a "pass" over all the matches and checking if any parents need to
                        // be created or modified
                        for (Match match : matches) {
                            // ignoring matches that don't have approved scores, or are the final match
                            // (with bracket index 1)
                            Integer matchIndex = match.getMatchIndex();
                            Integer bracketIndex = this.matchIndexToBracketIndex(matchIndex, numberOfParticipants);
                            boolean shouldHaveParent = match.getScoreApproved() && bracketIndex != 1;

                            // we must also check if the tournament should be over
                            // final bracket has an approved score being the condition for it
                            if (bracketIndex == 1 && match.getScoreApproved()) {
                                tournament.setEnded(true);
                                tournament.setEndTime(ZonedDateTime.now());
                                tournamentRepository.save(tournament);
                                log.debug("Reconciler: tournament {} marked as ended", tournamentId);
                            }

                            if (shouldHaveParent) {
                                // now checking if said parent exists
                                Integer parentBracketIndex = Math.floorDiv(bracketIndex, 2);
                                Integer parentMatchIndex = this.bracketIndexToMatchIndex(parentBracketIndex, numberOfParticipants);

                                Match parentMatch = this.findMatchWithMatchIndex(matches, parentMatchIndex);
                                boolean doesNotHaveParent = parentMatch == null && createdMatchIndices.get(parentMatchIndex) == null;
                                Team winningTeam = (match.getOneScore() > match.getTwoScore()) ? match.getTeamOne() : match.getTeamTwo();
                                boolean teamOneIsWinner = Math.round((float) bracketIndex / 2) == parentBracketIndex;

                                if (doesNotHaveParent) {
                                    needToCreateParentMatches = true;
                                    createdMatchIndices.put(parentMatchIndex, parentMatchIndex);
                                    // creating a parent match if one doesn't exist and one hasn't already been
                                    // created
                                    Match newMatch = new Match();

                                    newMatch.setTournament(tournament);
                                    newMatch.setMatchIndex(parentMatchIndex);
                                    newMatch.setScoreSubmitted(false);
                                    newMatch.setScoreApproved(false);
                                    newMatch.setOneScore(0);
                                    newMatch.setTwoScore(0);
                                    newMatch.setStartTime(Instant.now());

                                    // team one should be "top" bracket, and this is always the one who's bracket
                                    // index is exactly equal to 2x its parent
                                    if (teamOneIsWinner) {
                                        newMatch.setTeamOne(winningTeam);
                                    } else {
                                        newMatch.setTeamTwo(winningTeam);
                                    }
                                    matchRepository.save(newMatch);
                                    log.debug("Reconciler: parent match {} created in tournament {}", parentMatchIndex, tournamentId);
                                } else {
                                    // parent match should have the winner of this match set as one of its members
                                    @SuppressWarnings("null") // since this'll never actually be null
                                    boolean parentMatchIsUpdated = (teamOneIsWinner)
                                        ? parentMatch.getTeamOne() == winningTeam
                                        : parentMatch.getTeamTwo() == winningTeam;

                                    if (!parentMatchIsUpdated) {
                                        MatchDTO matchModifications = new MatchDTO();
                                        matchModifications.setId(parentMatch.getId());
                                        matchModifications.setMatchIndex(parentMatchIndex);
                                        matchModifications.setScoreSubmitted(false);
                                        matchModifications.setScoreApproved(false);

                                        if (teamOneIsWinner) {
                                            matchModifications.setTeamOne(this.teamMapper.toDto(winningTeam));
                                        } else {
                                            matchModifications.setTeamTwo(this.teamMapper.toDto(winningTeam));
                                        }

                                        this.matchService.partialUpdate(matchModifications);
                                        log.debug("Reconciler: parent match {} modified in tournament {}", parentMatchIndex, tournamentId);
                                    }
                                }
                            }
                        }

                        // if any matches were made created during this pass over, then update the list
                        // before rerunning
                        if (needToCreateParentMatches) {
                            matches = this.matchRepository.findAllByTournament(tournament);
                        }
                    } while (needToCreateParentMatches);
                } else {
                    // should ensure created matches are up to date with participant list
                    // counting participants
                    Integer actualParticipantCount = 0;
                    for (Match match : matches) {
                        if (match.getTeamOne() != null) {
                            actualParticipantCount++;
                        }
                        if (match.getTeamTwo() != null) {
                            actualParticipantCount++;
                        }
                    }

                    // remaking all the matches if the expected participant count doesn't match the
                    // actual
                    // one (i.e., participants have changed)
                    if (numberOfParticipants != actualParticipantCount) {
                        // deleting existing ones
                        matches.forEach(match -> {
                            matchService.delete(match.getId());
                        });

                        Integer participantIndex = 1;

                        // creating new ones
                        Integer expectedMatchCount = (int) Math.ceil(((float) numberOfParticipants) / 2);
                        for (int matchIndex = 1; matchIndex <= expectedMatchCount; matchIndex++) {
                            Match newMatch = new Match();

                            newMatch.setTournament(tournament);
                            newMatch.setMatchIndex(matchIndex);
                            newMatch.setScoreSubmitted(false);
                            newMatch.setScoreApproved(false);
                            newMatch.setStartTime(Instant.now());

                            // setting bottom first since the brackets fill bottom-up
                            if (participantIndex <= numberOfParticipants) {
                                Participant bottomParticipant = this.findParticipantWithSignUpRank(participants, participantIndex);
                                newMatch.setTeamTwo(bottomParticipant.getTeam());
                                newMatch.setTwoScore(0);
                                participantIndex++;
                            }
                            if (participantIndex <= numberOfParticipants) {
                                Participant topParticipant = this.findParticipantWithSignUpRank(participants, participantIndex);
                                newMatch.setTeamOne(topParticipant.getTeam());
                                newMatch.setOneScore(0);
                                participantIndex++;
                            }

                            this.matchService.save(this.matchMapper.toDto(newMatch));
                            log.debug(
                                "Reconciler: match {} created for {} participants in tournament {}",
                                matchIndex,
                                numberOfParticipants,
                                tournamentId
                            );
                        }
                    }
                }
                log.debug("Reconciled matches for tournament {}", tournamentId);
            });
    }

    /** Create a tournament */
    @Transactional
    public TournamentDTO create(TournamentDTO tournamentDTO) {
        Tournament newTournament = tournamentMapper.toEntity(tournamentDTO);

        SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .flatMap(playerDataRepository::findOneByUser)
            .ifPresentOrElse(
                playerData -> {
                    log.debug("Found player {}", playerData.toString());
                    newTournament.setCreator(playerData);
                    playerData.addCreator(newTournament);
                    playerDataRepository.save(playerData);
                    log.debug("Created tournament {} by {}", newTournament.getName(), playerData.getName());
                },
                () -> {
                    throw new UserMissingPlayerDataException();
                }
            );

        tournamentRepository.save(newTournament);
        return tournamentMapper.toDto(newTournament);
    }

    /**
     * Save a tournament.
     *
     * @param tournamentDTO the entity to save.
     * @return the persisted entity.
     */
    public TournamentDTO save(TournamentDTO tournamentDTO) {
        log.debug("Request to save Tournament : {}", tournamentDTO);
        Tournament tournament = tournamentMapper.toEntity(tournamentDTO);
        tournament = tournamentRepository.save(tournament);
        return tournamentMapper.toDto(tournament);
    }

    /**
     * Update a tournament.
     *
     * @param tournamentDTO the entity to save.
     * @return the persisted entity.
     */
    public TournamentDTO update(TournamentDTO tournamentDTO) {
        log.debug("Request to update Tournament : {}", tournamentDTO);
        Tournament tournament = tournamentMapper.toEntity(tournamentDTO);
        tournament = tournamentRepository.save(tournament);
        return tournamentMapper.toDto(tournament);
    }

    /**
     * Partially update a tournament.
     *
     * @param tournamentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TournamentDTO> partialUpdate(TournamentDTO tournamentDTO) {
        log.debug("Request to partially update Tournament : {}", tournamentDTO);

        return tournamentRepository
            .findById(tournamentDTO.getId())
            .map(existingTournament -> {
                tournamentMapper.partialUpdate(existingTournament, tournamentDTO);

                return existingTournament;
            })
            .map(tournamentRepository::save)
            .map(tournamentMapper::toDto);
    }

    /**
     * Get all the tournaments.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TournamentDTO> findAll() {
        log.debug("Request to get all Tournaments");
        return tournamentRepository.findAll().stream().map(tournamentMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one tournament by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TournamentDTO> findOne(Long id) {
        log.debug("Request to get Tournament : {}", id);
        return tournamentRepository.findById(id).map(tournamentMapper::toDto);
    }

    /**
     * Find the tournament by name.
     *
     * @param name the name of the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TournamentDTO> findOneByName(String name) {
        log.debug("Request to get Tournament : {}", name);
        return tournamentRepository.findOneByName(name).map(tournamentMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<TournamentDTO> findPopularTournaments() {
        int n = 10;
        List<TournamentDTO> tournaments = new LinkedList<>();
        tournamentRepository
            .findAll()
            .stream()
            .sorted((tournamentA, tournamentB) -> {
                int tournamentAPopulation = this.participantRepository.findAllByTournamentId(tournamentA.getId()).size();
                int tournamentBPopulation = this.participantRepository.findAllByTournamentId(tournamentB.getId()).size();
                return tournamentBPopulation - tournamentAPopulation;
            })
            .forEach(tournament -> {
                if (tournaments.size() < n) {
                    tournaments.add(tournamentMapper.toDto(tournament));
                }
            });
        return tournaments;
    }

    /**
     * Delete the tournament by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Tournament : {}", id);
        tournamentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Tournament> getTournamentByNameLike(String name) {
        String pattern = '%' + name + '%';
        return tournamentRepository.getTournamentByNameLike(pattern);
    }
}
