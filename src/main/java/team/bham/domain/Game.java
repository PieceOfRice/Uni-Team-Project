package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Game.
 */
@Entity
@Table(name = "game")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 0)
    @Column(name = "jhi_order", nullable = false)
    private Integer order;

    @NotNull
    @Column(name = "score_one", nullable = false)
    private Integer scoreOne;

    @NotNull
    @Column(name = "score_two", nullable = false)
    private Integer scoreTwo;

    @Column(name = "replay", nullable = true)
    private String replay;

    /*
    @JsonIgnoreProperties(value = { "game" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private OverwatchMap overwatchMap;
    */

    @ManyToOne
    private OverwatchMap overwatchMap;

    @OneToMany(mappedBy = "game")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "game", "playerData" }, allowSetters = true)
    private Set<GamePlayer> gamePlayers = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "games", "tournament", "teamOne", "teamTwo" }, allowSetters = true)
    private Match match;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Game id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrder() {
        return this.order;
    }

    public Game order(Integer order) {
        this.setOrder(order);
        return this;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getScoreOne() {
        return this.scoreOne;
    }

    public Game scoreOne(Integer scoreOne) {
        this.setScoreOne(scoreOne);
        return this;
    }

    public void setScoreOne(Integer scoreOne) {
        this.scoreOne = scoreOne;
    }

    public Integer getScoreTwo() {
        return this.scoreTwo;
    }

    public Game scoreTwo(Integer scoreTwo) {
        this.setScoreTwo(scoreTwo);
        return this;
    }

    public void setScoreTwo(Integer scoreTwo) {
        this.scoreTwo = scoreTwo;
    }

    //new replay attribute
    public String getReplay() {
        return this.replay;
    }

    public Game replay(String replay) {
        this.setReplay(replay);
        return this;
    }

    public void setReplay(String replay) {
        this.replay = replay;
    }

    public OverwatchMap getOverwatchMap() {
        return this.overwatchMap;
    }

    public void setOverwatchMap(OverwatchMap overwatchMap) {
        this.overwatchMap = overwatchMap;
    }

    public Game overwatchMap(OverwatchMap overwatchMap) {
        this.setOverwatchMap(overwatchMap);
        return this;
    }

    public Set<GamePlayer> getGamePlayers() {
        return this.gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        if (this.gamePlayers != null) {
            this.gamePlayers.forEach(i -> i.setGame(null));
        }
        if (gamePlayers != null) {
            gamePlayers.forEach(i -> i.setGame(this));
        }
        this.gamePlayers = gamePlayers;
    }

    public Game gamePlayers(Set<GamePlayer> gamePlayers) {
        this.setGamePlayers(gamePlayers);
        return this;
    }

    public Game addGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayers.add(gamePlayer);
        gamePlayer.setGame(this);
        return this;
    }

    public Game removeGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayers.remove(gamePlayer);
        gamePlayer.setGame(null);
        return this;
    }

    public Match getMatch() {
        return this.match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Game match(Match match) {
        this.setMatch(match);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Game)) {
            return false;
        }
        return id != null && id.equals(((Game) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Game{" +
            "id=" + getId() +
            ", order=" + getOrder() +
            ", scoreOne=" + getScoreOne() +
            ", scoreTwo=" + getScoreTwo() +
            ", replay=" + getReplay() +
            "}";
    }
}
