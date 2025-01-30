package team.bham.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import team.bham.domain.Tournament;
import team.bham.repository.TournamentRepository;
import team.bham.security.AuthoritiesConstants;
import team.bham.security.SecurityUtils;
import team.bham.service.TournamentService;
import team.bham.service.dto.TournamentDTO;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.Tournament}.
 */
@RestController
@RequestMapping("/api")
public class TournamentResource {

    private static class TournamentResourceException extends RuntimeException {

        private TournamentResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(TournamentResource.class);

    private static final String ENTITY_NAME = "tournament";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TournamentService tournamentService;

    private final TournamentRepository tournamentRepository;

    public TournamentResource(TournamentService tournamentService, TournamentRepository tournamentRepository) {
        this.tournamentService = tournamentService;
        this.tournamentRepository = tournamentRepository;
    }

    /**
     * {@code POST  /tournaments} : Create a new tournament.
     *
     * @param tournamentDTO the tournamentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tournamentDTO, or with status {@code 400 (Bad Request)} if the tournament has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tournaments")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<TournamentDTO> createTournament(@Valid @RequestBody TournamentDTO tournamentDTO) throws URISyntaxException {
        log.debug("REST request to save Tournament : {}", tournamentDTO);
        if (tournamentDTO.getId() != null) {
            throw new BadRequestAlertException("A new tournament cannot already have an ID", ENTITY_NAME, "idexists");
        }

        String userLogin = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new TournamentResourceException("Current user login not found"));
        TournamentDTO newTournament = tournamentService.create(tournamentDTO);

        return ResponseEntity
            .created(new URI("/api/tournaments/" + newTournament.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, newTournament.getId().toString()))
            .body(newTournament);
    }

    /**
     * {@code PUT  /tournaments/:id} : Updates an existing tournament.
     *
     * @param id the id of the tournamentDTO to save.
     * @param tournamentDTO the tournamentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tournamentDTO,
     * or with status {@code 400 (Bad Request)} if the tournamentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tournamentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tournaments/{id}")
    public ResponseEntity<TournamentDTO> updateTournament(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TournamentDTO tournamentDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Tournament : {}, {}", id, tournamentDTO);
        if (tournamentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tournamentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tournamentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TournamentDTO result = tournamentService.update(tournamentDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tournamentDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /tournaments/:id} : Partial updates given fields of an existing tournament, field will ignore if it is null
     *
     * @param id the id of the tournamentDTO to save.
     * @param tournamentDTO the tournamentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tournamentDTO,
     * or with status {@code 400 (Bad Request)} if the tournamentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tournamentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tournamentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tournaments/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TournamentDTO> partialUpdateTournament(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TournamentDTO tournamentDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Tournament partially : {}, {}", id, tournamentDTO);
        if (tournamentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tournamentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tournamentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TournamentDTO> result = tournamentService.partialUpdate(tournamentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tournamentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /tournaments} : get all the tournaments.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tournaments in body.
     */
    @GetMapping("/tournaments")
    public List<TournamentDTO> getAllTournaments(@RequestParam(required = false, value = "name") String name) {
        if (name == null) {
            log.debug("REST request to get all Tournaments");
            return tournamentService.findAll();
        } else {
            log.debug("REST request to get Tournament : {}", name);
            Optional<TournamentDTO> tournamentDTO = tournamentService.findOneByName(name);
            return tournamentDTO.map(List::of).orElseGet(Collections::emptyList);
        }
    }

    /**
     * {@code GET  /tournaments/:id} : get the "id" tournament.
     *
     * @param id the id of the tournamentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tournamentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tournaments/{id}")
    public ResponseEntity<TournamentDTO> getTournament(@PathVariable Long id) {
        log.debug("REST request to get Tournament : {}", id);
        Optional<TournamentDTO> tournamentDTO = tournamentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tournamentDTO);
    }

    @GetMapping("/tournaments/popular")
    public List<TournamentDTO> findPopularTournaments() {
        log.debug("REST request to get popular Tournaments");
        return tournamentService.findPopularTournaments();
    }

    /**
     * {@code DELETE  /tournaments/:id} : delete the "id" tournament.
     *
     * @param id the id of the tournamentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tournaments/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable Long id) {
        log.debug("REST request to delete Tournament : {}", id);
        tournamentService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("tournaments/search-like/{name}")
    public List<Tournament> getTournamnetByNameLike(@PathVariable(value = "name") String name) {
        List<Tournament> tourns = tournamentService.getTournamentByNameLike(name);
        return tourns;
    }
}
