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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.bham.repository.MatchRepository;
import team.bham.service.MatchService;
import team.bham.service.TournamentService;
import team.bham.service.dto.MatchDTO;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.Match}.
 */
@RestController
@RequestMapping("/api")
public class MatchResource {

    private final Logger log = LoggerFactory.getLogger(MatchResource.class);

    private static final String ENTITY_NAME = "match";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MatchService matchService;
    private final MatchRepository matchRepository;
    private final TournamentService tournamentService;

    public MatchResource(MatchService matchService, MatchRepository matchRepository, TournamentService tournamentService) {
        this.matchService = matchService;
        this.matchRepository = matchRepository;
        this.tournamentService = tournamentService;
    }

    /**
     * {@code POST  /matches} : Create a new match.
     *
     * @param matchDTO the matchDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new matchDTO, or with status {@code 400 (Bad Request)} if
     *         the match has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/matches")
    public ResponseEntity<MatchDTO> createMatch(@Valid @RequestBody MatchDTO matchDTO) throws URISyntaxException {
        log.debug("REST request to save Match : {}", matchDTO);
        if (matchDTO.getId() != null) {
            throw new BadRequestAlertException("A new match cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MatchDTO result = matchService.save(matchDTO);
        return ResponseEntity
            .created(new URI("/api/matches/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /matches/:id} : Updates an existing match.
     *
     * @param id       the id of the matchDTO to save.
     * @param matchDTO the matchDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated matchDTO,
     *         or with status {@code 400 (Bad Request)} if the matchDTO is not
     *         valid,
     *         or with status {@code 500 (Internal Server Error)} if the matchDTO
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/matches/{id}")
    public ResponseEntity<MatchDTO> updateMatch(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MatchDTO matchDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Match : {}, {}", id, matchDTO);
        if (matchDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, matchDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!matchRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        MatchDTO result = matchService.update(matchDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, matchDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /matches/:id} : Partial updates given fields of an existing
     * match, field will ignore if it is null
     *
     * @param id       the id of the matchDTO to save.
     * @param matchDTO the matchDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated matchDTO,
     *         or with status {@code 400 (Bad Request)} if the matchDTO is not
     *         valid,
     *         or with status {@code 404 (Not Found)} if the matchDTO is not found,
     *         or with status {@code 500 (Internal Server Error)} if the matchDTO
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/matches/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MatchDTO> partialUpdateMatch(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MatchDTO matchDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Match partially : {}, {}", id, matchDTO);
        if (matchDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, matchDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!matchRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MatchDTO> oldMatch = matchService.findOne(matchDTO.getId());
        Optional<MatchDTO> result = matchService.partialUpdate(matchDTO);

        oldMatch.ifPresent(existingMatch -> {
            result.ifPresent(updatedMatchDTO -> {
                // score approvals trigger a match reconciliation for the tournament of the
                // match
                if (matchDTO.getScoreApproved() == true && existingMatch.getScoreApproved() != true) {
                    this.tournamentService.reconcileTournamentMatches(updatedMatchDTO.getTournament().getId());
                }
            });
        });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, matchDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /matches} : get all the matches.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of matches in body.
     */
    @GetMapping("/matches")
    public List<MatchDTO> getAllMatches() {
        log.debug("REST request to get all Matches");
        return matchService.findAll();
    }

    @GetMapping("/matches/search/tournament/{id}")
    public List<MatchDTO> getFinalThreeMatchesByTournament(@PathVariable Long id) {
        return matchService.findFinalThreeByTournamentId(id);
    }

    @GetMapping("/matches/search/tournament/final/{id}")
    public ResponseEntity<MatchDTO> getFinalMatchByTournament(@PathVariable Long id) {
        Optional<MatchDTO> matchDTO = matchService.findFinalByTournamentId(id);
        return ResponseUtil.wrapOrNotFound(matchDTO);
    }

    /**
     * {@code GET  /matches/:id} : get the "id" match.
     *
     * @param id the id of the matchDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the matchDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/matches/{id}")
    public ResponseEntity<MatchDTO> getMatch(@PathVariable Long id) {
        log.debug("REST request to get Match : {}", id);
        Optional<MatchDTO> matchDTO = matchService.findOne(id);
        return ResponseUtil.wrapOrNotFound(matchDTO);
    }

    @GetMapping("/matches/team")
    public List<MatchDTO> getMatchesByTeam(@RequestParam("teamId") Long teamId) {
        log.debug("REST request to get all Matches by team id {}", teamId);
        return matchService.findAllByTeamId(teamId);
    }

    @GetMapping("/matches/player")
    public List<MatchDTO> getMatchesByPlayer(@RequestParam("id") Long playerId) {
        log.debug("REST request to get all Matches by player id {}", playerId);
        return matchService.findAllByPlayerId(playerId);
    }

    @GetMapping("/matches/tournament")
    public List<MatchDTO> getMatchesByTournament(@RequestParam("id") Long tournamentId) {
        log.debug("REST request to get all Matches by tournament id {}", tournamentId);
        return matchService.findAllByTournamentId(tournamentId);
    }

    /**
     * {@code DELETE  /matches/:id} : delete the "id" match.
     *
     * @param id the id of the matchDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/matches/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id) {
        log.debug("REST request to delete Match : {}", id);
        matchService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
