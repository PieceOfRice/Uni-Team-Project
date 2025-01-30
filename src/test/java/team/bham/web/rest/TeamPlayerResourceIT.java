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
import team.bham.domain.TeamPlayer;
import team.bham.domain.enumeration.TeamRole;
import team.bham.repository.TeamPlayerRepository;
import team.bham.service.dto.TeamPlayerDTO;
import team.bham.service.mapper.TeamPlayerMapper;

/**
 * Integration tests for the {@link TeamPlayerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TeamPlayerResourceIT {

    private static final TeamRole DEFAULT_ROLE = TeamRole.LEADER;
    private static final TeamRole UPDATED_ROLE = TeamRole.COACH;

    private static final String ENTITY_API_URL = "/api/team-players";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TeamPlayerRepository teamPlayerRepository;

    @Autowired
    private TeamPlayerMapper teamPlayerMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTeamPlayerMockMvc;

    private TeamPlayer teamPlayer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeamPlayer createEntity(EntityManager em) {
        TeamPlayer teamPlayer = new TeamPlayer().role(DEFAULT_ROLE);
        return teamPlayer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeamPlayer createUpdatedEntity(EntityManager em) {
        TeamPlayer teamPlayer = new TeamPlayer().role(UPDATED_ROLE);
        return teamPlayer;
    }

    @BeforeEach
    public void initTest() {
        teamPlayer = createEntity(em);
    }

    @Test
    @Transactional
    void createTeamPlayer() throws Exception {
        int databaseSizeBeforeCreate = teamPlayerRepository.findAll().size();
        // Create the TeamPlayer
        TeamPlayerDTO teamPlayerDTO = teamPlayerMapper.toDto(teamPlayer);
        restTeamPlayerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teamPlayerDTO)))
            .andExpect(status().isCreated());

        // Validate the TeamPlayer in the database
        List<TeamPlayer> teamPlayerList = teamPlayerRepository.findAll();
        assertThat(teamPlayerList).hasSize(databaseSizeBeforeCreate + 1);
        TeamPlayer testTeamPlayer = teamPlayerList.get(teamPlayerList.size() - 1);
        assertThat(testTeamPlayer.getRole()).isEqualTo(DEFAULT_ROLE);
    }

    @Test
    @Transactional
    void createTeamPlayerWithExistingId() throws Exception {
        // Create the TeamPlayer with an existing ID
        teamPlayer.setId(1L);
        TeamPlayerDTO teamPlayerDTO = teamPlayerMapper.toDto(teamPlayer);

        int databaseSizeBeforeCreate = teamPlayerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTeamPlayerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teamPlayerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TeamPlayer in the database
        List<TeamPlayer> teamPlayerList = teamPlayerRepository.findAll();
        assertThat(teamPlayerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRoleIsRequired() throws Exception {
        int databaseSizeBeforeTest = teamPlayerRepository.findAll().size();
        // set the field null
        teamPlayer.setRole(null);

        // Create the TeamPlayer, which fails.
        TeamPlayerDTO teamPlayerDTO = teamPlayerMapper.toDto(teamPlayer);

        restTeamPlayerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teamPlayerDTO)))
            .andExpect(status().isBadRequest());

        List<TeamPlayer> teamPlayerList = teamPlayerRepository.findAll();
        assertThat(teamPlayerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTeamPlayers() throws Exception {
        // Initialize the database
        teamPlayerRepository.saveAndFlush(teamPlayer);

        // Get all the teamPlayerList
        restTeamPlayerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teamPlayer.getId().intValue())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())));
    }

    @Test
    @Transactional
    void getTeamPlayer() throws Exception {
        // Initialize the database
        teamPlayerRepository.saveAndFlush(teamPlayer);

        // Get the teamPlayer
        restTeamPlayerMockMvc
            .perform(get(ENTITY_API_URL_ID, teamPlayer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(teamPlayer.getId().intValue()))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTeamPlayer() throws Exception {
        // Get the teamPlayer
        restTeamPlayerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTeamPlayer() throws Exception {
        // Initialize the database
        teamPlayerRepository.saveAndFlush(teamPlayer);

        int databaseSizeBeforeUpdate = teamPlayerRepository.findAll().size();

        // Update the teamPlayer
        TeamPlayer updatedTeamPlayer = teamPlayerRepository.findById(teamPlayer.getId()).get();
        // Disconnect from session so that the updates on updatedTeamPlayer are not directly saved in db
        em.detach(updatedTeamPlayer);
        updatedTeamPlayer.role(UPDATED_ROLE);
        TeamPlayerDTO teamPlayerDTO = teamPlayerMapper.toDto(updatedTeamPlayer);

        restTeamPlayerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, teamPlayerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teamPlayerDTO))
            )
            .andExpect(status().isOk());

        // Validate the TeamPlayer in the database
        List<TeamPlayer> teamPlayerList = teamPlayerRepository.findAll();
        assertThat(teamPlayerList).hasSize(databaseSizeBeforeUpdate);
        TeamPlayer testTeamPlayer = teamPlayerList.get(teamPlayerList.size() - 1);
        assertThat(testTeamPlayer.getRole()).isEqualTo(UPDATED_ROLE);
    }

    @Test
    @Transactional
    void putNonExistingTeamPlayer() throws Exception {
        int databaseSizeBeforeUpdate = teamPlayerRepository.findAll().size();
        teamPlayer.setId(count.incrementAndGet());

        // Create the TeamPlayer
        TeamPlayerDTO teamPlayerDTO = teamPlayerMapper.toDto(teamPlayer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamPlayerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, teamPlayerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teamPlayerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamPlayer in the database
        List<TeamPlayer> teamPlayerList = teamPlayerRepository.findAll();
        assertThat(teamPlayerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTeamPlayer() throws Exception {
        int databaseSizeBeforeUpdate = teamPlayerRepository.findAll().size();
        teamPlayer.setId(count.incrementAndGet());

        // Create the TeamPlayer
        TeamPlayerDTO teamPlayerDTO = teamPlayerMapper.toDto(teamPlayer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamPlayerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teamPlayerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamPlayer in the database
        List<TeamPlayer> teamPlayerList = teamPlayerRepository.findAll();
        assertThat(teamPlayerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTeamPlayer() throws Exception {
        int databaseSizeBeforeUpdate = teamPlayerRepository.findAll().size();
        teamPlayer.setId(count.incrementAndGet());

        // Create the TeamPlayer
        TeamPlayerDTO teamPlayerDTO = teamPlayerMapper.toDto(teamPlayer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamPlayerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teamPlayerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TeamPlayer in the database
        List<TeamPlayer> teamPlayerList = teamPlayerRepository.findAll();
        assertThat(teamPlayerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTeamPlayerWithPatch() throws Exception {
        // Initialize the database
        teamPlayerRepository.saveAndFlush(teamPlayer);

        int databaseSizeBeforeUpdate = teamPlayerRepository.findAll().size();

        // Update the teamPlayer using partial update
        TeamPlayer partialUpdatedTeamPlayer = new TeamPlayer();
        partialUpdatedTeamPlayer.setId(teamPlayer.getId());

        partialUpdatedTeamPlayer.role(UPDATED_ROLE);

        restTeamPlayerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeamPlayer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTeamPlayer))
            )
            .andExpect(status().isOk());

        // Validate the TeamPlayer in the database
        List<TeamPlayer> teamPlayerList = teamPlayerRepository.findAll();
        assertThat(teamPlayerList).hasSize(databaseSizeBeforeUpdate);
        TeamPlayer testTeamPlayer = teamPlayerList.get(teamPlayerList.size() - 1);
        assertThat(testTeamPlayer.getRole()).isEqualTo(UPDATED_ROLE);
    }

    @Test
    @Transactional
    void fullUpdateTeamPlayerWithPatch() throws Exception {
        // Initialize the database
        teamPlayerRepository.saveAndFlush(teamPlayer);

        int databaseSizeBeforeUpdate = teamPlayerRepository.findAll().size();

        // Update the teamPlayer using partial update
        TeamPlayer partialUpdatedTeamPlayer = new TeamPlayer();
        partialUpdatedTeamPlayer.setId(teamPlayer.getId());

        partialUpdatedTeamPlayer.role(UPDATED_ROLE);

        restTeamPlayerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeamPlayer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTeamPlayer))
            )
            .andExpect(status().isOk());

        // Validate the TeamPlayer in the database
        List<TeamPlayer> teamPlayerList = teamPlayerRepository.findAll();
        assertThat(teamPlayerList).hasSize(databaseSizeBeforeUpdate);
        TeamPlayer testTeamPlayer = teamPlayerList.get(teamPlayerList.size() - 1);
        assertThat(testTeamPlayer.getRole()).isEqualTo(UPDATED_ROLE);
    }

    @Test
    @Transactional
    void patchNonExistingTeamPlayer() throws Exception {
        int databaseSizeBeforeUpdate = teamPlayerRepository.findAll().size();
        teamPlayer.setId(count.incrementAndGet());

        // Create the TeamPlayer
        TeamPlayerDTO teamPlayerDTO = teamPlayerMapper.toDto(teamPlayer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamPlayerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, teamPlayerDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(teamPlayerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamPlayer in the database
        List<TeamPlayer> teamPlayerList = teamPlayerRepository.findAll();
        assertThat(teamPlayerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTeamPlayer() throws Exception {
        int databaseSizeBeforeUpdate = teamPlayerRepository.findAll().size();
        teamPlayer.setId(count.incrementAndGet());

        // Create the TeamPlayer
        TeamPlayerDTO teamPlayerDTO = teamPlayerMapper.toDto(teamPlayer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamPlayerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(teamPlayerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamPlayer in the database
        List<TeamPlayer> teamPlayerList = teamPlayerRepository.findAll();
        assertThat(teamPlayerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTeamPlayer() throws Exception {
        int databaseSizeBeforeUpdate = teamPlayerRepository.findAll().size();
        teamPlayer.setId(count.incrementAndGet());

        // Create the TeamPlayer
        TeamPlayerDTO teamPlayerDTO = teamPlayerMapper.toDto(teamPlayer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamPlayerMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(teamPlayerDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TeamPlayer in the database
        List<TeamPlayer> teamPlayerList = teamPlayerRepository.findAll();
        assertThat(teamPlayerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTeamPlayer() throws Exception {
        // Initialize the database
        teamPlayerRepository.saveAndFlush(teamPlayer);

        int databaseSizeBeforeDelete = teamPlayerRepository.findAll().size();

        // Delete the teamPlayer
        restTeamPlayerMockMvc
            .perform(delete(ENTITY_API_URL_ID, teamPlayer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TeamPlayer> teamPlayerList = teamPlayerRepository.findAll();
        assertThat(teamPlayerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
