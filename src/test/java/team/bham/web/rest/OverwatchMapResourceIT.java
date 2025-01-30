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
import team.bham.domain.OverwatchMap;
import team.bham.domain.enumeration.MapMode;
import team.bham.repository.OverwatchMapRepository;
import team.bham.service.dto.OverwatchMapDTO;
import team.bham.service.mapper.OverwatchMapMapper;

/**
 * Integration tests for the {@link OverwatchMapResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OverwatchMapResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final MapMode DEFAULT_MODE = MapMode.CONTROL;
    private static final MapMode UPDATED_MODE = MapMode.ESCORT;

    private static final String ENTITY_API_URL = "/api/overwatch-maps";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OverwatchMapRepository overwatchMapRepository;

    @Autowired
    private OverwatchMapMapper overwatchMapMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOverwatchMapMockMvc;

    private OverwatchMap overwatchMap;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OverwatchMap createEntity(EntityManager em) {
        OverwatchMap overwatchMap = new OverwatchMap().name(DEFAULT_NAME).mode(DEFAULT_MODE);
        return overwatchMap;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OverwatchMap createUpdatedEntity(EntityManager em) {
        OverwatchMap overwatchMap = new OverwatchMap().name(UPDATED_NAME).mode(UPDATED_MODE);
        return overwatchMap;
    }

    @BeforeEach
    public void initTest() {
        overwatchMap = createEntity(em);
    }

    @Test
    @Transactional
    void createOverwatchMap() throws Exception {
        int databaseSizeBeforeCreate = overwatchMapRepository.findAll().size();
        // Create the OverwatchMap
        OverwatchMapDTO overwatchMapDTO = overwatchMapMapper.toDto(overwatchMap);
        restOverwatchMapMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(overwatchMapDTO))
            )
            .andExpect(status().isCreated());

        // Validate the OverwatchMap in the database
        List<OverwatchMap> overwatchMapList = overwatchMapRepository.findAll();
        assertThat(overwatchMapList).hasSize(databaseSizeBeforeCreate + 1);
        OverwatchMap testOverwatchMap = overwatchMapList.get(overwatchMapList.size() - 1);
        assertThat(testOverwatchMap.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOverwatchMap.getMode()).isEqualTo(DEFAULT_MODE);
    }

    @Test
    @Transactional
    void createOverwatchMapWithExistingId() throws Exception {
        // Create the OverwatchMap with an existing ID
        overwatchMap.setId(1L);
        OverwatchMapDTO overwatchMapDTO = overwatchMapMapper.toDto(overwatchMap);

        int databaseSizeBeforeCreate = overwatchMapRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOverwatchMapMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(overwatchMapDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OverwatchMap in the database
        List<OverwatchMap> overwatchMapList = overwatchMapRepository.findAll();
        assertThat(overwatchMapList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = overwatchMapRepository.findAll().size();
        // set the field null
        overwatchMap.setName(null);

        // Create the OverwatchMap, which fails.
        OverwatchMapDTO overwatchMapDTO = overwatchMapMapper.toDto(overwatchMap);

        restOverwatchMapMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(overwatchMapDTO))
            )
            .andExpect(status().isBadRequest());

        List<OverwatchMap> overwatchMapList = overwatchMapRepository.findAll();
        assertThat(overwatchMapList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkModeIsRequired() throws Exception {
        int databaseSizeBeforeTest = overwatchMapRepository.findAll().size();
        // set the field null
        overwatchMap.setMode(null);

        // Create the OverwatchMap, which fails.
        OverwatchMapDTO overwatchMapDTO = overwatchMapMapper.toDto(overwatchMap);

        restOverwatchMapMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(overwatchMapDTO))
            )
            .andExpect(status().isBadRequest());

        List<OverwatchMap> overwatchMapList = overwatchMapRepository.findAll();
        assertThat(overwatchMapList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOverwatchMaps() throws Exception {
        // Initialize the database
        overwatchMapRepository.saveAndFlush(overwatchMap);

        // Get all the overwatchMapList
        restOverwatchMapMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(overwatchMap.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].mode").value(hasItem(DEFAULT_MODE.toString())));
    }

    @Test
    @Transactional
    void getOverwatchMap() throws Exception {
        // Initialize the database
        overwatchMapRepository.saveAndFlush(overwatchMap);

        // Get the overwatchMap
        restOverwatchMapMockMvc
            .perform(get(ENTITY_API_URL_ID, overwatchMap.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(overwatchMap.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.mode").value(DEFAULT_MODE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingOverwatchMap() throws Exception {
        // Get the overwatchMap
        restOverwatchMapMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOverwatchMap() throws Exception {
        // Initialize the database
        overwatchMapRepository.saveAndFlush(overwatchMap);

        int databaseSizeBeforeUpdate = overwatchMapRepository.findAll().size();

        // Update the overwatchMap
        OverwatchMap updatedOverwatchMap = overwatchMapRepository.findById(overwatchMap.getId()).get();
        // Disconnect from session so that the updates on updatedOverwatchMap are not directly saved in db
        em.detach(updatedOverwatchMap);
        updatedOverwatchMap.name(UPDATED_NAME).mode(UPDATED_MODE);
        OverwatchMapDTO overwatchMapDTO = overwatchMapMapper.toDto(updatedOverwatchMap);

        restOverwatchMapMockMvc
            .perform(
                put(ENTITY_API_URL_ID, overwatchMapDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(overwatchMapDTO))
            )
            .andExpect(status().isOk());

        // Validate the OverwatchMap in the database
        List<OverwatchMap> overwatchMapList = overwatchMapRepository.findAll();
        assertThat(overwatchMapList).hasSize(databaseSizeBeforeUpdate);
        OverwatchMap testOverwatchMap = overwatchMapList.get(overwatchMapList.size() - 1);
        assertThat(testOverwatchMap.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOverwatchMap.getMode()).isEqualTo(UPDATED_MODE);
    }

    @Test
    @Transactional
    void putNonExistingOverwatchMap() throws Exception {
        int databaseSizeBeforeUpdate = overwatchMapRepository.findAll().size();
        overwatchMap.setId(count.incrementAndGet());

        // Create the OverwatchMap
        OverwatchMapDTO overwatchMapDTO = overwatchMapMapper.toDto(overwatchMap);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOverwatchMapMockMvc
            .perform(
                put(ENTITY_API_URL_ID, overwatchMapDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(overwatchMapDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OverwatchMap in the database
        List<OverwatchMap> overwatchMapList = overwatchMapRepository.findAll();
        assertThat(overwatchMapList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOverwatchMap() throws Exception {
        int databaseSizeBeforeUpdate = overwatchMapRepository.findAll().size();
        overwatchMap.setId(count.incrementAndGet());

        // Create the OverwatchMap
        OverwatchMapDTO overwatchMapDTO = overwatchMapMapper.toDto(overwatchMap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOverwatchMapMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(overwatchMapDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OverwatchMap in the database
        List<OverwatchMap> overwatchMapList = overwatchMapRepository.findAll();
        assertThat(overwatchMapList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOverwatchMap() throws Exception {
        int databaseSizeBeforeUpdate = overwatchMapRepository.findAll().size();
        overwatchMap.setId(count.incrementAndGet());

        // Create the OverwatchMap
        OverwatchMapDTO overwatchMapDTO = overwatchMapMapper.toDto(overwatchMap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOverwatchMapMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(overwatchMapDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OverwatchMap in the database
        List<OverwatchMap> overwatchMapList = overwatchMapRepository.findAll();
        assertThat(overwatchMapList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOverwatchMapWithPatch() throws Exception {
        // Initialize the database
        overwatchMapRepository.saveAndFlush(overwatchMap);

        int databaseSizeBeforeUpdate = overwatchMapRepository.findAll().size();

        // Update the overwatchMap using partial update
        OverwatchMap partialUpdatedOverwatchMap = new OverwatchMap();
        partialUpdatedOverwatchMap.setId(overwatchMap.getId());

        partialUpdatedOverwatchMap.mode(UPDATED_MODE);

        restOverwatchMapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOverwatchMap.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOverwatchMap))
            )
            .andExpect(status().isOk());

        // Validate the OverwatchMap in the database
        List<OverwatchMap> overwatchMapList = overwatchMapRepository.findAll();
        assertThat(overwatchMapList).hasSize(databaseSizeBeforeUpdate);
        OverwatchMap testOverwatchMap = overwatchMapList.get(overwatchMapList.size() - 1);
        assertThat(testOverwatchMap.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOverwatchMap.getMode()).isEqualTo(UPDATED_MODE);
    }

    @Test
    @Transactional
    void fullUpdateOverwatchMapWithPatch() throws Exception {
        // Initialize the database
        overwatchMapRepository.saveAndFlush(overwatchMap);

        int databaseSizeBeforeUpdate = overwatchMapRepository.findAll().size();

        // Update the overwatchMap using partial update
        OverwatchMap partialUpdatedOverwatchMap = new OverwatchMap();
        partialUpdatedOverwatchMap.setId(overwatchMap.getId());

        partialUpdatedOverwatchMap.name(UPDATED_NAME).mode(UPDATED_MODE);

        restOverwatchMapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOverwatchMap.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOverwatchMap))
            )
            .andExpect(status().isOk());

        // Validate the OverwatchMap in the database
        List<OverwatchMap> overwatchMapList = overwatchMapRepository.findAll();
        assertThat(overwatchMapList).hasSize(databaseSizeBeforeUpdate);
        OverwatchMap testOverwatchMap = overwatchMapList.get(overwatchMapList.size() - 1);
        assertThat(testOverwatchMap.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOverwatchMap.getMode()).isEqualTo(UPDATED_MODE);
    }

    @Test
    @Transactional
    void patchNonExistingOverwatchMap() throws Exception {
        int databaseSizeBeforeUpdate = overwatchMapRepository.findAll().size();
        overwatchMap.setId(count.incrementAndGet());

        // Create the OverwatchMap
        OverwatchMapDTO overwatchMapDTO = overwatchMapMapper.toDto(overwatchMap);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOverwatchMapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, overwatchMapDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(overwatchMapDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OverwatchMap in the database
        List<OverwatchMap> overwatchMapList = overwatchMapRepository.findAll();
        assertThat(overwatchMapList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOverwatchMap() throws Exception {
        int databaseSizeBeforeUpdate = overwatchMapRepository.findAll().size();
        overwatchMap.setId(count.incrementAndGet());

        // Create the OverwatchMap
        OverwatchMapDTO overwatchMapDTO = overwatchMapMapper.toDto(overwatchMap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOverwatchMapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(overwatchMapDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OverwatchMap in the database
        List<OverwatchMap> overwatchMapList = overwatchMapRepository.findAll();
        assertThat(overwatchMapList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOverwatchMap() throws Exception {
        int databaseSizeBeforeUpdate = overwatchMapRepository.findAll().size();
        overwatchMap.setId(count.incrementAndGet());

        // Create the OverwatchMap
        OverwatchMapDTO overwatchMapDTO = overwatchMapMapper.toDto(overwatchMap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOverwatchMapMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(overwatchMapDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OverwatchMap in the database
        List<OverwatchMap> overwatchMapList = overwatchMapRepository.findAll();
        assertThat(overwatchMapList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOverwatchMap() throws Exception {
        // Initialize the database
        overwatchMapRepository.saveAndFlush(overwatchMap);

        int databaseSizeBeforeDelete = overwatchMapRepository.findAll().size();

        // Delete the overwatchMap
        restOverwatchMapMockMvc
            .perform(delete(ENTITY_API_URL_ID, overwatchMap.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OverwatchMap> overwatchMapList = overwatchMapRepository.findAll();
        assertThat(overwatchMapList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
