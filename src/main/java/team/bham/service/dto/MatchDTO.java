package team.bham.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link team.bham.domain.Match} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MatchDTO implements Serializable {

    private Long id;

    @NotNull
    private Integer matchIndex;

    @Min(value = 0)
    private Integer oneScore;

    @Min(value = 0)
    private Integer twoScore;

    private Instant startTime;

    private Instant endTime;

    @NotNull
    private Boolean scoreSubmitted;

    @NotNull
    private Boolean scoreApproved;

    private TournamentDTO tournament;

    private TeamDTO teamOne;

    private TeamDTO teamTwo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMatchIndex() {
        return matchIndex;
    }

    public void setMatchIndex(Integer matchIndex) {
        this.matchIndex = matchIndex;
    }

    public Integer getOneScore() {
        return oneScore;
    }

    public void setOneScore(Integer oneScore) {
        this.oneScore = oneScore;
    }

    public Integer getTwoScore() {
        return twoScore;
    }

    public void setTwoScore(Integer twoScore) {
        this.twoScore = twoScore;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Boolean getScoreSubmitted() {
        return scoreSubmitted;
    }

    public void setScoreSubmitted(Boolean scoreSubmitted) {
        this.scoreSubmitted = scoreSubmitted;
    }

    public Boolean getScoreApproved() {
        return scoreApproved;
    }

    public void setScoreApproved(Boolean scoreApproved) {
        this.scoreApproved = scoreApproved;
    }

    public TournamentDTO getTournament() {
        return tournament;
    }

    public void setTournament(TournamentDTO tournament) {
        this.tournament = tournament;
    }

    public TeamDTO getTeamOne() {
        return teamOne;
    }

    public void setTeamOne(TeamDTO teamOne) {
        this.teamOne = teamOne;
    }

    public TeamDTO getTeamTwo() {
        return teamTwo;
    }

    public void setTeamTwo(TeamDTO teamTwo) {
        this.teamTwo = teamTwo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MatchDTO)) {
            return false;
        }

        MatchDTO matchDTO = (MatchDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, matchDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MatchDTO{" +
            "id=" + getId() +
            ", matchIndex=" + getMatchIndex() +
            ", oneScore=" + getOneScore() +
            ", twoScore=" + getTwoScore() +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", scoreSubmitted='" + getScoreSubmitted() + "'" +
            ", scoreApproved='" + getScoreApproved() + "'" +
            ", tournament=" + getTournament() +
            ", teamOne=" + getTeamOne() +
            ", teamTwo=" + getTeamTwo() +
            "}";
    }
}
