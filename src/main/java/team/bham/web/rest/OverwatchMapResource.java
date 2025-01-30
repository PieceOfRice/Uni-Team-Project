package team.bham.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.bham.repository.OverwatchMapRepository;
import team.bham.service.OverwatchMapService;
import team.bham.service.dto.OverwatchMapDTO;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.OverwatchMap}.
 */
@RestController
@RequestMapping("/api")
public class OverwatchMapResource {

    private final Logger log = LoggerFactory.getLogger(OverwatchMapResource.class);

    private static final String ENTITY_NAME = "overwatchMap";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OverwatchMapService overwatchMapService;

    private final OverwatchMapRepository overwatchMapRepository;

    public OverwatchMapResource(OverwatchMapService overwatchMapService, OverwatchMapRepository overwatchMapRepository) {
        this.overwatchMapService = overwatchMapService;
        this.overwatchMapRepository = overwatchMapRepository;
    }

    /**
     * {@code POST  /overwatch-maps} : Create a new overwatchMap.
     *
     * @param overwatchMapDTO the overwatchMapDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new overwatchMapDTO, or with status {@code 400 (Bad Request)} if the overwatchMap has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/overwatch-maps")
    public ResponseEntity<OverwatchMapDTO> createOverwatchMap(@Valid @RequestBody OverwatchMapDTO overwatchMapDTO)
        throws URISyntaxException {
        log.debug("REST request to save OverwatchMap : {}", overwatchMapDTO);
        if (overwatchMapDTO.getId() != null) {
            throw new BadRequestAlertException("A new overwatchMap cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OverwatchMapDTO result = overwatchMapService.save(overwatchMapDTO);
        return ResponseEntity
            .created(new URI("/api/overwatch-maps/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /overwatch-maps/:id} : Updates an existing overwatchMap.
     *
     * @param id the id of the overwatchMapDTO to save.
     * @param overwatchMapDTO the overwatchMapDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated overwatchMapDTO,
     * or with status {@code 400 (Bad Request)} if the overwatchMapDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the overwatchMapDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/overwatch-maps/{id}")
    public ResponseEntity<OverwatchMapDTO> updateOverwatchMap(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OverwatchMapDTO overwatchMapDTO
    ) throws URISyntaxException {
        log.debug("REST request to update OverwatchMap : {}, {}", id, overwatchMapDTO);
        if (overwatchMapDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, overwatchMapDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!overwatchMapRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        OverwatchMapDTO result = overwatchMapService.update(overwatchMapDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, overwatchMapDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /overwatch-maps/:id} : Partial updates given fields of an existing overwatchMap, field will ignore if it is null
     *
     * @param id the id of the overwatchMapDTO to save.
     * @param overwatchMapDTO the overwatchMapDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated overwatchMapDTO,
     * or with status {@code 400 (Bad Request)} if the overwatchMapDTO is not valid,
     * or with status {@code 404 (Not Found)} if the overwatchMapDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the overwatchMapDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/overwatch-maps/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OverwatchMapDTO> partialUpdateOverwatchMap(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OverwatchMapDTO overwatchMapDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update OverwatchMap partially : {}, {}", id, overwatchMapDTO);
        if (overwatchMapDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, overwatchMapDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!overwatchMapRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OverwatchMapDTO> result = overwatchMapService.partialUpdate(overwatchMapDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, overwatchMapDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /overwatch-maps} : get all the overwatchMaps.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of overwatchMaps in body.
     */
    @GetMapping("/overwatch-maps")
    public List<OverwatchMapDTO> getAllOverwatchMaps(@RequestParam(required = false) String filter) {
        if ("game-is-null".equals(filter)) {
            log.debug("REST request to get all OverwatchMaps");
            return overwatchMapService.findAll();
        }
        log.debug("REST request to get all OverwatchMaps");
        return overwatchMapService.findAll();
    }

    /**
     * {@code GET  /overwatch-maps/:id} : get the "id" overwatchMap.
     *
     * @param id the id of the overwatchMapDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the overwatchMapDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/overwatch-maps/{id}")
    public ResponseEntity<OverwatchMapDTO> getOverwatchMap(@PathVariable Long id) {
        log.debug("REST request to get OverwatchMap : {}", id);
        Optional<OverwatchMapDTO> overwatchMapDTO = overwatchMapService.findOne(id);
        return ResponseUtil.wrapOrNotFound(overwatchMapDTO);
    }

    /**
     * {@code DELETE  /overwatch-maps/:id} : delete the "id" overwatchMap.
     *
     * @param id the id of the overwatchMapDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/overwatch-maps/{id}")
    public ResponseEntity<Void> deleteOverwatchMap(@PathVariable Long id) {
        log.debug("REST request to delete OverwatchMap : {}", id);
        overwatchMapService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
