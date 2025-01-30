package team.bham.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import team.bham.IntegrationTest;
import team.bham.domain.TournamentAccessibility;
import team.bham.domain.enumeration.VenueAccessibilities;
import team.bham.repository.TournamentAccessibilityRepository;
import team.bham.service.dto.TournamentAccessibilityDTO;
import team.bham.service.mapper.TournamentAccessibilityMapper;

/**
 * Integration tests for the {@link TournamentAccessibilityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TournamentAccessibilityResourceIT {

    private static final VenueAccessibilities DEFAULT_ACCESSIBILITY = VenueAccessibilities.ACCESSIBLE_PARKING;
    private static final VenueAccessibilities UPDATED_ACCESSIBILITY = VenueAccessibilities.RAMPS;

    private static final String ENTITY_API_URL = "/api/tournament-accessibilities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TournamentAccessibilityRepository tournamentAccessibilityRepository;

    @Autowired
    private TournamentAccessibilityMapper tournamentAccessibilityMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTournamentAccessibilityMockMvc;

    private TournamentAccessibility tournamentAccessibility;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TournamentAccessibility createEntity(EntityManager em) {
        TournamentAccessibility tournamentAccessibility = new TournamentAccessibility().accessibility(DEFAULT_ACCESSIBILITY);
        return tournamentAccessibility;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TournamentAccessibility createUpdatedEntity(EntityManager em) {
        TournamentAccessibility tournamentAccessibility = new TournamentAccessibility().accessibility(UPDATED_ACCESSIBILITY);
        return tournamentAccessibility;
    }

    @BeforeEach
    public void initTest() {
        tournamentAccessibility = createEntity(em);
    }

    @Test
    @Transactional
    void createTournamentAccessibility() throws Exception {
        int databaseSizeBeforeCreate = tournamentAccessibilityRepository.findAll().size();
        // Create the TournamentAccessibility
        TournamentAccessibilityDTO tournamentAccessibilityDTO = tournamentAccessibilityMapper.toDto(tournamentAccessibility);
        restTournamentAccessibilityMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tournamentAccessibilityDTO))
            )
            .andExpect(status().isCreated());

        // Validate the TournamentAccessibility in the database
        List<TournamentAccessibility> tournamentAccessibilityList = tournamentAccessibilityRepository.findAll();
        assertThat(tournamentAccessibilityList).hasSize(databaseSizeBeforeCreate + 1);
        TournamentAccessibility testTournamentAccessibility = tournamentAccessibilityList.get(tournamentAccessibilityList.size() - 1);
        assertThat(testTournamentAccessibility.getAccessibility()).isEqualTo(DEFAULT_ACCESSIBILITY);
    }

    @Test
    @Transactional
    void createTournamentAccessibilityWithExistingId() throws Exception {
        // Create the TournamentAccessibility with an existing ID
        tournamentAccessibility.setId(1L);
        TournamentAccessibilityDTO tournamentAccessibilityDTO = tournamentAccessibilityMapper.toDto(tournamentAccessibility);

        int databaseSizeBeforeCreate = tournamentAccessibilityRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTournamentAccessibilityMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tournamentAccessibilityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TournamentAccessibility in the database
        List<TournamentAccessibility> tournamentAccessibilityList = tournamentAccessibilityRepository.findAll();
        assertThat(tournamentAccessibilityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAccessibilityIsRequired() throws Exception {
        int databaseSizeBeforeTest = tournamentAccessibilityRepository.findAll().size();
        // set the field null
        tournamentAccessibility.setAccessibility(null);

        // Create the TournamentAccessibility, which fails.
        TournamentAccessibilityDTO tournamentAccessibilityDTO = tournamentAccessibilityMapper.toDto(tournamentAccessibility);

        restTournamentAccessibilityMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tournamentAccessibilityDTO))
            )
            .andExpect(status().isBadRequest());

        List<TournamentAccessibility> tournamentAccessibilityList = tournamentAccessibilityRepository.findAll();
        assertThat(tournamentAccessibilityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTournamentAccessibilities() throws Exception {
        // Initialize the database
        tournamentAccessibilityRepository.saveAndFlush(tournamentAccessibility);

        // Get all the tournamentAccessibilityList
        restTournamentAccessibilityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tournamentAccessibility.getId().intValue())))
            .andExpect(jsonPath("$.[*].accessibility").value(hasItem(DEFAULT_ACCESSIBILITY.toString())));
    }

    @Test
    @Transactional
    void getTournamentAccessibility() throws Exception {
        // Initialize the database
        tournamentAccessibilityRepository.saveAndFlush(tournamentAccessibility);

        // Get the tournamentAccessibility
        restTournamentAccessibilityMockMvc
            .perform(get(ENTITY_API_URL_ID, tournamentAccessibility.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tournamentAccessibility.getId().intValue()))
            .andExpect(jsonPath("$.accessibility").value(DEFAULT_ACCESSIBILITY.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTournamentAccessibility() throws Exception {
        // Get the tournamentAccessibility
        restTournamentAccessibilityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTournamentAccessibility() throws Exception {
        // Initialize the database
        tournamentAccessibilityRepository.saveAndFlush(tournamentAccessibility);

        int databaseSizeBeforeUpdate = tournamentAccessibilityRepository.findAll().size();

        // Update the tournamentAccessibility
        TournamentAccessibility updatedTournamentAccessibility = tournamentAccessibilityRepository
            .findById(tournamentAccessibility.getId())
            .get();
        // Disconnect from session so that the updates on updatedTournamentAccessibility are not directly saved in db
        em.detach(updatedTournamentAccessibility);
        updatedTournamentAccessibility.accessibility(UPDATED_ACCESSIBILITY);
        TournamentAccessibilityDTO tournamentAccessibilityDTO = tournamentAccessibilityMapper.toDto(updatedTournamentAccessibility);

        restTournamentAccessibilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tournamentAccessibilityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tournamentAccessibilityDTO))
            )
            .andExpect(status().isOk());

        // Validate the TournamentAccessibility in the database
        List<TournamentAccessibility> tournamentAccessibilityList = tournamentAccessibilityRepository.findAll();
        assertThat(tournamentAccessibilityList).hasSize(databaseSizeBeforeUpdate);
        TournamentAccessibility testTournamentAccessibility = tournamentAccessibilityList.get(tournamentAccessibilityList.size() - 1);
        assertThat(testTournamentAccessibility.getAccessibility()).isEqualTo(UPDATED_ACCESSIBILITY);
    }

    @Test
    @Transactional
    void putNonExistingTournamentAccessibility() throws Exception {
        int databaseSizeBeforeUpdate = tournamentAccessibilityRepository.findAll().size();
        tournamentAccessibility.setId(count.incrementAndGet());

        // Create the TournamentAccessibility
        TournamentAccessibilityDTO tournamentAccessibilityDTO = tournamentAccessibilityMapper.toDto(tournamentAccessibility);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTournamentAccessibilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tournamentAccessibilityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tournamentAccessibilityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TournamentAccessibility in the database
        List<TournamentAccessibility> tournamentAccessibilityList = tournamentAccessibilityRepository.findAll();
        assertThat(tournamentAccessibilityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTournamentAccessibility() throws Exception {
        int databaseSizeBeforeUpdate = tournamentAccessibilityRepository.findAll().size();
        tournamentAccessibility.setId(count.incrementAndGet());

        // Create the TournamentAccessibility
        TournamentAccessibilityDTO tournamentAccessibilityDTO = tournamentAccessibilityMapper.toDto(tournamentAccessibility);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTournamentAccessibilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tournamentAccessibilityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TournamentAccessibility in the database
        List<TournamentAccessibility> tournamentAccessibilityList = tournamentAccessibilityRepository.findAll();
        assertThat(tournamentAccessibilityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTournamentAccessibility() throws Exception {
        int databaseSizeBeforeUpdate = tournamentAccessibilityRepository.findAll().size();
        tournamentAccessibility.setId(count.incrementAndGet());

        // Create the TournamentAccessibility
        TournamentAccessibilityDTO tournamentAccessibilityDTO = tournamentAccessibilityMapper.toDto(tournamentAccessibility);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTournamentAccessibilityMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tournamentAccessibilityDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TournamentAccessibility in the database
        List<TournamentAccessibility> tournamentAccessibilityList = tournamentAccessibilityRepository.findAll();
        assertThat(tournamentAccessibilityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTournamentAccessibilityWithPatch() throws Exception {
        // Initialize the database
        tournamentAccessibilityRepository.saveAndFlush(tournamentAccessibility);

        int databaseSizeBeforeUpdate = tournamentAccessibilityRepository.findAll().size();

        // Update the tournamentAccessibility using partial update
        TournamentAccessibility partialUpdatedTournamentAccessibility = new TournamentAccessibility();
        partialUpdatedTournamentAccessibility.setId(tournamentAccessibility.getId());

        partialUpdatedTournamentAccessibility.accessibility(UPDATED_ACCESSIBILITY);

        restTournamentAccessibilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTournamentAccessibility.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTournamentAccessibility))
            )
            .andExpect(status().isOk());

        // Validate the TournamentAccessibility in the database
        List<TournamentAccessibility> tournamentAccessibilityList = tournamentAccessibilityRepository.findAll();
        assertThat(tournamentAccessibilityList).hasSize(databaseSizeBeforeUpdate);
        TournamentAccessibility testTournamentAccessibility = tournamentAccessibilityList.get(tournamentAccessibilityList.size() - 1);
        assertThat(testTournamentAccessibility.getAccessibility()).isEqualTo(UPDATED_ACCESSIBILITY);
    }

    @Test
    @Transactional
    void fullUpdateTournamentAccessibilityWithPatch() throws Exception {
        // Initialize the database
        tournamentAccessibilityRepository.saveAndFlush(tournamentAccessibility);

        int databaseSizeBeforeUpdate = tournamentAccessibilityRepository.findAll().size();

        // Update the tournamentAccessibility using partial update
        TournamentAccessibility partialUpdatedTournamentAccessibility = new TournamentAccessibility();
        partialUpdatedTournamentAccessibility.setId(tournamentAccessibility.getId());

        partialUpdatedTournamentAccessibility.accessibility(UPDATED_ACCESSIBILITY);

        restTournamentAccessibilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTournamentAccessibility.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTournamentAccessibility))
            )
            .andExpect(status().isOk());

        // Validate the TournamentAccessibility in the database
        List<TournamentAccessibility> tournamentAccessibilityList = tournamentAccessibilityRepository.findAll();
        assertThat(tournamentAccessibilityList).hasSize(databaseSizeBeforeUpdate);
        TournamentAccessibility testTournamentAccessibility = tournamentAccessibilityList.get(tournamentAccessibilityList.size() - 1);
        assertThat(testTournamentAccessibility.getAccessibility()).isEqualTo(UPDATED_ACCESSIBILITY);
    }

    @Test
    @Transactional
    void patchNonExistingTournamentAccessibility() throws Exception {
        int databaseSizeBeforeUpdate = tournamentAccessibilityRepository.findAll().size();
        tournamentAccessibility.setId(count.incrementAndGet());

        // Create the TournamentAccessibility
        TournamentAccessibilityDTO tournamentAccessibilityDTO = tournamentAccessibilityMapper.toDto(tournamentAccessibility);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTournamentAccessibilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tournamentAccessibilityDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tournamentAccessibilityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TournamentAccessibility in the database
        List<TournamentAccessibility> tournamentAccessibilityList = tournamentAccessibilityRepository.findAll();
        assertThat(tournamentAccessibilityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTournamentAccessibility() throws Exception {
        int databaseSizeBeforeUpdate = tournamentAccessibilityRepository.findAll().size();
        tournamentAccessibility.setId(count.incrementAndGet());

        // Create the TournamentAccessibility
        TournamentAccessibilityDTO tournamentAccessibilityDTO = tournamentAccessibilityMapper.toDto(tournamentAccessibility);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTournamentAccessibilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tournamentAccessibilityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TournamentAccessibility in the database
        List<TournamentAccessibility> tournamentAccessibilityList = tournamentAccessibilityRepository.findAll();
        assertThat(tournamentAccessibilityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTournamentAccessibility() throws Exception {
        int databaseSizeBeforeUpdate = tournamentAccessibilityRepository.findAll().size();
        tournamentAccessibility.setId(count.incrementAndGet());

        // Create the TournamentAccessibility
        TournamentAccessibilityDTO tournamentAccessibilityDTO = tournamentAccessibilityMapper.toDto(tournamentAccessibility);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTournamentAccessibilityMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tournamentAccessibilityDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TournamentAccessibility in the database
        List<TournamentAccessibility> tournamentAccessibilityList = tournamentAccessibilityRepository.findAll();
        assertThat(tournamentAccessibilityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTournamentAccessibility() throws Exception {
        // Initialize the database
        tournamentAccessibilityRepository.saveAndFlush(tournamentAccessibility);

        int databaseSizeBeforeDelete = tournamentAccessibilityRepository.findAll().size();

        // Delete the tournamentAccessibility
        restTournamentAccessibilityMockMvc
            .perform(delete(ENTITY_API_URL_ID, tournamentAccessibility.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TournamentAccessibility> tournamentAccessibilityList = tournamentAccessibilityRepository.findAll();
        assertThat(tournamentAccessibilityList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
