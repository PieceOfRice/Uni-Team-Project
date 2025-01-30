package team.bham.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import team.bham.domain.Match;
import team.bham.repository.MatchRepository;
import team.bham.service.dto.MatchDTO;
import team.bham.service.mapper.MatchMapper;

/**
 * Integration tests for the {@link MatchResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MatchResourceIT {

    private static final Integer DEFAULT_MATCH_INDEX = 1;
    private static final Integer UPDATED_MATCH_INDEX = 2;

    private static final Integer DEFAULT_ONE_SCORE = 0;
    private static final Integer UPDATED_ONE_SCORE = 1;

    private static final Integer DEFAULT_TWO_SCORE = 0;
    private static final Integer UPDATED_TWO_SCORE = 1;

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_SCORE_SUBMITTED = false;
    private static final Boolean UPDATED_SCORE_SUBMITTED = true;

    private static final Boolean DEFAULT_SCORE_APPROVED = false;
    private static final Boolean UPDATED_SCORE_APPROVED = true;

    private static final String ENTITY_API_URL = "/api/matches";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MatchMapper matchMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMatchMockMvc;

    private Match match;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Match createEntity(EntityManager em) {
        Match match = new Match()
            .matchIndex(DEFAULT_MATCH_INDEX)
            .oneScore(DEFAULT_ONE_SCORE)
            .twoScore(DEFAULT_TWO_SCORE)
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME)
            .scoreSubmitted(DEFAULT_SCORE_SUBMITTED)
            .scoreApproved(DEFAULT_SCORE_APPROVED);
        return match;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Match createUpdatedEntity(EntityManager em) {
        Match match = new Match()
            .matchIndex(UPDATED_MATCH_INDEX)
            .oneScore(UPDATED_ONE_SCORE)
            .twoScore(UPDATED_TWO_SCORE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .scoreSubmitted(UPDATED_SCORE_SUBMITTED)
            .scoreApproved(UPDATED_SCORE_APPROVED);
        return match;
    }

    @BeforeEach
    public void initTest() {
        match = createEntity(em);
    }

    @Test
    @Transactional
    void createMatch() throws Exception {
        int databaseSizeBeforeCreate = matchRepository.findAll().size();
        // Create the Match
        MatchDTO matchDTO = matchMapper.toDto(match);
        restMatchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(matchDTO)))
            .andExpect(status().isCreated());

        // Validate the Match in the database
        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeCreate + 1);
        Match testMatch = matchList.get(matchList.size() - 1);
        assertThat(testMatch.getMatchIndex()).isEqualTo(DEFAULT_MATCH_INDEX);
        assertThat(testMatch.getOneScore()).isEqualTo(DEFAULT_ONE_SCORE);
        assertThat(testMatch.getTwoScore()).isEqualTo(DEFAULT_TWO_SCORE);
        assertThat(testMatch.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testMatch.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testMatch.getScoreSubmitted()).isEqualTo(DEFAULT_SCORE_SUBMITTED);
        assertThat(testMatch.getScoreApproved()).isEqualTo(DEFAULT_SCORE_APPROVED);
    }

    @Test
    @Transactional
    void createMatchWithExistingId() throws Exception {
        // Create the Match with an existing ID
        match.setId(1L);
        MatchDTO matchDTO = matchMapper.toDto(match);

        int databaseSizeBeforeCreate = matchRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMatchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(matchDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Match in the database
        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMatchIndexIsRequired() throws Exception {
        int databaseSizeBeforeTest = matchRepository.findAll().size();
        // set the field null
        match.setMatchIndex(null);

        // Create the Match, which fails.
        MatchDTO matchDTO = matchMapper.toDto(match);

        restMatchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(matchDTO)))
            .andExpect(status().isBadRequest());

        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkScoreSubmittedIsRequired() throws Exception {
        int databaseSizeBeforeTest = matchRepository.findAll().size();
        // set the field null
        match.setScoreSubmitted(null);

        // Create the Match, which fails.
        MatchDTO matchDTO = matchMapper.toDto(match);

        restMatchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(matchDTO)))
            .andExpect(status().isBadRequest());

        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkScoreApprovedIsRequired() throws Exception {
        int databaseSizeBeforeTest = matchRepository.findAll().size();
        // set the field null
        match.setScoreApproved(null);

        // Create the Match, which fails.
        MatchDTO matchDTO = matchMapper.toDto(match);

        restMatchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(matchDTO)))
            .andExpect(status().isBadRequest());

        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMatches() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        // Get all the matchList
        restMatchMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(match.getId().intValue())))
            .andExpect(jsonPath("$.[*].matchIndex").value(hasItem(DEFAULT_MATCH_INDEX)))
            .andExpect(jsonPath("$.[*].oneScore").value(hasItem(DEFAULT_ONE_SCORE)))
            .andExpect(jsonPath("$.[*].twoScore").value(hasItem(DEFAULT_TWO_SCORE)))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].scoreSubmitted").value(hasItem(DEFAULT_SCORE_SUBMITTED.booleanValue())))
            .andExpect(jsonPath("$.[*].scoreApproved").value(hasItem(DEFAULT_SCORE_APPROVED.booleanValue())));
    }

    @Test
    @Transactional
    void getMatch() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        // Get the match
        restMatchMockMvc
            .perform(get(ENTITY_API_URL_ID, match.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(match.getId().intValue()))
            .andExpect(jsonPath("$.matchIndex").value(DEFAULT_MATCH_INDEX))
            .andExpect(jsonPath("$.oneScore").value(DEFAULT_ONE_SCORE))
            .andExpect(jsonPath("$.twoScore").value(DEFAULT_TWO_SCORE))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()))
            .andExpect(jsonPath("$.scoreSubmitted").value(DEFAULT_SCORE_SUBMITTED.booleanValue()))
            .andExpect(jsonPath("$.scoreApproved").value(DEFAULT_SCORE_APPROVED.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingMatch() throws Exception {
        // Get the match
        restMatchMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMatch() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        int databaseSizeBeforeUpdate = matchRepository.findAll().size();

        // Update the match
        Match updatedMatch = matchRepository.findById(match.getId()).get();
        // Disconnect from session so that the updates on updatedMatch are not directly saved in db
        em.detach(updatedMatch);
        updatedMatch
            .matchIndex(UPDATED_MATCH_INDEX)
            .oneScore(UPDATED_ONE_SCORE)
            .twoScore(UPDATED_TWO_SCORE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .scoreSubmitted(UPDATED_SCORE_SUBMITTED)
            .scoreApproved(UPDATED_SCORE_APPROVED);
        MatchDTO matchDTO = matchMapper.toDto(updatedMatch);

        restMatchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, matchDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(matchDTO))
            )
            .andExpect(status().isOk());

        // Validate the Match in the database
        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeUpdate);
        Match testMatch = matchList.get(matchList.size() - 1);
        assertThat(testMatch.getMatchIndex()).isEqualTo(UPDATED_MATCH_INDEX);
        assertThat(testMatch.getOneScore()).isEqualTo(UPDATED_ONE_SCORE);
        assertThat(testMatch.getTwoScore()).isEqualTo(UPDATED_TWO_SCORE);
        assertThat(testMatch.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testMatch.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testMatch.getScoreSubmitted()).isEqualTo(UPDATED_SCORE_SUBMITTED);
        assertThat(testMatch.getScoreApproved()).isEqualTo(UPDATED_SCORE_APPROVED);
    }

    @Test
    @Transactional
    void putNonExistingMatch() throws Exception {
        int databaseSizeBeforeUpdate = matchRepository.findAll().size();
        match.setId(count.incrementAndGet());

        // Create the Match
        MatchDTO matchDTO = matchMapper.toDto(match);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMatchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, matchDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(matchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Match in the database
        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMatch() throws Exception {
        int databaseSizeBeforeUpdate = matchRepository.findAll().size();
        match.setId(count.incrementAndGet());

        // Create the Match
        MatchDTO matchDTO = matchMapper.toDto(match);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMatchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(matchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Match in the database
        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMatch() throws Exception {
        int databaseSizeBeforeUpdate = matchRepository.findAll().size();
        match.setId(count.incrementAndGet());

        // Create the Match
        MatchDTO matchDTO = matchMapper.toDto(match);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMatchMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(matchDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Match in the database
        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMatchWithPatch() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        int databaseSizeBeforeUpdate = matchRepository.findAll().size();

        // Update the match using partial update
        Match partialUpdatedMatch = new Match();
        partialUpdatedMatch.setId(match.getId());

        partialUpdatedMatch
            .oneScore(UPDATED_ONE_SCORE)
            .twoScore(UPDATED_TWO_SCORE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .scoreApproved(UPDATED_SCORE_APPROVED);

        restMatchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMatch.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMatch))
            )
            .andExpect(status().isOk());

        // Validate the Match in the database
        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeUpdate);
        Match testMatch = matchList.get(matchList.size() - 1);
        assertThat(testMatch.getMatchIndex()).isEqualTo(DEFAULT_MATCH_INDEX);
        assertThat(testMatch.getOneScore()).isEqualTo(UPDATED_ONE_SCORE);
        assertThat(testMatch.getTwoScore()).isEqualTo(UPDATED_TWO_SCORE);
        assertThat(testMatch.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testMatch.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testMatch.getScoreSubmitted()).isEqualTo(DEFAULT_SCORE_SUBMITTED);
        assertThat(testMatch.getScoreApproved()).isEqualTo(UPDATED_SCORE_APPROVED);
    }

    @Test
    @Transactional
    void fullUpdateMatchWithPatch() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        int databaseSizeBeforeUpdate = matchRepository.findAll().size();

        // Update the match using partial update
        Match partialUpdatedMatch = new Match();
        partialUpdatedMatch.setId(match.getId());

        partialUpdatedMatch
            .matchIndex(UPDATED_MATCH_INDEX)
            .oneScore(UPDATED_ONE_SCORE)
            .twoScore(UPDATED_TWO_SCORE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .scoreSubmitted(UPDATED_SCORE_SUBMITTED)
            .scoreApproved(UPDATED_SCORE_APPROVED);

        restMatchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMatch.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMatch))
            )
            .andExpect(status().isOk());

        // Validate the Match in the database
        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeUpdate);
        Match testMatch = matchList.get(matchList.size() - 1);
        assertThat(testMatch.getMatchIndex()).isEqualTo(UPDATED_MATCH_INDEX);
        assertThat(testMatch.getOneScore()).isEqualTo(UPDATED_ONE_SCORE);
        assertThat(testMatch.getTwoScore()).isEqualTo(UPDATED_TWO_SCORE);
        assertThat(testMatch.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testMatch.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testMatch.getScoreSubmitted()).isEqualTo(UPDATED_SCORE_SUBMITTED);
        assertThat(testMatch.getScoreApproved()).isEqualTo(UPDATED_SCORE_APPROVED);
    }

    @Test
    @Transactional
    void patchNonExistingMatch() throws Exception {
        int databaseSizeBeforeUpdate = matchRepository.findAll().size();
        match.setId(count.incrementAndGet());

        // Create the Match
        MatchDTO matchDTO = matchMapper.toDto(match);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMatchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, matchDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(matchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Match in the database
        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMatch() throws Exception {
        int databaseSizeBeforeUpdate = matchRepository.findAll().size();
        match.setId(count.incrementAndGet());

        // Create the Match
        MatchDTO matchDTO = matchMapper.toDto(match);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMatchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(matchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Match in the database
        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMatch() throws Exception {
        int databaseSizeBeforeUpdate = matchRepository.findAll().size();
        match.setId(count.incrementAndGet());

        // Create the Match
        MatchDTO matchDTO = matchMapper.toDto(match);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMatchMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(matchDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Match in the database
        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMatch() throws Exception {
        // Initialize the database
        matchRepository.saveAndFlush(match);

        int databaseSizeBeforeDelete = matchRepository.findAll().size();

        // Delete the match
        restMatchMockMvc
            .perform(delete(ENTITY_API_URL_ID, match.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Match> matchList = matchRepository.findAll();
        assertThat(matchList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
