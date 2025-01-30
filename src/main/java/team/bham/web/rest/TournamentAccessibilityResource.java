package team.bham.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
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
import team.bham.repository.TournamentAccessibilityRepository;
import team.bham.security.AuthoritiesConstants;
import team.bham.security.SecurityUtils;
import team.bham.service.TournamentAccessibilityService;
import team.bham.service.dto.TournamentAccessibilityDTO;
import team.bham.service.dto.TournamentDTO;
import team.bham.web.rest.errors.BadRequestAlertException;
import team.bham.web.rest.vm.AccessibilityVM;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.TournamentAccessibility}.
 */
@RestController
@RequestMapping("/api")
public class TournamentAccessibilityResource {

    private static class TournamentAccessibilityResourceException extends RuntimeException {

        private TournamentAccessibilityResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(TournamentAccessibilityResource.class);

    private static final String ENTITY_NAME = "tournamentAccessibility";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TournamentAccessibilityService tournamentAccessibilityService;

    private final TournamentAccessibilityRepository tournamentAccessibilityRepository;

    public TournamentAccessibilityResource(
        TournamentAccessibilityService tournamentAccessibilityService,
        TournamentAccessibilityRepository tournamentAccessibilityRepository
    ) {
        this.tournamentAccessibilityService = tournamentAccessibilityService;
        this.tournamentAccessibilityRepository = tournamentAccessibilityRepository;
    }

    /**
     * {@code POST  /tournament-accessibilities} : Create a new tournamentAccessibility.
     *
     * @param accessibilityVM the accessibilityVM to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tournamentAccessibilityDTO, or with status {@code 400 (Bad Request)} if the tournamentAccessibility has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tournament-accessibilities")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<TournamentAccessibilityDTO> createTournamentAccessibility(@Valid @RequestBody AccessibilityVM accessibilityVM)
        throws URISyntaxException {
        log.debug("hello");
        log.debug("REST request to save TournamentAccessibility : {}", accessibilityVM);
        if (accessibilityVM.getId() != null) {
            throw new BadRequestAlertException("A new tournamentAccessibility cannot already have an ID", ENTITY_NAME, "idexists");
        }

        String userLogin = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new TournamentAccessibilityResourceException("Current user login not found"));

        TournamentAccessibilityDTO newAccessibilityDTO = tournamentAccessibilityService.create(accessibilityVM);
        log.debug(userLogin, newAccessibilityDTO);

        return ResponseEntity
            .created(new URI("/api/tournament-accessibilities/" + newAccessibilityDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, newAccessibilityDTO.getId().toString()))
            .body(newAccessibilityDTO);
    }

    /**
     * {@code PUT  /tournament-accessibilities/:id} : Updates an existing tournamentAccessibility.
     *
     * @param id the id of the tournamentAccessibilityDTO to save.
     * @param tournamentAccessibilityDTO the tournamentAccessibilityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tournamentAccessibilityDTO,
     * or with status {@code 400 (Bad Request)} if the tournamentAccessibilityDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tournamentAccessibilityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tournament-accessibilities/{id}")
    public ResponseEntity<TournamentAccessibilityDTO> updateTournamentAccessibility(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TournamentAccessibilityDTO tournamentAccessibilityDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TournamentAccessibility : {}, {}", id, tournamentAccessibilityDTO);
        if (tournamentAccessibilityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tournamentAccessibilityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tournamentAccessibilityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TournamentAccessibilityDTO result = tournamentAccessibilityService.update(tournamentAccessibilityDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tournamentAccessibilityDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /tournament-accessibilities/:id} : Partial updates given fields of an existing tournamentAccessibility, field will ignore if it is null
     *
     * @param id the id of the tournamentAccessibilityDTO to save.
     * @param tournamentAccessibilityDTO the tournamentAccessibilityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tournamentAccessibilityDTO,
     * or with status {@code 400 (Bad Request)} if the tournamentAccessibilityDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tournamentAccessibilityDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tournamentAccessibilityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tournament-accessibilities/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TournamentAccessibilityDTO> partialUpdateTournamentAccessibility(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TournamentAccessibilityDTO tournamentAccessibilityDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TournamentAccessibility partially : {}, {}", id, tournamentAccessibilityDTO);
        if (tournamentAccessibilityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tournamentAccessibilityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tournamentAccessibilityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TournamentAccessibilityDTO> result = tournamentAccessibilityService.partialUpdate(tournamentAccessibilityDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tournamentAccessibilityDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /tournament-accessibilities} : get all the tournamentAccessibilities.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tournamentAccessibilities in body.
     */
    @GetMapping("/tournament-accessibilities")
    public List<TournamentAccessibilityDTO> getAllTournamentAccessibilities() {
        log.debug("REST request to get all TournamentAccessibilities");
        return tournamentAccessibilityService.findAll();
    }

    /**
     * {@code GET  /tournament-accessibilities/:id} : get the "id" tournamentAccessibility.
     *
     * @param id the id of the tournamentAccessibilityDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tournamentAccessibilityDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tournament-accessibilities/{id}")
    public ResponseEntity<TournamentAccessibilityDTO> getTournamentAccessibility(@PathVariable Long id) {
        log.debug("REST request to get TournamentAccessibility : {}", id);
        Optional<TournamentAccessibilityDTO> tournamentAccessibilityDTO = tournamentAccessibilityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tournamentAccessibilityDTO);
    }

    /**
     * {@code DELETE  /tournament-accessibilities/:id} : delete the "id" tournamentAccessibility.
     *
     * @param id the id of the tournamentAccessibilityDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tournament-accessibilities/{id}")
    public ResponseEntity<Void> deleteTournamentAccessibility(@PathVariable Long id) {
        log.debug("REST request to delete TournamentAccessibility : {}", id);
        tournamentAccessibilityService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
