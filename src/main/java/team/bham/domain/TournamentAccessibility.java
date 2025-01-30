package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import team.bham.domain.enumeration.VenueAccessibilities;

/**
 * A TournamentAccessibility.
 */
@Entity
@Table(name = "tournament_accessibility")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TournamentAccessibility implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "accessibility", nullable = false)
    private VenueAccessibilities accessibility;

    @ManyToOne
    @JsonIgnoreProperties(value = { "matches", "tournamentAccessibilities", "participants", "creator" }, allowSetters = true)
    private Tournament tournament;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TournamentAccessibility id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VenueAccessibilities getAccessibility() {
        return this.accessibility;
    }

    public TournamentAccessibility accessibility(VenueAccessibilities accessibility) {
        this.setAccessibility(accessibility);
        return this;
    }

    public void setAccessibility(VenueAccessibilities accessibility) {
        this.accessibility = accessibility;
    }

    public Tournament getTournament() {
        return this.tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public TournamentAccessibility tournament(Tournament tournament) {
        this.setTournament(tournament);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TournamentAccessibility)) {
            return false;
        }
        return id != null && id.equals(((TournamentAccessibility) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TournamentAccessibility{" +
            "id=" + getId() +
            ", accessibility='" + getAccessibility() + "'" +
            "}";
    }
}
