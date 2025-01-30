package team.bham.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;
import team.bham.domain.enumeration.VenueAccessibilities;

/**
 * A DTO for the {@link team.bham.domain.TournamentAccessibility} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TournamentAccessibilityDTO implements Serializable {

    private Long id;

    @NotNull
    private VenueAccessibilities accessibility;

    private TournamentDTO tournament;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VenueAccessibilities getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(VenueAccessibilities accessibility) {
        this.accessibility = accessibility;
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
        if (!(o instanceof TournamentAccessibilityDTO)) {
            return false;
        }

        TournamentAccessibilityDTO tournamentAccessibilityDTO = (TournamentAccessibilityDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tournamentAccessibilityDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TournamentAccessibilityDTO{" +
            "id=" + getId() +
            ", accessibility='" + getAccessibility() + "'" +
            ", tournament=" + getTournament() +
            "}";
    }
}
