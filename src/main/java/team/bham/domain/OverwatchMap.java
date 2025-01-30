package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import team.bham.domain.enumeration.MapMode;

/**
 * A OverwatchMap.
 */
@Entity
@Table(name = "overwatch_map")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OverwatchMap implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private MapMode mode;

    /*
    @JsonIgnoreProperties(value = { "overwatchMap", "gamePlayers", "match" }, allowSetters = true)
    @OneToOne(mappedBy = "overwatchMap")
    private Game game;
    */

    @OneToMany(mappedBy = "overwatchMap")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Game> games = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OverwatchMap id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public OverwatchMap name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MapMode getMode() {
        return this.mode;
    }

    public OverwatchMap mode(MapMode mode) {
        this.setMode(mode);
        return this;
    }

    public void setMode(MapMode mode) {
        this.mode = mode;
    }

    /*
    public Game getGame() {
        return this.game;
    }

    public void setGame(Game game) {
        if (this.game != null) {
            this.game.setOverwatchMap(null);
        }
        if (game != null) {
            game.setOverwatchMap(this);
        }
        this.game = game;
    }

    

    public OverwatchMap game(Game game) {
        this.setGame(game);
        return this;
    }

    */

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OverwatchMap)) {
            return false;
        }
        return id != null && id.equals(((OverwatchMap) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OverwatchMap{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", mode='" + getMode() + "'" +
            "}";
    }
}
