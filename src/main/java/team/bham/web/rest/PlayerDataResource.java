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
import team.bham.domain.PlayerData;
import team.bham.repository.PlayerDataRepository;
import team.bham.service.PlayerDataService;
import team.bham.service.dto.PlayerDataDTO;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.PlayerData}.
 */
@RestController
@RequestMapping("/api")
public class PlayerDataResource {

    private final Logger log = LoggerFactory.getLogger(PlayerDataResource.class);

    private static final String ENTITY_NAME = "playerData";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PlayerDataService playerDataService;

    private final PlayerDataRepository playerDataRepository;

    public PlayerDataResource(PlayerDataService playerDataService, PlayerDataRepository playerDataRepository) {
        this.playerDataService = playerDataService;
        this.playerDataRepository = playerDataRepository;
    }

    /**
     * {@code POST  /player-data} : Create a new playerData.
     *
     * @param playerDataDTO the playerDataDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new playerDataDTO, or with status {@code 400 (Bad Request)} if the playerData has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/player-data")
    public ResponseEntity<PlayerDataDTO> createPlayerData(@Valid @RequestBody PlayerDataDTO playerDataDTO) throws URISyntaxException {
        log.debug("REST request to save PlayerData : {}", playerDataDTO);
        if (playerDataDTO.getId() != null) {
            throw new BadRequestAlertException("A new playerData cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PlayerDataDTO result = playerDataService.save(playerDataDTO);
        return ResponseEntity
            .created(new URI("/api/player-data/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /player-data/:id} : Updates an existing playerData.
     *
     * @param id the id of the playerDataDTO to save.
     * @param playerDataDTO the playerDataDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated playerDataDTO,
     * or with status {@code 400 (Bad Request)} if the playerDataDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the playerDataDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/player-data/{id}")
    public ResponseEntity<PlayerDataDTO> updatePlayerData(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PlayerDataDTO playerDataDTO
    ) throws URISyntaxException {
        log.debug("REST request to update PlayerData : {}, {}", id, playerDataDTO);
        if (playerDataDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, playerDataDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!playerDataRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PlayerDataDTO result = playerDataService.update(playerDataDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, playerDataDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /player-data/:id} : Partial updates given fields of an existing playerData, field will ignore if it is null
     *
     * @param id the id of the playerDataDTO to save.
     * @param playerDataDTO the playerDataDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated playerDataDTO,
     * or with status {@code 400 (Bad Request)} if the playerDataDTO is not valid,
     * or with status {@code 404 (Not Found)} if the playerDataDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the playerDataDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/player-data/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PlayerDataDTO> partialUpdatePlayerData(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PlayerDataDTO playerDataDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update PlayerData partially : {}, {}", id, playerDataDTO);
        if (playerDataDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, playerDataDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!playerDataRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PlayerDataDTO> result = playerDataService.partialUpdate(playerDataDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, playerDataDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /player-data} : get all the playerData.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of playerData in body.
     */
    @GetMapping("/player-data")
    public List<PlayerDataDTO> getAllPlayerData() {
        log.debug("REST request to get all PlayerData");
        return playerDataService.findAll();
    }

    /**
     * {@code GET  /player-data/team/:id} : get all the playerData in team with ID.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of playerData in body.
     */
    @GetMapping("/player-data/team/{id}")
    public List<Object[]> getAllPlayerDataInTeam(@PathVariable Long id) {
        log.debug("REST request to get all PlayerData In Team : {}", id);
        return playerDataService.findAllByTeamId(id);
    }

    @GetMapping("player-data/search/{name}")
    public ResponseEntity<PlayerDataDTO> getPlayerDataByName(@PathVariable(value = "name") String name) {
        Optional<PlayerDataDTO> playerDataDTO = playerDataService.findOneByName(name);
        return ResponseUtil.wrapOrNotFound(playerDataDTO);
    }

    @GetMapping("player-data/search-like/{name}")
    public List<PlayerData> getPlayerDataByNameLikeOrOverwatchUsernameLike(@PathVariable(value = "name") String name) {
        List<PlayerData> playerDatas = playerDataService.findAllByNameLikeOrOverwatchUsernameLike(name);
        return playerDatas;
    }

    @GetMapping("player-data/search-like-team/{name}")
    public List<Object[]> getPlayerDataByNameLikeOrOverwatchUsernameLikeWithTeams(@PathVariable(value = "name") String name) {
        List<Object[]> playerDatas = playerDataService.findAllByNameLikeOrOverwatchUsernameLikeWithTeams(name);
        return playerDatas;
    }

    /**
     * {@code GET  /player-data} : get all the playerData from Ids.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of playerData in body.
     */
    @GetMapping("/player-data/list")
    public List<PlayerData> getPlayerDataByPlayerIds(@RequestParam List<Long> playerIds) {
        log.debug("REST request to get all PlayerData from a list of ids");
        return playerDataService.findAllByIds(playerIds);
    }

    /**
     * {@code GET  /player-data/:id} : get the "id" playerData.
     *
     * @param id the id of the playerDataDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the playerDataDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/player-data/{id}")
    public ResponseEntity<PlayerDataDTO> getPlayerData(@PathVariable Long id) {
        log.debug("REST request to get PlayerData : {}", id);
        Optional<PlayerDataDTO> playerDataDTO = playerDataService.findOne(id);
        return ResponseUtil.wrapOrNotFound(playerDataDTO);
    }

    /**
     * {@code GET  /player-data/user/:id} : get the PlayerData of "id" User
     *
     * @param id the id of the User.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the playerDataDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/player-data/user/{id}")
    public ResponseEntity<PlayerDataDTO> getPlayerDataFromUserId(@PathVariable Long id) {
        log.debug("REST request to get PlayerData From UserId : {}", id);
        Optional<PlayerDataDTO> playerDataDTO = playerDataService.findPlayerDataByUserId(id);
        return ResponseUtil.wrapOrNotFound(playerDataDTO);
    }

    /**
     * {@code DELETE  /player-data/:id} : delete the "id" playerData.
     *
     * @param id the id of the playerDataDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/player-data/{id}")
    public ResponseEntity<Void> deletePlayerData(@PathVariable Long id) {
        log.debug("REST request to delete PlayerData : {}", id);
        playerDataService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
