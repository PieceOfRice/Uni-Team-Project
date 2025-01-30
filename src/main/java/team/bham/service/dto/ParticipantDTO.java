package team.bham.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link team.bham.domain.Participant} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParticipantDTO implements Serializable {

    private Long id;

    @NotNull
    private Integer signUpRank;

    private TeamDTO team;

    private TournamentDTO tournament;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSignUpRank() {
        return signUpRank;
    }

    public void setSignUpRank(Integer signUpRank) {
        this.signUpRank = signUpRank;
    }

    public TeamDTO getTeam() {
        return team;
    }

    public void setTeam(TeamDTO team) {
        this.team = team;
    }

    public TournamentDTO getTournament() {
        return tournament;
    }

    public void setTournament(TournamentDTO tournament) {
        this.tournament = tournament;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParticipantDTO)) {
            return false;
        }

        ParticipantDTO participantDTO = (ParticipantDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, participantDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParticipantDTO{" +
            "id=" + getId() +
            ", signUpRank=" + getSignUpRank() +
            ", team=" + getTeam() +
            ", tournament=" + getTournament() +
            "}";
    }
}
