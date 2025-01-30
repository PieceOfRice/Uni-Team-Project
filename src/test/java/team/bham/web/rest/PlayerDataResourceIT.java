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
import org.springframework.util.Base64Utils;
import team.bham.IntegrationTest;
import team.bham.domain.PlayerData;
import team.bham.domain.enumeration.PlayerDevice;
import team.bham.domain.enumeration.PlayerLanguage;
import team.bham.repository.PlayerDataRepository;
import team.bham.service.dto.PlayerDataDTO;
import team.bham.service.mapper.PlayerDataMapper;

/**
 * Integration tests for the {@link PlayerDataResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PlayerDataResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_OVERWATCH_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_OVERWATCH_USERNAME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_PROFILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PROFILE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PROFILE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PROFILE_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_BIO = "AAAAAAAAAA";
    private static final String UPDATED_BIO = "BBBBBBBBBB";

    private static final PlayerLanguage DEFAULT_PRIMARY_LANGUAGE = PlayerLanguage.ENGLISH;
    private static final PlayerLanguage UPDATED_PRIMARY_LANGUAGE = PlayerLanguage.SPANISH;

    private static final PlayerDevice DEFAULT_DEVICE = PlayerDevice.DESKTOP;
    private static final PlayerDevice UPDATED_DEVICE = PlayerDevice.CONSOLE;

    private static final Integer DEFAULT_MATCHES_PLAYED = 0;
    private static final Integer UPDATED_MATCHES_PLAYED = 1;

    private static final Integer DEFAULT_TOURNAMENTS_PLAYED = 0;
    private static final Integer UPDATED_TOURNAMENTS_PLAYED = 1;

    private static final Integer DEFAULT_MATCH_WINS = 0;
    private static final Integer UPDATED_MATCH_WINS = 1;

    private static final Integer DEFAULT_TOURNAMENT_WINS = 0;
    private static final Integer UPDATED_TOURNAMENT_WINS = 1;

    private static final Integer DEFAULT_TOURNAMENT_TOP_10_S = 0;
    private static final Integer UPDATED_TOURNAMENT_TOP_10_S = 1;

    private static final String ENTITY_API_URL = "/api/player-data";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PlayerDataRepository playerDataRepository;

    @Autowired
    private PlayerDataMapper playerDataMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPlayerDataMockMvc;

    private PlayerData playerData;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlayerData createEntity(EntityManager em) {
        PlayerData playerData = new PlayerData()
            .name(DEFAULT_NAME)
            .overwatchUsername(DEFAULT_OVERWATCH_USERNAME)
            .profile(DEFAULT_PROFILE)
            .profileContentType(DEFAULT_PROFILE_CONTENT_TYPE)
            .bio(DEFAULT_BIO)
            .primaryLanguage(DEFAULT_PRIMARY_LANGUAGE)
            .device(DEFAULT_DEVICE)
            .matchesPlayed(DEFAULT_MATCHES_PLAYED)
            .tournamentsPlayed(DEFAULT_TOURNAMENTS_PLAYED)
            .matchWins(DEFAULT_MATCH_WINS)
            .tournamentWins(DEFAULT_TOURNAMENT_WINS)
            .tournamentTop10s(DEFAULT_TOURNAMENT_TOP_10_S);
        return playerData;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlayerData createUpdatedEntity(EntityManager em) {
        PlayerData playerData = new PlayerData()
            .name(UPDATED_NAME)
            .overwatchUsername(UPDATED_OVERWATCH_USERNAME)
            .profile(UPDATED_PROFILE)
            .profileContentType(UPDATED_PROFILE_CONTENT_TYPE)
            .bio(UPDATED_BIO)
            .primaryLanguage(UPDATED_PRIMARY_LANGUAGE)
            .device(UPDATED_DEVICE)
            .matchesPlayed(UPDATED_MATCHES_PLAYED)
            .tournamentsPlayed(UPDATED_TOURNAMENTS_PLAYED)
            .matchWins(UPDATED_MATCH_WINS)
            .tournamentWins(UPDATED_TOURNAMENT_WINS)
            .tournamentTop10s(UPDATED_TOURNAMENT_TOP_10_S);
        return playerData;
    }

    @BeforeEach
    public void initTest() {
        playerData = createEntity(em);
    }

    @Test
    @Transactional
    void createPlayerData() throws Exception {
        int databaseSizeBeforeCreate = playerDataRepository.findAll().size();
        // Create the PlayerData
        PlayerDataDTO playerDataDTO = playerDataMapper.toDto(playerData);
        restPlayerDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(playerDataDTO)))
            .andExpect(status().isCreated());

        // Validate the PlayerData in the database
        List<PlayerData> playerDataList = playerDataRepository.findAll();
        assertThat(playerDataList).hasSize(databaseSizeBeforeCreate + 1);
        PlayerData testPlayerData = playerDataList.get(playerDataList.size() - 1);
        assertThat(testPlayerData.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPlayerData.getOverwatchUsername()).isEqualTo(DEFAULT_OVERWATCH_USERNAME);
        assertThat(testPlayerData.getProfile()).isEqualTo(DEFAULT_PROFILE);
        assertThat(testPlayerData.getProfileContentType()).isEqualTo(DEFAULT_PROFILE_CONTENT_TYPE);
        assertThat(testPlayerData.getBio()).isEqualTo(DEFAULT_BIO);
        assertThat(testPlayerData.getPrimaryLanguage()).isEqualTo(DEFAULT_PRIMARY_LANGUAGE);
        assertThat(testPlayerData.getDevice()).isEqualTo(DEFAULT_DEVICE);
        assertThat(testPlayerData.getMatchesPlayed()).isEqualTo(DEFAULT_MATCHES_PLAYED);
        assertThat(testPlayerData.getTournamentsPlayed()).isEqualTo(DEFAULT_TOURNAMENTS_PLAYED);
        assertThat(testPlayerData.getMatchWins()).isEqualTo(DEFAULT_MATCH_WINS);
        assertThat(testPlayerData.getTournamentWins()).isEqualTo(DEFAULT_TOURNAMENT_WINS);
        assertThat(testPlayerData.getTournamentTop10s()).isEqualTo(DEFAULT_TOURNAMENT_TOP_10_S);
    }

    @Test
    @Transactional
    void createPlayerDataWithExistingId() throws Exception {
        // Create the PlayerData with an existing ID
        playerData.setId(1L);
        PlayerDataDTO playerDataDTO = playerDataMapper.toDto(playerData);

        int databaseSizeBeforeCreate = playerDataRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlayerDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(playerDataDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PlayerData in the database
        List<PlayerData> playerDataList = playerDataRepository.findAll();
        assertThat(playerDataList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = playerDataRepository.findAll().size();
        // set the field null
        playerData.setName(null);

        // Create the PlayerData, which fails.
        PlayerDataDTO playerDataDTO = playerDataMapper.toDto(playerData);

        restPlayerDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(playerDataDTO)))
            .andExpect(status().isBadRequest());

        List<PlayerData> playerDataList = playerDataRepository.findAll();
        assertThat(playerDataList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMatchesPlayedIsRequired() throws Exception {
        int databaseSizeBeforeTest = playerDataRepository.findAll().size();
        // set the field null
        playerData.setMatchesPlayed(null);

        // Create the PlayerData, which fails.
        PlayerDataDTO playerDataDTO = playerDataMapper.toDto(playerData);

        restPlayerDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(playerDataDTO)))
            .andExpect(status().isBadRequest());

        List<PlayerData> playerDataList = playerDataRepository.findAll();
        assertThat(playerDataList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTournamentsPlayedIsRequired() throws Exception {
        int databaseSizeBeforeTest = playerDataRepository.findAll().size();
        // set the field null
        playerData.setTournamentsPlayed(null);

        // Create the PlayerData, which fails.
        PlayerDataDTO playerDataDTO = playerDataMapper.toDto(playerData);

        restPlayerDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(playerDataDTO)))
            .andExpect(status().isBadRequest());

        List<PlayerData> playerDataList = playerDataRepository.findAll();
        assertThat(playerDataList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMatchWinsIsRequired() throws Exception {
        int databaseSizeBeforeTest = playerDataRepository.findAll().size();
        // set the field null
        playerData.setMatchWins(null);

        // Create the PlayerData, which fails.
        PlayerDataDTO playerDataDTO = playerDataMapper.toDto(playerData);

        restPlayerDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(playerDataDTO)))
            .andExpect(status().isBadRequest());

        List<PlayerData> playerDataList = playerDataRepository.findAll();
        assertThat(playerDataList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTournamentWinsIsRequired() throws Exception {
        int databaseSizeBeforeTest = playerDataRepository.findAll().size();
        // set the field null
        playerData.setTournamentWins(null);

        // Create the PlayerData, which fails.
        PlayerDataDTO playerDataDTO = playerDataMapper.toDto(playerData);

        restPlayerDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(playerDataDTO)))
            .andExpect(status().isBadRequest());

        List<PlayerData> playerDataList = playerDataRepository.findAll();
        assertThat(playerDataList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPlayerData() throws Exception {
        // Initialize the database
        playerDataRepository.saveAndFlush(playerData);

        // Get all the playerDataList
        restPlayerDataMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(playerData.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].overwatchUsername").value(hasItem(DEFAULT_OVERWATCH_USERNAME)))
            .andExpect(jsonPath("$.[*].profileContentType").value(hasItem(DEFAULT_PROFILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].profile").value(hasItem(Base64Utils.encodeToString(DEFAULT_PROFILE))))
            .andExpect(jsonPath("$.[*].bio").value(hasItem(DEFAULT_BIO)))
            .andExpect(jsonPath("$.[*].primaryLanguage").value(hasItem(DEFAULT_PRIMARY_LANGUAGE.toString())))
            .andExpect(jsonPath("$.[*].device").value(hasItem(DEFAULT_DEVICE.toString())))
            .andExpect(jsonPath("$.[*].matchesPlayed").value(hasItem(DEFAULT_MATCHES_PLAYED)))
            .andExpect(jsonPath("$.[*].tournamentsPlayed").value(hasItem(DEFAULT_TOURNAMENTS_PLAYED)))
            .andExpect(jsonPath("$.[*].matchWins").value(hasItem(DEFAULT_MATCH_WINS)))
            .andExpect(jsonPath("$.[*].tournamentWins").value(hasItem(DEFAULT_TOURNAMENT_WINS)))
            .andExpect(jsonPath("$.[*].tournamentTop10s").value(hasItem(DEFAULT_TOURNAMENT_TOP_10_S)));
    }

    @Test
    @Transactional
    void getPlayerData() throws Exception {
        // Initialize the database
        playerDataRepository.saveAndFlush(playerData);

        // Get the playerData
        restPlayerDataMockMvc
            .perform(get(ENTITY_API_URL_ID, playerData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(playerData.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.overwatchUsername").value(DEFAULT_OVERWATCH_USERNAME))
            .andExpect(jsonPath("$.profileContentType").value(DEFAULT_PROFILE_CONTENT_TYPE))
            .andExpect(jsonPath("$.profile").value(Base64Utils.encodeToString(DEFAULT_PROFILE)))
            .andExpect(jsonPath("$.bio").value(DEFAULT_BIO))
            .andExpect(jsonPath("$.primaryLanguage").value(DEFAULT_PRIMARY_LANGUAGE.toString()))
            .andExpect(jsonPath("$.device").value(DEFAULT_DEVICE.toString()))
            .andExpect(jsonPath("$.matchesPlayed").value(DEFAULT_MATCHES_PLAYED))
            .andExpect(jsonPath("$.tournamentsPlayed").value(DEFAULT_TOURNAMENTS_PLAYED))
            .andExpect(jsonPath("$.matchWins").value(DEFAULT_MATCH_WINS))
            .andExpect(jsonPath("$.tournamentWins").value(DEFAULT_TOURNAMENT_WINS))
            .andExpect(jsonPath("$.tournamentTop10s").value(DEFAULT_TOURNAMENT_TOP_10_S));
    }

    @Test
    @Transactional
    void getNonExistingPlayerData() throws Exception {
        // Get the playerData
        restPlayerDataMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPlayerData() throws Exception {
        // Initialize the database
        playerDataRepository.saveAndFlush(playerData);

        int databaseSizeBeforeUpdate = playerDataRepository.findAll().size();

        // Update the playerData
        PlayerData updatedPlayerData = playerDataRepository.findById(playerData.getId()).get();
        // Disconnect from session so that the updates on updatedPlayerData are not directly saved in db
        em.detach(updatedPlayerData);
        updatedPlayerData
            .name(UPDATED_NAME)
            .overwatchUsername(UPDATED_OVERWATCH_USERNAME)
            .profile(UPDATED_PROFILE)
            .profileContentType(UPDATED_PROFILE_CONTENT_TYPE)
            .bio(UPDATED_BIO)
            .primaryLanguage(UPDATED_PRIMARY_LANGUAGE)
            .device(UPDATED_DEVICE)
            .matchesPlayed(UPDATED_MATCHES_PLAYED)
            .tournamentsPlayed(UPDATED_TOURNAMENTS_PLAYED)
            .matchWins(UPDATED_MATCH_WINS)
            .tournamentWins(UPDATED_TOURNAMENT_WINS)
            .tournamentTop10s(UPDATED_TOURNAMENT_TOP_10_S);
        PlayerDataDTO playerDataDTO = playerDataMapper.toDto(updatedPlayerData);

        restPlayerDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, playerDataDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(playerDataDTO))
            )
            .andExpect(status().isOk());

        // Validate the PlayerData in the database
        List<PlayerData> playerDataList = playerDataRepository.findAll();
        assertThat(playerDataList).hasSize(databaseSizeBeforeUpdate);
        PlayerData testPlayerData = playerDataList.get(playerDataList.size() - 1);
        assertThat(testPlayerData.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPlayerData.getOverwatchUsername()).isEqualTo(UPDATED_OVERWATCH_USERNAME);
        assertThat(testPlayerData.getProfile()).isEqualTo(UPDATED_PROFILE);
        assertThat(testPlayerData.getProfileContentType()).isEqualTo(UPDATED_PROFILE_CONTENT_TYPE);
        assertThat(testPlayerData.getBio()).isEqualTo(UPDATED_BIO);
        assertThat(testPlayerData.getPrimaryLanguage()).isEqualTo(UPDATED_PRIMARY_LANGUAGE);
        assertThat(testPlayerData.getDevice()).isEqualTo(UPDATED_DEVICE);
        assertThat(testPlayerData.getMatchesPlayed()).isEqualTo(UPDATED_MATCHES_PLAYED);
        assertThat(testPlayerData.getTournamentsPlayed()).isEqualTo(UPDATED_TOURNAMENTS_PLAYED);
        assertThat(testPlayerData.getMatchWins()).isEqualTo(UPDATED_MATCH_WINS);
        assertThat(testPlayerData.getTournamentWins()).isEqualTo(UPDATED_TOURNAMENT_WINS);
        assertThat(testPlayerData.getTournamentTop10s()).isEqualTo(UPDATED_TOURNAMENT_TOP_10_S);
    }

    @Test
    @Transactional
    void putNonExistingPlayerData() throws Exception {
        int databaseSizeBeforeUpdate = playerDataRepository.findAll().size();
        playerData.setId(count.incrementAndGet());

        // Create the PlayerData
        PlayerDataDTO playerDataDTO = playerDataMapper.toDto(playerData);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlayerDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, playerDataDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(playerDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlayerData in the database
        List<PlayerData> playerDataList = playerDataRepository.findAll();
        assertThat(playerDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPlayerData() throws Exception {
        int databaseSizeBeforeUpdate = playerDataRepository.findAll().size();
        playerData.setId(count.incrementAndGet());

        // Create the PlayerData
        PlayerDataDTO playerDataDTO = playerDataMapper.toDto(playerData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlayerDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(playerDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlayerData in the database
        List<PlayerData> playerDataList = playerDataRepository.findAll();
        assertThat(playerDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPlayerData() throws Exception {
        int databaseSizeBeforeUpdate = playerDataRepository.findAll().size();
        playerData.setId(count.incrementAndGet());

        // Create the PlayerData
        PlayerDataDTO playerDataDTO = playerDataMapper.toDto(playerData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlayerDataMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(playerDataDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PlayerData in the database
        List<PlayerData> playerDataList = playerDataRepository.findAll();
        assertThat(playerDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePlayerDataWithPatch() throws Exception {
        // Initialize the database
        playerDataRepository.saveAndFlush(playerData);

        int databaseSizeBeforeUpdate = playerDataRepository.findAll().size();

        // Update the playerData using partial update
        PlayerData partialUpdatedPlayerData = new PlayerData();
        partialUpdatedPlayerData.setId(playerData.getId());

        partialUpdatedPlayerData
            .name(UPDATED_NAME)
            .overwatchUsername(UPDATED_OVERWATCH_USERNAME)
            .device(UPDATED_DEVICE)
            .matchesPlayed(UPDATED_MATCHES_PLAYED)
            .tournamentsPlayed(UPDATED_TOURNAMENTS_PLAYED)
            .tournamentWins(UPDATED_TOURNAMENT_WINS)
            .tournamentTop10s(UPDATED_TOURNAMENT_TOP_10_S);

        restPlayerDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlayerData.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlayerData))
            )
            .andExpect(status().isOk());

        // Validate the PlayerData in the database
        List<PlayerData> playerDataList = playerDataRepository.findAll();
        assertThat(playerDataList).hasSize(databaseSizeBeforeUpdate);
        PlayerData testPlayerData = playerDataList.get(playerDataList.size() - 1);
        assertThat(testPlayerData.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPlayerData.getOverwatchUsername()).isEqualTo(UPDATED_OVERWATCH_USERNAME);
        assertThat(testPlayerData.getProfile()).isEqualTo(DEFAULT_PROFILE);
        assertThat(testPlayerData.getProfileContentType()).isEqualTo(DEFAULT_PROFILE_CONTENT_TYPE);
        assertThat(testPlayerData.getBio()).isEqualTo(DEFAULT_BIO);
        assertThat(testPlayerData.getPrimaryLanguage()).isEqualTo(DEFAULT_PRIMARY_LANGUAGE);
        assertThat(testPlayerData.getDevice()).isEqualTo(UPDATED_DEVICE);
        assertThat(testPlayerData.getMatchesPlayed()).isEqualTo(UPDATED_MATCHES_PLAYED);
        assertThat(testPlayerData.getTournamentsPlayed()).isEqualTo(UPDATED_TOURNAMENTS_PLAYED);
        assertThat(testPlayerData.getMatchWins()).isEqualTo(DEFAULT_MATCH_WINS);
        assertThat(testPlayerData.getTournamentWins()).isEqualTo(UPDATED_TOURNAMENT_WINS);
        assertThat(testPlayerData.getTournamentTop10s()).isEqualTo(UPDATED_TOURNAMENT_TOP_10_S);
    }

    @Test
    @Transactional
    void fullUpdatePlayerDataWithPatch() throws Exception {
        // Initialize the database
        playerDataRepository.saveAndFlush(playerData);

        int databaseSizeBeforeUpdate = playerDataRepository.findAll().size();

        // Update the playerData using partial update
        PlayerData partialUpdatedPlayerData = new PlayerData();
        partialUpdatedPlayerData.setId(playerData.getId());

        partialUpdatedPlayerData
            .name(UPDATED_NAME)
            .overwatchUsername(UPDATED_OVERWATCH_USERNAME)
            .profile(UPDATED_PROFILE)
            .profileContentType(UPDATED_PROFILE_CONTENT_TYPE)
            .bio(UPDATED_BIO)
            .primaryLanguage(UPDATED_PRIMARY_LANGUAGE)
            .device(UPDATED_DEVICE)
            .matchesPlayed(UPDATED_MATCHES_PLAYED)
            .tournamentsPlayed(UPDATED_TOURNAMENTS_PLAYED)
            .matchWins(UPDATED_MATCH_WINS)
            .tournamentWins(UPDATED_TOURNAMENT_WINS)
            .tournamentTop10s(UPDATED_TOURNAMENT_TOP_10_S);

        restPlayerDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlayerData.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlayerData))
            )
            .andExpect(status().isOk());

        // Validate the PlayerData in the database
        List<PlayerData> playerDataList = playerDataRepository.findAll();
        assertThat(playerDataList).hasSize(databaseSizeBeforeUpdate);
        PlayerData testPlayerData = playerDataList.get(playerDataList.size() - 1);
        assertThat(testPlayerData.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPlayerData.getOverwatchUsername()).isEqualTo(UPDATED_OVERWATCH_USERNAME);
        assertThat(testPlayerData.getProfile()).isEqualTo(UPDATED_PROFILE);
        assertThat(testPlayerData.getProfileContentType()).isEqualTo(UPDATED_PROFILE_CONTENT_TYPE);
        assertThat(testPlayerData.getBio()).isEqualTo(UPDATED_BIO);
        assertThat(testPlayerData.getPrimaryLanguage()).isEqualTo(UPDATED_PRIMARY_LANGUAGE);
        assertThat(testPlayerData.getDevice()).isEqualTo(UPDATED_DEVICE);
        assertThat(testPlayerData.getMatchesPlayed()).isEqualTo(UPDATED_MATCHES_PLAYED);
        assertThat(testPlayerData.getTournamentsPlayed()).isEqualTo(UPDATED_TOURNAMENTS_PLAYED);
        assertThat(testPlayerData.getMatchWins()).isEqualTo(UPDATED_MATCH_WINS);
        assertThat(testPlayerData.getTournamentWins()).isEqualTo(UPDATED_TOURNAMENT_WINS);
        assertThat(testPlayerData.getTournamentTop10s()).isEqualTo(UPDATED_TOURNAMENT_TOP_10_S);
    }

    @Test
    @Transactional
    void patchNonExistingPlayerData() throws Exception {
        int databaseSizeBeforeUpdate = playerDataRepository.findAll().size();
        playerData.setId(count.incrementAndGet());

        // Create the PlayerData
        PlayerDataDTO playerDataDTO = playerDataMapper.toDto(playerData);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlayerDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, playerDataDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(playerDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlayerData in the database
        List<PlayerData> playerDataList = playerDataRepository.findAll();
        assertThat(playerDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPlayerData() throws Exception {
        int databaseSizeBeforeUpdate = playerDataRepository.findAll().size();
        playerData.setId(count.incrementAndGet());

        // Create the PlayerData
        PlayerDataDTO playerDataDTO = playerDataMapper.toDto(playerData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlayerDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(playerDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlayerData in the database
        List<PlayerData> playerDataList = playerDataRepository.findAll();
        assertThat(playerDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPlayerData() throws Exception {
        int databaseSizeBeforeUpdate = playerDataRepository.findAll().size();
        playerData.setId(count.incrementAndGet());

        // Create the PlayerData
        PlayerDataDTO playerDataDTO = playerDataMapper.toDto(playerData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlayerDataMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(playerDataDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PlayerData in the database
        List<PlayerData> playerDataList = playerDataRepository.findAll();
        assertThat(playerDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePlayerData() throws Exception {
        // Initialize the database
        playerDataRepository.saveAndFlush(playerData);

        int databaseSizeBeforeDelete = playerDataRepository.findAll().size();

        // Delete the playerData
        restPlayerDataMockMvc
            .perform(delete(ENTITY_API_URL_ID, playerData.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PlayerData> playerDataList = playerDataRepository.findAll();
        assertThat(playerDataList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
