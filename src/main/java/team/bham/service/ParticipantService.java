package team.bham.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.bham.domain.Participant;
import team.bham.domain.enumeration.AccessStatus;
import team.bham.repository.ParticipantRepository;
import team.bham.repository.TeamRepository;
import team.bham.repository.TournamentRepository;
import team.bham.service.dto.ParticipantDTO;
import team.bham.service.mapper.ParticipantMapper;

/**
 * Service Implementation for managing {@link Participant}.
 */
@Service
@Transactional
public class ParticipantService {

    private final Logger log = LoggerFactory.getLogger(ParticipantService.class);

    private final ParticipantMapper participantMapper;
    private final ParticipantRepository participantRepository;
    private final TournamentService tournamentService;
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;

    public ParticipantService(
        ParticipantMapper participantMapper,
        ParticipantRepository participantRepository,
        TournamentService tournamentService,
        TournamentRepository tournamentRepository,
        TeamRepository teamRepository
    ) {
        this.participantRepository = participantRepository;
        this.participantMapper = participantMapper;
        this.tournamentRepository = tournamentRepository;
        this.teamRepository = teamRepository;
        this.tournamentService = tournamentService;
    }

    /**
     * Save a participant.
     *
     * @param participantDTO the entity to save.
     * @return the persisted entity.
     */
    public ParticipantDTO save(ParticipantDTO participantDTO) {
        log.debug("Request to save Participant : {}", participantDTO);
        Participant participant = participantMapper.toEntity(participantDTO);
        participant = participantRepository.save(participant);
        return participantMapper.toDto(participant);
    }

    /**
     * Update a participant.
     *
     * @param participantDTO the entity to save.
     * @return the persisted entity.
     */
    public ParticipantDTO update(ParticipantDTO participantDTO) {
        log.debug("Request to update Participant : {}", participantDTO);
        Participant participant = participantMapper.toEntity(participantDTO);
        participant = participantRepository.save(participant);
        return participantMapper.toDto(participant);
    }

    /**
     * Partially update a participant.
     *
     * @param participantDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ParticipantDTO> partialUpdate(ParticipantDTO participantDTO) {
        log.debug("Request to partially update Participant : {}", participantDTO);

        return participantRepository
            .findById(participantDTO.getId())
            .map(existingParticipant -> {
                participantMapper.partialUpdate(existingParticipant, participantDTO);

                return existingParticipant;
            })
            .map(participantRepository::save)
            .map(participantMapper::toDto);
    }

    /**
     * Get all the participants.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ParticipantDTO> findAll() {
        log.debug("Request to get all Participants");
        return participantRepository.findAll().stream().map(participantMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one participant by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ParticipantDTO> findOne(Long id) {
        log.debug("Request to get Participant : {}", id);
        return participantRepository.findById(id).map(participantMapper::toDto);
    }

    /**
     * Get one participant by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public List<ParticipantDTO> findParticipantsByTournamentId(Long id) {
        log.debug("Request to get Participants by Tournament ID : {}", id);
        return participantRepository
            .findAllByTournamentId(id)
            .stream()
            .map(participantMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Delete the participant by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Participant : {}", id);
        participantRepository.deleteById(id);
    }

    @Transactional
    public void join(Long tournamentId, Long teamId, int signUpRank) {
        Participant newParticipant = new Participant();

        this.tournamentRepository.findOneById(tournamentId)
            .ifPresentOrElse(
                tournament -> {
                    if (tournament.getAccessStatus() == AccessStatus.CLOSED) {
                        throw new TournamentClosedException();
                    }

                    newParticipant.setTournament(tournament);
                },
                () -> {
                    throw new TournamentDoesNotExistException();
                }
            );

        this.teamRepository.findOneById(teamId)
            .ifPresentOrElse(
                team -> {
                    newParticipant.setTeam(team);
                },
                () -> {
                    throw new TeamDoesNotExistException();
                }
            );

        newParticipant.setSignUpRank(signUpRank);
        participantRepository.save(newParticipant);
        log.debug(
            "Added team {} as participant for tournament {}",
            newParticipant.getTeam().getName(),
            newParticipant.getTournament().getName()
        );
        this.tournamentService.reconcileTournamentMatches(tournamentId);
    }

    @Transactional
    public void leave(Long tournamentId, Long teamId, Long participantId) {
        this.findOne(participantId)
            .ifPresentOrElse(
                participant -> {
                    int signUpRank = participant.getSignUpRank();

                    List<Participant> participants = this.participantRepository.findAllByTournamentId(tournamentId);

                    for (Participant p : participants) {
                        if (p.getSignUpRank() > signUpRank) {
                            p.setSignUpRank(p.getSignUpRank() - 1);
                            this.update(participantMapper.toDto(p));
                        }
                    }
                },
                () -> {
                    throw new ParticipantDoesNotExistException();
                }
            );

        this.delete(participantId);
        log.debug("Deleted participant from tournament {}", tournamentId);
        this.tournamentService.reconcileTournamentMatches(tournamentId);
    }
}
