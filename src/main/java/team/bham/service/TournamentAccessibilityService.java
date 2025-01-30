package team.bham.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.bham.domain.Tournament;
import team.bham.domain.TournamentAccessibility;
import team.bham.repository.TournamentAccessibilityRepository;
import team.bham.repository.TournamentRepository;
import team.bham.service.dto.TournamentAccessibilityDTO;
import team.bham.service.mapper.TournamentAccessibilityMapper;
import team.bham.web.rest.vm.AccessibilityVM;

/**
 * Service Implementation for managing {@link TournamentAccessibility}.
 */
@Service
@Transactional
public class TournamentAccessibilityService {

    private final Logger log = LoggerFactory.getLogger(TournamentAccessibilityService.class);

    private final TournamentAccessibilityRepository tournamentAccessibilityRepository;
    private final TournamentRepository tournamentRepository;
    private final TournamentAccessibilityMapper tournamentAccessibilityMapper;

    public TournamentAccessibilityService(
        TournamentAccessibilityRepository tournamentAccessibilityRepository,
        TournamentAccessibilityMapper tournamentAccessibilityMapper,
        TournamentRepository tournamentRepository
    ) {
        this.tournamentAccessibilityRepository = tournamentAccessibilityRepository;
        this.tournamentAccessibilityMapper = tournamentAccessibilityMapper;
        this.tournamentRepository = tournamentRepository;
    }

    // Create a tournament
    @Transactional
    public TournamentAccessibilityDTO create(AccessibilityVM accessibilityVM) {
        log.debug("check");
        TournamentAccessibility newTournamentAccessibility = new TournamentAccessibility();
        Tournament currentTournament = accessibilityVM.getTournament();

        newTournamentAccessibility.setTournament(accessibilityVM.getTournament());
        newTournamentAccessibility.setAccessibility(accessibilityVM.getAccessibility());

        tournamentAccessibilityRepository.save(newTournamentAccessibility);
        currentTournament.addTournamentAccessibility(newTournamentAccessibility);
        tournamentRepository.save(currentTournament);

        return tournamentAccessibilityMapper.toDto(newTournamentAccessibility);
    }

    /**
     * Save a tournamentAccessibility.
     *
     * @param tournamentAccessibilityDTO the entity to save.
     * @return the persisted entity.
     */
    public TournamentAccessibilityDTO save(TournamentAccessibilityDTO tournamentAccessibilityDTO) {
        log.debug("Request to save TournamentAccessibility : {}", tournamentAccessibilityDTO);
        TournamentAccessibility tournamentAccessibility = tournamentAccessibilityMapper.toEntity(tournamentAccessibilityDTO);
        tournamentAccessibility = tournamentAccessibilityRepository.save(tournamentAccessibility);
        return tournamentAccessibilityMapper.toDto(tournamentAccessibility);
    }

    /**
     * Update a tournamentAccessibility.
     *
     * @param tournamentAccessibilityDTO the entity to save.
     * @return the persisted entity.
     */
    public TournamentAccessibilityDTO update(TournamentAccessibilityDTO tournamentAccessibilityDTO) {
        log.debug("Request to update TournamentAccessibility : {}", tournamentAccessibilityDTO);
        TournamentAccessibility tournamentAccessibility = tournamentAccessibilityMapper.toEntity(tournamentAccessibilityDTO);
        tournamentAccessibility = tournamentAccessibilityRepository.save(tournamentAccessibility);
        return tournamentAccessibilityMapper.toDto(tournamentAccessibility);
    }

    /**
     * Partially update a tournamentAccessibility.
     *
     * @param tournamentAccessibilityDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TournamentAccessibilityDTO> partialUpdate(TournamentAccessibilityDTO tournamentAccessibilityDTO) {
        log.debug("Request to partially update TournamentAccessibility : {}", tournamentAccessibilityDTO);

        return tournamentAccessibilityRepository
            .findById(tournamentAccessibilityDTO.getId())
            .map(existingTournamentAccessibility -> {
                tournamentAccessibilityMapper.partialUpdate(existingTournamentAccessibility, tournamentAccessibilityDTO);

                return existingTournamentAccessibility;
            })
            .map(tournamentAccessibilityRepository::save)
            .map(tournamentAccessibilityMapper::toDto);
    }

    /**
     * Get all the tournamentAccessibilities.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TournamentAccessibilityDTO> findAll() {
        log.debug("Request to get all TournamentAccessibilities");
        return tournamentAccessibilityRepository
            .findAll()
            .stream()
            .map(tournamentAccessibilityMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one tournamentAccessibility by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TournamentAccessibilityDTO> findOne(Long id) {
        log.debug("Request to get TournamentAccessibility : {}", id);
        return tournamentAccessibilityRepository.findById(id).map(tournamentAccessibilityMapper::toDto);
    }

    /**
     * Delete the tournamentAccessibility by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete TournamentAccessibility : {}", id);
        tournamentAccessibilityRepository.deleteById(id);
    }
}
