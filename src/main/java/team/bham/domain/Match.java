package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Match.
 */
@Entity
@Table(name = "match")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Match implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "match_index", nullable = false)
    private Integer matchIndex;

    @Min(value = 0)
    @Column(name = "one_score")
    private Integer oneScore;

    @Min(value = 0)
    @Column(name = "two_score")
    private Integer twoScore;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @NotNull
    @Column(name = "score_submitted", nullable = false)
    private Boolean scoreSubmitted;

    @NotNull
    @Column(name = "score_approved", nullable = false)
    private Boolean scoreApproved;

    @OneToMany(mappedBy = "match")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "overwatchMap", "gamePlayers", "match" }, allowSetters = true)
    private Set<Game> games = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "matches", "tournamentAccessibilities", "participants", "creator" }, allowSetters = true)
    private Tournament tournament;

    @ManyToOne
    @JsonIgnoreProperties(value = { "teamOnes", "teamTwos", "teams", "participants" }, allowSetters = true)
    private Team teamOne;

    @ManyToOne
    @JsonIgnoreProperties(value = { "teamOnes", "teamTwos", "teams", "participants" }, allowSetters = true)
    private Team teamTwo;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Match id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMatchIndex() {
        return this.matchIndex;
    }

    public Match matchIndex(Integer matchIndex) {
        this.setMatchIndex(matchIndex);
        return this;
    }

    public void setMatchIndex(Integer matchIndex) {
        this.matchIndex = matchIndex;
    }

    public Integer getOneScore() {
        return this.oneScore;
    }

    public Match oneScore(Integer oneScore) {
        this.setOneScore(oneScore);
        return this;
    }

    public void setOneScore(Integer oneScore) {
        this.oneScore = oneScore;
    }

    public Integer getTwoScore() {
        return this.twoScore;
    }

    public Match twoScore(Integer twoScore) {
        this.setTwoScore(twoScore);
        return this;
    }

    public void setTwoScore(Integer twoScore) {
        this.twoScore = twoScore;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public Match startTime(Instant startTime) {
        this.setStartTime(startTime);
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public Match endTime(Instant endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Boolean getScoreSubmitted() {
        return this.scoreSubmitted;
    }

    public Match scoreSubmitted(Boolean scoreSubmitted) {
        this.setScoreSubmitted(scoreSubmitted);
        return this;
    }

    public void setScoreSubmitted(Boolean scoreSubmitted) {
        this.scoreSubmitted = scoreSubmitted;
    }

    public Boolean getScoreApproved() {
        return this.scoreApproved;
    }

    public Match scoreApproved(Boolean scoreApproved) {
        this.setScoreApproved(scoreApproved);
        return this;
    }

    public void setScoreApproved(Boolean scoreApproved) {
        this.scoreApproved = scoreApproved;
    }

    public Set<Game> getGames() {
        return this.games;
    }

    public void setGames(Set<Game> games) {
        if (this.games != null) {
            this.games.forEach(i -> i.setMatch(null));
        }
        if (games != null) {
            games.forEach(i -> i.setMatch(this));
        }
        this.games = games;
    }

    public Match games(Set<Game> games) {
        this.setGames(games);
        return this;
    }

    public Match addGame(Game game) {
        this.games.add(game);
        game.setMatch(this);
        return this;
    }

    public Match removeGame(Game game) {
        this.games.remove(game);
        game.setMatch(null);
        return this;
    }

    public Tournament getTournament() {
        return this.tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Match tournament(Tournament tournament) {
        this.setTournament(tournament);
        return this;
    }

    public Team getTeamOne() {
        return this.teamOne;
    }

    public void setTeamOne(Team team) {
        this.teamOne = team;
    }

    public Match teamOne(Team team) {
        this.setTeamOne(team);
        return this;
    }

    public Team getTeamTwo() {
        return this.teamTwo;
    }

    public void setTeamTwo(Team team) {
        this.teamTwo = team;
    }

    public Match teamTwo(Team team) {
        this.setTeamTwo(team);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Match)) {
            return false;
        }
        return id != null && id.equals(((Match) o).id);
    }

    @Override
    public int hashCode() {
        // see
        // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Match{" +
                "id=" + getId() +
                ", matchIndex=" + getMatchIndex() +
                ", oneScore=" + getOneScore() +
                ", twoScore=" + getTwoScore() +
                ", startTime='" + getStartTime() + "'" +
                ", endTime='" + getEndTime() + "'" +
                ", scoreSubmitted='" + getScoreSubmitted() + "'" +
                ", scoreApproved='" + getScoreApproved() + "'" +
                "}";
    }
}
