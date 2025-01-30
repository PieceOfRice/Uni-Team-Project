package team.bham.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static team.bham.web.rest.TestUtil.sameInstant;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
import team.bham.domain.Tournament;
import team.bham.domain.enumeration.AccessStatus;
import team.bham.domain.enumeration.TournamentBracketType;
import team.bham.domain.enumeration.TournamentSetting;
import team.bham.repository.TournamentRepository;
import team.bham.service.dto.TournamentDTO;
import team.bham.service.mapper.TournamentMapper;

/**
 * Integration tests for the {@link TournamentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TournamentResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Float DEFAULT_PRIZE_POOL = 1F;
    private static final Float UPDATED_PRIZE_POOL = 2F;

    private static final Float DEFAULT_ENTRY_FEE = 1F;
    private static final Float UPDATED_ENTRY_FEE = 2F;

    private static final ZonedDateTime DEFAULT_START_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_END_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBB";

    private static final TournamentBracketType DEFAULT_BRACKET_FORMAT = TournamentBracketType.SINGLE_ELIMINATION;
    private static final TournamentBracketType UPDATED_BRACKET_FORMAT = TournamentBracketType.DOUBLE_ELIMINATION;

    private static final AccessStatus DEFAULT_ACCESS_STATUS = AccessStatus.PUBLIC;
    private static final AccessStatus UPDATED_ACCESS_STATUS = AccessStatus.PRIVATE;

    private static final Boolean DEFAULT_IS_LIVE = false;
    private static final Boolean UPDATED_IS_LIVE = true;

    private static final Boolean DEFAULT_ENDED = false;
    private static final Boolean UPDATED_ENDED = true;

    private static final byte[] DEFAULT_BANNER = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_BANNER = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_BANNER_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_BANNER_CONTENT_TYPE = "image/png";

    private static final Integer DEFAULT_GAMES_PER_MATCH = 1;
    private static final Integer UPDATED_GAMES_PER_MATCH = 2;

    private static final Integer DEFAULT_MAX_PARTICIPANTS = 1;
    private static final Integer UPDATED_MAX_PARTICIPANTS = 2;

    private static final TournamentSetting DEFAULT_TOURNAMENT_SETTING = TournamentSetting.IN_PERSON;
    private static final TournamentSetting UPDATED_TOURNAMENT_SETTING = TournamentSetting.ONLINE;

    private static final String ENTITY_API_URL = "/api/tournaments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TournamentMapper tournamentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTournamentMockMvc;

    private Tournament tournament;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tournament createEntity(EntityManager em) {
        Tournament tournament = new Tournament()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .prizePool(DEFAULT_PRIZE_POOL)
            .entryFee(DEFAULT_ENTRY_FEE)
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME)
            .location(DEFAULT_LOCATION)
            .bracketFormat(DEFAULT_BRACKET_FORMAT)
            .accessStatus(DEFAULT_ACCESS_STATUS)
            .isLive(DEFAULT_IS_LIVE)
            .ended(DEFAULT_ENDED)
            .banner(DEFAULT_BANNER)
            .bannerContentType(DEFAULT_BANNER_CONTENT_TYPE)
            .gamesPerMatch(DEFAULT_GAMES_PER_MATCH)
            .maxParticipants(DEFAULT_MAX_PARTICIPANTS)
            .tournamentSetting(DEFAULT_TOURNAMENT_SETTING);
        return tournament;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tournament createUpdatedEntity(EntityManager em) {
        Tournament tournament = new Tournament()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .prizePool(UPDATED_PRIZE_POOL)
            .entryFee(UPDATED_ENTRY_FEE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .location(UPDATED_LOCATION)
            .bracketFormat(UPDATED_BRACKET_FORMAT)
            .accessStatus(UPDATED_ACCESS_STATUS)
            .isLive(UPDATED_IS_LIVE)
            .ended(UPDATED_ENDED)
            .banner(UPDATED_BANNER)
            .bannerContentType(UPDATED_BANNER_CONTENT_TYPE)
            .gamesPerMatch(UPDATED_GAMES_PER_MATCH)
            .maxParticipants(UPDATED_MAX_PARTICIPANTS)
            .tournamentSetting(UPDATED_TOURNAMENT_SETTING);
        return tournament;
    }

    @BeforeEach
    public void initTest() {
        tournament = createEntity(em);
    }

    @Test
    @Transactional
    void createTournament() throws Exception {
        int databaseSizeBeforeCreate = tournamentRepository.findAll().size();
        // Create the Tournament
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);
        restTournamentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tournamentDTO)))
            .andExpect(status().isCreated());

        // Validate the Tournament in the database
        List<Tournament> tournamentList = tournamentRepository.findAll();
        assertThat(tournamentList).hasSize(databaseSizeBeforeCreate + 1);
        Tournament testTournament = tournamentList.get(tournamentList.size() - 1);
        assertThat(testTournament.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTournament.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTournament.getPrizePool()).isEqualTo(DEFAULT_PRIZE_POOL);
        assertThat(testTournament.getEntryFee()).isEqualTo(DEFAULT_ENTRY_FEE);
        assertThat(testTournament.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testTournament.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testTournament.getLocation()).isEqualTo(DEFAULT_LOCATION);
        assertThat(testTournament.getBracketFormat()).isEqualTo(DEFAULT_BRACKET_FORMAT);
        assertThat(testTournament.getAccessStatus()).isEqualTo(DEFAULT_ACCESS_STATUS);
        assertThat(testTournament.getIsLive()).isEqualTo(DEFAULT_IS_LIVE);
        assertThat(testTournament.getEnded()).isEqualTo(DEFAULT_ENDED);
        assertThat(testTournament.getBanner()).isEqualTo(DEFAULT_BANNER);
        assertThat(testTournament.getBannerContentType()).isEqualTo(DEFAULT_BANNER_CONTENT_TYPE);
        assertThat(testTournament.getGamesPerMatch()).isEqualTo(DEFAULT_GAMES_PER_MATCH);
        assertThat(testTournament.getMaxParticipants()).isEqualTo(DEFAULT_MAX_PARTICIPANTS);
        assertThat(testTournament.getTournamentSetting()).isEqualTo(DEFAULT_TOURNAMENT_SETTING);
    }

    @Test
    @Transactional
    void createTournamentWithExistingId() throws Exception {
        // Create the Tournament with an existing ID
        tournament.setId(1L);
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);

        int databaseSizeBeforeCreate = tournamentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTournamentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tournamentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Tournament in the database
        List<Tournament> tournamentList = tournamentRepository.findAll();
        assertThat(tournamentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = tournamentRepository.findAll().size();
        // set the field null
        tournament.setName(null);

        // Create the Tournament, which fails.
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);

        restTournamentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tournamentDTO)))
            .andExpect(status().isBadRequest());

        List<Tournament> tournamentList = tournamentRepository.findAll();
        assertThat(tournamentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = tournamentRepository.findAll().size();
        // set the field null
        tournament.setStartTime(null);

        // Create the Tournament, which fails.
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);

        restTournamentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tournamentDTO)))
            .andExpect(status().isBadRequest());

        List<Tournament> tournamentList = tournamentRepository.findAll();
        assertThat(tournamentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAccessStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = tournamentRepository.findAll().size();
        // set the field null
        tournament.setAccessStatus(null);

        // Create the Tournament, which fails.
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);

        restTournamentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tournamentDTO)))
            .andExpect(status().isBadRequest());

        List<Tournament> tournamentList = tournamentRepository.findAll();
        assertThat(tournamentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsLiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = tournamentRepository.findAll().size();
        // set the field null
        tournament.setIsLive(null);

        // Create the Tournament, which fails.
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);

        restTournamentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tournamentDTO)))
            .andExpect(status().isBadRequest());

        List<Tournament> tournamentList = tournamentRepository.findAll();
        assertThat(tournamentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndedIsRequired() throws Exception {
        int databaseSizeBeforeTest = tournamentRepository.findAll().size();
        // set the field null
        tournament.setEnded(null);

        // Create the Tournament, which fails.
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);

        restTournamentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tournamentDTO)))
            .andExpect(status().isBadRequest());

        List<Tournament> tournamentList = tournamentRepository.findAll();
        assertThat(tournamentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGamesPerMatchIsRequired() throws Exception {
        int databaseSizeBeforeTest = tournamentRepository.findAll().size();
        // set the field null
        tournament.setGamesPerMatch(null);

        // Create the Tournament, which fails.
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);

        restTournamentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tournamentDTO)))
            .andExpect(status().isBadRequest());

        List<Tournament> tournamentList = tournamentRepository.findAll();
        assertThat(tournamentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTournamentSettingIsRequired() throws Exception {
        int databaseSizeBeforeTest = tournamentRepository.findAll().size();
        // set the field null
        tournament.setTournamentSetting(null);

        // Create the Tournament, which fails.
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);

        restTournamentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tournamentDTO)))
            .andExpect(status().isBadRequest());

        List<Tournament> tournamentList = tournamentRepository.findAll();
        assertThat(tournamentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTournaments() throws Exception {
        // Initialize the database
        tournamentRepository.saveAndFlush(tournament);

        // Get all the tournamentList
        restTournamentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tournament.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].prizePool").value(hasItem(DEFAULT_PRIZE_POOL.doubleValue())))
            .andExpect(jsonPath("$.[*].entryFee").value(hasItem(DEFAULT_ENTRY_FEE.doubleValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(sameInstant(DEFAULT_START_TIME))))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(sameInstant(DEFAULT_END_TIME))))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION)))
            .andExpect(jsonPath("$.[*].bracketFormat").value(hasItem(DEFAULT_BRACKET_FORMAT.toString())))
            .andExpect(jsonPath("$.[*].accessStatus").value(hasItem(DEFAULT_ACCESS_STATUS.toString())))
            .andExpect(jsonPath("$.[*].isLive").value(hasItem(DEFAULT_IS_LIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].ended").value(hasItem(DEFAULT_ENDED.booleanValue())))
            .andExpect(jsonPath("$.[*].bannerContentType").value(hasItem(DEFAULT_BANNER_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].banner").value(hasItem(Base64Utils.encodeToString(DEFAULT_BANNER))))
            .andExpect(jsonPath("$.[*].gamesPerMatch").value(hasItem(DEFAULT_GAMES_PER_MATCH)))
            .andExpect(jsonPath("$.[*].maxParticipants").value(hasItem(DEFAULT_MAX_PARTICIPANTS)))
            .andExpect(jsonPath("$.[*].tournamentSetting").value(hasItem(DEFAULT_TOURNAMENT_SETTING.toString())));
    }

    @Test
    @Transactional
    void getTournament() throws Exception {
        // Initialize the database
        tournamentRepository.saveAndFlush(tournament);

        // Get the tournament
        restTournamentMockMvc
            .perform(get(ENTITY_API_URL_ID, tournament.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tournament.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.prizePool").value(DEFAULT_PRIZE_POOL.doubleValue()))
            .andExpect(jsonPath("$.entryFee").value(DEFAULT_ENTRY_FEE.doubleValue()))
            .andExpect(jsonPath("$.startTime").value(sameInstant(DEFAULT_START_TIME)))
            .andExpect(jsonPath("$.endTime").value(sameInstant(DEFAULT_END_TIME)))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION))
            .andExpect(jsonPath("$.bracketFormat").value(DEFAULT_BRACKET_FORMAT.toString()))
            .andExpect(jsonPath("$.accessStatus").value(DEFAULT_ACCESS_STATUS.toString()))
            .andExpect(jsonPath("$.isLive").value(DEFAULT_IS_LIVE.booleanValue()))
            .andExpect(jsonPath("$.ended").value(DEFAULT_ENDED.booleanValue()))
            .andExpect(jsonPath("$.bannerContentType").value(DEFAULT_BANNER_CONTENT_TYPE))
            .andExpect(jsonPath("$.banner").value(Base64Utils.encodeToString(DEFAULT_BANNER)))
            .andExpect(jsonPath("$.gamesPerMatch").value(DEFAULT_GAMES_PER_MATCH))
            .andExpect(jsonPath("$.maxParticipants").value(DEFAULT_MAX_PARTICIPANTS))
            .andExpect(jsonPath("$.tournamentSetting").value(DEFAULT_TOURNAMENT_SETTING.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTournament() throws Exception {
        // Get the tournament
        restTournamentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTournament() throws Exception {
        // Initialize the database
        tournamentRepository.saveAndFlush(tournament);

        int databaseSizeBeforeUpdate = tournamentRepository.findAll().size();

        // Update the tournament
        Tournament updatedTournament = tournamentRepository.findById(tournament.getId()).get();
        // Disconnect from session so that the updates on updatedTournament are not directly saved in db
        em.detach(updatedTournament);
        updatedTournament
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .prizePool(UPDATED_PRIZE_POOL)
            .entryFee(UPDATED_ENTRY_FEE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .location(UPDATED_LOCATION)
            .bracketFormat(UPDATED_BRACKET_FORMAT)
            .accessStatus(UPDATED_ACCESS_STATUS)
            .isLive(UPDATED_IS_LIVE)
            .ended(UPDATED_ENDED)
            .banner(UPDATED_BANNER)
            .bannerContentType(UPDATED_BANNER_CONTENT_TYPE)
            .gamesPerMatch(UPDATED_GAMES_PER_MATCH)
            .maxParticipants(UPDATED_MAX_PARTICIPANTS)
            .tournamentSetting(UPDATED_TOURNAMENT_SETTING);
        TournamentDTO tournamentDTO = tournamentMapper.toDto(updatedTournament);

        restTournamentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tournamentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tournamentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Tournament in the database
        List<Tournament> tournamentList = tournamentRepository.findAll();
        assertThat(tournamentList).hasSize(databaseSizeBeforeUpdate);
        Tournament testTournament = tournamentList.get(tournamentList.size() - 1);
        assertThat(testTournament.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTournament.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTournament.getPrizePool()).isEqualTo(UPDATED_PRIZE_POOL);
        assertThat(testTournament.getEntryFee()).isEqualTo(UPDATED_ENTRY_FEE);
        assertThat(testTournament.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testTournament.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testTournament.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testTournament.getBracketFormat()).isEqualTo(UPDATED_BRACKET_FORMAT);
        assertThat(testTournament.getAccessStatus()).isEqualTo(UPDATED_ACCESS_STATUS);
        assertThat(testTournament.getIsLive()).isEqualTo(UPDATED_IS_LIVE);
        assertThat(testTournament.getEnded()).isEqualTo(UPDATED_ENDED);
        assertThat(testTournament.getBanner()).isEqualTo(UPDATED_BANNER);
        assertThat(testTournament.getBannerContentType()).isEqualTo(UPDATED_BANNER_CONTENT_TYPE);
        assertThat(testTournament.getGamesPerMatch()).isEqualTo(UPDATED_GAMES_PER_MATCH);
        assertThat(testTournament.getMaxParticipants()).isEqualTo(UPDATED_MAX_PARTICIPANTS);
        assertThat(testTournament.getTournamentSetting()).isEqualTo(UPDATED_TOURNAMENT_SETTING);
    }

    @Test
    @Transactional
    void putNonExistingTournament() throws Exception {
        int databaseSizeBeforeUpdate = tournamentRepository.findAll().size();
        tournament.setId(count.incrementAndGet());

        // Create the Tournament
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTournamentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tournamentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tournamentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tournament in the database
        List<Tournament> tournamentList = tournamentRepository.findAll();
        assertThat(tournamentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTournament() throws Exception {
        int databaseSizeBeforeUpdate = tournamentRepository.findAll().size();
        tournament.setId(count.incrementAndGet());

        // Create the Tournament
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTournamentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tournamentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tournament in the database
        List<Tournament> tournamentList = tournamentRepository.findAll();
        assertThat(tournamentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTournament() throws Exception {
        int databaseSizeBeforeUpdate = tournamentRepository.findAll().size();
        tournament.setId(count.incrementAndGet());

        // Create the Tournament
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTournamentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tournamentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tournament in the database
        List<Tournament> tournamentList = tournamentRepository.findAll();
        assertThat(tournamentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTournamentWithPatch() throws Exception {
        // Initialize the database
        tournamentRepository.saveAndFlush(tournament);

        int databaseSizeBeforeUpdate = tournamentRepository.findAll().size();

        // Update the tournament using partial update
        Tournament partialUpdatedTournament = new Tournament();
        partialUpdatedTournament.setId(tournament.getId());

        partialUpdatedTournament
            .description(UPDATED_DESCRIPTION)
            .endTime(UPDATED_END_TIME)
            .location(UPDATED_LOCATION)
            .bracketFormat(UPDATED_BRACKET_FORMAT)
            .accessStatus(UPDATED_ACCESS_STATUS)
            .ended(UPDATED_ENDED)
            .gamesPerMatch(UPDATED_GAMES_PER_MATCH)
            .maxParticipants(UPDATED_MAX_PARTICIPANTS);

        restTournamentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTournament.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTournament))
            )
            .andExpect(status().isOk());

        // Validate the Tournament in the database
        List<Tournament> tournamentList = tournamentRepository.findAll();
        assertThat(tournamentList).hasSize(databaseSizeBeforeUpdate);
        Tournament testTournament = tournamentList.get(tournamentList.size() - 1);
        assertThat(testTournament.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTournament.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTournament.getPrizePool()).isEqualTo(DEFAULT_PRIZE_POOL);
        assertThat(testTournament.getEntryFee()).isEqualTo(DEFAULT_ENTRY_FEE);
        assertThat(testTournament.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testTournament.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testTournament.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testTournament.getBracketFormat()).isEqualTo(UPDATED_BRACKET_FORMAT);
        assertThat(testTournament.getAccessStatus()).isEqualTo(UPDATED_ACCESS_STATUS);
        assertThat(testTournament.getIsLive()).isEqualTo(DEFAULT_IS_LIVE);
        assertThat(testTournament.getEnded()).isEqualTo(UPDATED_ENDED);
        assertThat(testTournament.getBanner()).isEqualTo(DEFAULT_BANNER);
        assertThat(testTournament.getBannerContentType()).isEqualTo(DEFAULT_BANNER_CONTENT_TYPE);
        assertThat(testTournament.getGamesPerMatch()).isEqualTo(UPDATED_GAMES_PER_MATCH);
        assertThat(testTournament.getMaxParticipants()).isEqualTo(UPDATED_MAX_PARTICIPANTS);
        assertThat(testTournament.getTournamentSetting()).isEqualTo(DEFAULT_TOURNAMENT_SETTING);
    }

    @Test
    @Transactional
    void fullUpdateTournamentWithPatch() throws Exception {
        // Initialize the database
        tournamentRepository.saveAndFlush(tournament);

        int databaseSizeBeforeUpdate = tournamentRepository.findAll().size();

        // Update the tournament using partial update
        Tournament partialUpdatedTournament = new Tournament();
        partialUpdatedTournament.setId(tournament.getId());

        partialUpdatedTournament
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .prizePool(UPDATED_PRIZE_POOL)
            .entryFee(UPDATED_ENTRY_FEE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .location(UPDATED_LOCATION)
            .bracketFormat(UPDATED_BRACKET_FORMAT)
            .accessStatus(UPDATED_ACCESS_STATUS)
            .isLive(UPDATED_IS_LIVE)
            .ended(UPDATED_ENDED)
            .banner(UPDATED_BANNER)
            .bannerContentType(UPDATED_BANNER_CONTENT_TYPE)
            .gamesPerMatch(UPDATED_GAMES_PER_MATCH)
            .maxParticipants(UPDATED_MAX_PARTICIPANTS)
            .tournamentSetting(UPDATED_TOURNAMENT_SETTING);

        restTournamentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTournament.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTournament))
            )
            .andExpect(status().isOk());

        // Validate the Tournament in the database
        List<Tournament> tournamentList = tournamentRepository.findAll();
        assertThat(tournamentList).hasSize(databaseSizeBeforeUpdate);
        Tournament testTournament = tournamentList.get(tournamentList.size() - 1);
        assertThat(testTournament.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTournament.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTournament.getPrizePool()).isEqualTo(UPDATED_PRIZE_POOL);
        assertThat(testTournament.getEntryFee()).isEqualTo(UPDATED_ENTRY_FEE);
        assertThat(testTournament.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testTournament.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testTournament.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testTournament.getBracketFormat()).isEqualTo(UPDATED_BRACKET_FORMAT);
        assertThat(testTournament.getAccessStatus()).isEqualTo(UPDATED_ACCESS_STATUS);
        assertThat(testTournament.getIsLive()).isEqualTo(UPDATED_IS_LIVE);
        assertThat(testTournament.getEnded()).isEqualTo(UPDATED_ENDED);
        assertThat(testTournament.getBanner()).isEqualTo(UPDATED_BANNER);
        assertThat(testTournament.getBannerContentType()).isEqualTo(UPDATED_BANNER_CONTENT_TYPE);
        assertThat(testTournament.getGamesPerMatch()).isEqualTo(UPDATED_GAMES_PER_MATCH);
        assertThat(testTournament.getMaxParticipants()).isEqualTo(UPDATED_MAX_PARTICIPANTS);
        assertThat(testTournament.getTournamentSetting()).isEqualTo(UPDATED_TOURNAMENT_SETTING);
    }

    @Test
    @Transactional
    void patchNonExistingTournament() throws Exception {
        int databaseSizeBeforeUpdate = tournamentRepository.findAll().size();
        tournament.setId(count.incrementAndGet());

        // Create the Tournament
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTournamentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tournamentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tournamentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tournament in the database
        List<Tournament> tournamentList = tournamentRepository.findAll();
        assertThat(tournamentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTournament() throws Exception {
        int databaseSizeBeforeUpdate = tournamentRepository.findAll().size();
        tournament.setId(count.incrementAndGet());

        // Create the Tournament
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTournamentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tournamentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tournament in the database
        List<Tournament> tournamentList = tournamentRepository.findAll();
        assertThat(tournamentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTournament() throws Exception {
        int databaseSizeBeforeUpdate = tournamentRepository.findAll().size();
        tournament.setId(count.incrementAndGet());

        // Create the Tournament
        TournamentDTO tournamentDTO = tournamentMapper.toDto(tournament);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTournamentMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(tournamentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tournament in the database
        List<Tournament> tournamentList = tournamentRepository.findAll();
        assertThat(tournamentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTournament() throws Exception {
        // Initialize the database
        tournamentRepository.saveAndFlush(tournament);

        int databaseSizeBeforeDelete = tournamentRepository.findAll().size();

        // Delete the tournament
        restTournamentMockMvc
            .perform(delete(ENTITY_API_URL_ID, tournament.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Tournament> tournamentList = tournamentRepository.findAll();
        assertThat(tournamentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
