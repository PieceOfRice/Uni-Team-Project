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
import team.bham.repository.ParticipantRepository;
import team.bham.security.AuthoritiesConstants;
import team.bham.service.ParticipantService;
import team.bham.service.dto.ParticipantDTO;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.Participant}.
 */
@RestController
@RequestMapping("/api")
public class ParticipantResource {

    private final Logger log = LoggerFactory.getLogger(ParticipantResource.class);

    private static final String ENTITY_NAME = "participant";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ParticipantService participantService;

    private final ParticipantRepository participantRepository;

    public ParticipantResource(ParticipantService participantService, ParticipantRepository participantRepository) {
        this.participantService = participantService;
        this.participantRepository = participantRepository;
    }

    /**
     * {@code POST  /participants} : Create a new participant.
     *
     * @param participantDTO the participantDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new participantDTO, or with status {@code 400 (Bad Request)}
     *         if the participant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/participants")
    public ResponseEntity<ParticipantDTO> createParticipant(@Valid @RequestBody ParticipantDTO participantDTO) throws URISyntaxException {
        log.debug("REST request to save Participant : {}", participantDTO);
        if (participantDTO.getId() != null) {
            throw new BadRequestAlertException("A new participant cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ParticipantDTO result = participantService.save(participantDTO);
        return ResponseEntity
            .created(new URI("/api/participants/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /participants/:id} : Updates an existing participant.
     *
     * @param id             the id of the participantDTO to save.
     * @param participantDTO the participantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated participantDTO,
     *         or with status {@code 400 (Bad Request)} if the participantDTO is not
     *         valid,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         participantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/participants/{id}")
    public ResponseEntity<ParticipantDTO> updateParticipant(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ParticipantDTO participantDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Participant : {}, {}", id, participantDTO);
        if (participantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, participantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!participantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ParticipantDTO result = participantService.update(participantDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, participantDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /participants/:id} : Partial updates given fields of an
     * existing participant, field will ignore if it is null
     *
     * @param id             the id of the participantDTO to save.
     * @param participantDTO the participantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated participantDTO,
     *         or with status {@code 400 (Bad Request)} if the participantDTO is not
     *         valid,
     *         or with status {@code 404 (Not Found)} if the participantDTO is not
     *         found,
     *         or with status {@code 500 (Internal Server Error)} if the
     *         participantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/participants/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ParticipantDTO> partialUpdateParticipant(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ParticipantDTO participantDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Participant partially : {}, {}", id, participantDTO);
        if (participantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, participantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!participantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ParticipantDTO> result = participantService.partialUpdate(participantDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, participantDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /participants} : get all the participants.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of participants in body.
     */
    @GetMapping("/participants")
    public List<ParticipantDTO> getAllParticipants() {
        log.debug("REST request to get all Participants");
        return participantService.findAll();
    }

    /**
     * {@code GET  /participants/:id} : get the "id" participant.
     *
     * @param id the id of the participantDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the participantDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/participants/{id}")
    public ResponseEntity<ParticipantDTO> getParticipant(@PathVariable Long id) {
        log.debug("REST request to get Participant : {}", id);
        Optional<ParticipantDTO> participantDTO = participantService.findOne(id);
        return ResponseUtil.wrapOrNotFound(participantDTO);
    }

    /**
     * {@code GET  /participants/tournament/:id} : get the participants by
     * tournament "id".
     *
     * @param id the id of the tournament by which to retrieve all participants.
     * @return the list of participantDTOs
     */
    @GetMapping("/participants/tournament/{id}")
    public List<ParticipantDTO> getParticipantsByTournamentId(@PathVariable Long id) {
        log.debug("REST request to get Participants by Tournament ID : {}", id);
        List<ParticipantDTO> participants = participantService.findParticipantsByTournamentId(id);
        return participants;
    }

    /**
     * {@code DELETE  /participants/:id} : delete the "id" participant.
     *
     * @param id the id of the participantDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/participants/{id}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long id) {
        log.debug("REST request to delete Participant : {}", id);
        participantService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    @PutMapping("/participants/join/{tournamentID}/{teamID}/{signUpRank}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void joinTournament(
        @PathVariable(value = "tournamentID") final Long tournamentID,
        @PathVariable(value = "teamID") final Long teamID,
        @PathVariable(value = "signUpRank") int signUpRank
    ) throws URISyntaxException {
        log.debug("REST request to join Tournament : {} {}", teamID, tournamentID);

        participantService.join(tournamentID, teamID, signUpRank);
    }

    @PutMapping("/participants/leave/{tournamentID}/{teamID}/{participantID}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.USER + "\")")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void leaveTournament(
        @PathVariable(value = "tournamentID") final Long tournamentID,
        @PathVariable(value = "teamID") final Long teamID,
        @PathVariable(value = "participantID") final Long participantID
    ) throws URISyntaxException {
        log.debug("REST request to leave Tournament : {} {}", teamID, tournamentID);

        participantService.leave(tournamentID, teamID, participantID);
    }
}
