package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import team.bham.domain.enumeration.GameTeam;

/**
 * A GamePlayer.
 */
@Entity
@Table(name = "game_player")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GamePlayer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "team", nullable = false)
    private GameTeam team;

    @ManyToOne
    @JsonIgnoreProperties(value = { "overwatchMap", "gamePlayers", "match" }, allowSetters = true)
    private Game game;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "gamePlayers", "creators", "players" }, allowSetters = true)
    private PlayerData playerData;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public GamePlayer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GameTeam getTeam() {
        return this.team;
    }

    public GamePlayer team(GameTeam team) {
        this.setTeam(team);
        return this;
    }

    public void setTeam(GameTeam team) {
        this.team = team;
    }

    public Game getGame() {
        return this.game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public GamePlayer game(Game game) {
        this.setGame(game);
        return this;
    }

    public PlayerData getPlayerData() {
        return this.playerData;
    }

    public void setPlayerData(PlayerData playerData) {
        this.playerData = playerData;
    }

    public GamePlayer playerData(PlayerData playerData) {
        this.setPlayerData(playerData);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GamePlayer)) {
            return false;
        }
        return id != null && id.equals(((GamePlayer) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GamePlayer{" +
            "id=" + getId() +
            ", team='" + getTeam() + "'" +
            "}";
    }
}
