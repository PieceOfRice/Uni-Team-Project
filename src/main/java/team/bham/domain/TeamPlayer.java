package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import team.bham.domain.enumeration.TeamRole;

/**
 * A TeamPlayer.
 */
@Entity
@Table(name = "team_player")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeamPlayer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private TeamRole role;

    @NotNull
    @Column(name = "accepted", nullable = false)
    private boolean accepted;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "gamePlayers", "creators", "players" }, allowSetters = true)
    private PlayerData player;

    @ManyToOne
    @JsonIgnoreProperties(value = { "teamOnes", "teamTwos", "teams", "participants" }, allowSetters = true)
    private Team team;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TeamPlayer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TeamRole getRole() {
        return this.role;
    }

    public TeamPlayer role(TeamRole role) {
        this.setRole(role);
        return this;
    }

    public void setRole(TeamRole role) {
        this.role = role;
    }

    public PlayerData getPlayer() {
        return this.player;
    }

    public void setPlayer(PlayerData playerData) {
        this.player = playerData;
    }

    public TeamPlayer player(PlayerData playerData) {
        this.setPlayer(playerData);
        return this;
    }

    public Team getTeam() {
        return this.team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public boolean getAccepted() {
        return this.accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public TeamPlayer team(Team team) {
        this.setTeam(team);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TeamPlayer)) {
            return false;
        }
        return id != null && id.equals(((TeamPlayer) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeamPlayer{" +
            "id=" + getId() +
            ", role='" + getRole() + "'" +
            "}";
    }
}
