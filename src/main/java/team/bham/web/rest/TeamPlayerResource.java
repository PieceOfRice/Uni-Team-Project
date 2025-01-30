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
import team.bham.domain.TeamPlayer;
import team.bham.repository.TeamPlayerRepository;
import team.bham.service.TeamPlayerService;
import team.bham.service.dto.TeamPlayerDTO;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.TeamPlayer}.
 */
@RestController
@RequestMapping("/api")
public class TeamPlayerResource {

    private final Logger log = LoggerFactory.getLogger(TeamPlayerResource.class);

    private static final String ENTITY_NAME = "teamPlayer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TeamPlayerService teamPlayerService;

    private final TeamPlayerRepository teamPlayerRepository;

    public TeamPlayerResource(TeamPlayerService teamPlayerService, TeamPlayerRepository teamPlayerRepository) {
        this.teamPlayerService = teamPlayerService;
        this.teamPlayerRepository = teamPlayerRepository;
    }

    /**
     * {@code POST  /team-players} : Create a new teamPlayer.
     *
     * @param teamPlayerDTO the teamPlayerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new teamPlayerDTO, or with status {@code 400 (Bad Request)}
     *         if the teamPlayer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/team-players")
    public ResponseEntity<TeamPlayerDTO> createTeamPlayer(@Valid @RequestBody TeamPlayerDTO teamPlayerDTO) throws URISyntaxException {
        log.debug("REST request to save TeamPlayer : {}", teamPlayerDTO);
        if (teamPlayerDTO.getId() != null) {
            throw new BadRequestAlertException("A new teamPlayer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TeamPlayerDTO result = teamPlayerService.save(teamPlayerDTO);
        return ResponseEntity
            .created(new URI("/api/team-players/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /team-players/:id} : Updates an existing teamPlayer.
     *
     * @param id            the id of the teamPlayerDTO to save.
     * @param teamPlayerDTO the teamPlayerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated teamPlayerDTO,
     *         or with status {@code 400 (Bad Request)} if the teamPlayerDTO is not
     *         valid,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         teamPlayerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/team-players/{id}")
    public ResponseEntity<TeamPlayerDTO> updateTeamPlayer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TeamPlayerDTO teamPlayerDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TeamPlayer : {}, {}", id, teamPlayerDTO);
        if (teamPlayerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teamPlayerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!teamPlayerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TeamPlayerDTO result = teamPlayerService.update(teamPlayerDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, teamPlayerDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /team-players/:id} : Partial updates given fields of an
     * existing teamPlayer, field will ignore if it is null
     *
     * @param id            the id of the teamPlayerDTO to save.
     * @param teamPlayerDTO the teamPlayerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated teamPlayerDTO,
     *         or with status {@code 400 (Bad Request)} if the teamPlayerDTO is not
     *         valid,
     *         or with status {@code 404 (Not Found)} if the teamPlayerDTO is not
     *         found,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         teamPlayerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/team-players/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TeamPlayerDTO> partialUpdateTeamPlayer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TeamPlayerDTO teamPlayerDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TeamPlayer partially : {}, {}", id, teamPlayerDTO);
        if (teamPlayerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teamPlayerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!teamPlayerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TeamPlayerDTO> result = teamPlayerService.partialUpdate(teamPlayerDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, teamPlayerDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /team-players} : get all the teamPlayers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of teamPlayers in body.
     */
    @GetMapping("/team-players")
    public List<TeamPlayerDTO> getAllTeamPlayers() {
        log.debug("REST request to get all TeamPlayers");
        return teamPlayerService.findAll();
    }

    /**
     * {@code GET  /team-players/team/:id} : get all teamPlayer in the "id" team.
     *
     * @param id the id of the team of the teamPlayerDTOs to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of teamPlayers in body.
     */
    @GetMapping("/team-players/team/{id}")
    public List<TeamPlayerDTO> getPlayersInTeam(@PathVariable Long id) {
        log.debug("REST request to get TeamPlayers in Team : {}", id);
        return teamPlayerService.findPlayersInTeamWithPlayer(id);
    }

    @GetMapping("team-players/player/{id}")
    public ResponseEntity<TeamPlayerDTO> getTeamPlayerByPlayerId(@PathVariable Long id) {
        Optional<TeamPlayerDTO> teamPlayerDTO = teamPlayerService.findTeamPlayerByPlayerId(id);
        return ResponseUtil.wrapOrNotFound(teamPlayerDTO);
    }

    /**
     * {@code GET  /team-players/:id} : get the "id" teamPlayer.
     *
     * @param id the id of the teamPlayerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the teamPlayerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/team-players/{id}")
    public ResponseEntity<TeamPlayerDTO> getTeamPlayer(@PathVariable Long id) {
        log.debug("REST request to get TeamPlayer : {}", id);
        Optional<TeamPlayerDTO> teamPlayerDTO = teamPlayerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(teamPlayerDTO);
    }

    /**
     * {@code GET  /team-players/:pdid/:teamid} : get the teamPlayer with pdid and
     * teamid.
     *
     * @param id the id of the teamPlayerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the teamPlayerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/team-players/{pdid}/{teamid}")
    public ResponseEntity<TeamPlayerDTO> getTeamPlayerByPlayerDataIdAndTeamId(@PathVariable Long pdid, @PathVariable Long teamid) {
        log.debug("REST request to get TeamPlayer By PlayerDataID and TeamID : {}, {}", pdid, teamid);
        Optional<TeamPlayerDTO> teamPlayerDTO = teamPlayerService.findOneByPlayerDataIdAndTeamId(pdid, teamid);
        return ResponseUtil.wrapOrNotFound(teamPlayerDTO);
    }

    /**
     * {@code GET  /team-players/player/:pdid} : get the teamPlayer with pdid
     *
     * @param id the id of the teamPlayerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the teamPlayerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/team-players/leader/{pdid}")
    public List<TeamPlayerDTO> findLeader(@PathVariable Long pdid) {
        log.debug("REST request to get TeamPlayer leader By PlayerDataID: {}", pdid);
        List<TeamPlayerDTO> teamPlayerDTOs = teamPlayerService.findLeader(pdid);
        return teamPlayerDTOs;
    }

    /**
     * {@code DELETE  /team-players/:id} : delete the "id" teamPlayer.
     *
     * @param id the id of the teamPlayerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/team-players/{id}")
    public ResponseEntity<Void> deleteTeamPlayer(@PathVariable Long id) {
        log.debug("REST request to delete TeamPlayer : {}", id);
        teamPlayerService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
