package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import team.bham.domain.enumeration.PlayerDevice;
import team.bham.domain.enumeration.PlayerLanguage;

/**
 * A PlayerData.
 */
@Entity
@Table(name = "player_data")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PlayerData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Size(min = 3, max = 40)
    @Column(name = "overwatch_username", length = 40)
    private String overwatchUsername;

    @Lob
    @Column(name = "profile")
    private byte[] profile;

    @Column(name = "profile_content_type")
    private String profileContentType;

    @Size(max = 1000)
    @Column(name = "bio", length = 1000)
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(name = "primary_language")
    private PlayerLanguage primaryLanguage;

    @Enumerated(EnumType.STRING)
    @Column(name = "device")
    private PlayerDevice device;

    @NotNull
    @Min(value = 0)
    @Column(name = "matches_played", nullable = false)
    private Integer matchesPlayed;

    @NotNull
    @Min(value = 0)
    @Column(name = "tournaments_played", nullable = false)
    private Integer tournamentsPlayed;

    @NotNull
    @Min(value = 0)
    @Column(name = "match_wins", nullable = false)
    private Integer matchWins;

    @NotNull
    @Min(value = 0)
    @Column(name = "tournament_wins", nullable = false)
    private Integer tournamentWins;

    @Min(value = 0)
    @Column(name = "tournament_top_10_s")
    private Integer tournamentTop10s;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    @OneToMany(mappedBy = "playerData")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "game", "playerData" }, allowSetters = true)
    private Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy = "creator")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "matches", "tournamentAccessibilities", "participants", "creator" }, allowSetters = true)
    private Set<Tournament> creators = new HashSet<>();

    @OneToMany(mappedBy = "player")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "player", "team" }, allowSetters = true)
    private Set<TeamPlayer> players = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PlayerData id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public PlayerData name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverwatchUsername() {
        return this.overwatchUsername;
    }

    public PlayerData overwatchUsername(String overwatchUsername) {
        this.setOverwatchUsername(overwatchUsername);
        return this;
    }

    public void setOverwatchUsername(String overwatchUsername) {
        this.overwatchUsername = overwatchUsername;
    }

    public byte[] getProfile() {
        return this.profile;
    }

    public PlayerData profile(byte[] profile) {
        this.setProfile(profile);
        return this;
    }

    public void setProfile(byte[] profile) {
        this.profile = profile;
    }

    public String getProfileContentType() {
        return this.profileContentType;
    }

    public PlayerData profileContentType(String profileContentType) {
        this.profileContentType = profileContentType;
        return this;
    }

    public void setProfileContentType(String profileContentType) {
        this.profileContentType = profileContentType;
    }

    public String getBio() {
        return this.bio;
    }

    public PlayerData bio(String bio) {
        this.setBio(bio);
        return this;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public PlayerLanguage getPrimaryLanguage() {
        return this.primaryLanguage;
    }

    public PlayerData primaryLanguage(PlayerLanguage primaryLanguage) {
        this.setPrimaryLanguage(primaryLanguage);
        return this;
    }

    public void setPrimaryLanguage(PlayerLanguage primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public PlayerDevice getDevice() {
        return this.device;
    }

    public PlayerData device(PlayerDevice device) {
        this.setDevice(device);
        return this;
    }

    public void setDevice(PlayerDevice device) {
        this.device = device;
    }

    public Integer getMatchesPlayed() {
        return this.matchesPlayed;
    }

    public PlayerData matchesPlayed(Integer matchesPlayed) {
        this.setMatchesPlayed(matchesPlayed);
        return this;
    }

    public void setMatchesPlayed(Integer matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public Integer getTournamentsPlayed() {
        return this.tournamentsPlayed;
    }

    public PlayerData tournamentsPlayed(Integer tournamentsPlayed) {
        this.setTournamentsPlayed(tournamentsPlayed);
        return this;
    }

    public void setTournamentsPlayed(Integer tournamentsPlayed) {
        this.tournamentsPlayed = tournamentsPlayed;
    }

    public Integer getMatchWins() {
        return this.matchWins;
    }

    public PlayerData matchWins(Integer matchWins) {
        this.setMatchWins(matchWins);
        return this;
    }

    public void setMatchWins(Integer matchWins) {
        this.matchWins = matchWins;
    }

    public Integer getTournamentWins() {
        return this.tournamentWins;
    }

    public PlayerData tournamentWins(Integer tournamentWins) {
        this.setTournamentWins(tournamentWins);
        return this;
    }

    public void setTournamentWins(Integer tournamentWins) {
        this.tournamentWins = tournamentWins;
    }

    public Integer getTournamentTop10s() {
        return this.tournamentTop10s;
    }

    public PlayerData tournamentTop10s(Integer tournamentTop10s) {
        this.setTournamentTop10s(tournamentTop10s);
        return this;
    }

    public void setTournamentTop10s(Integer tournamentTop10s) {
        this.tournamentTop10s = tournamentTop10s;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PlayerData user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<GamePlayer> getGamePlayers() {
        return this.gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        if (this.gamePlayers != null) {
            this.gamePlayers.forEach(i -> i.setPlayerData(null));
        }
        if (gamePlayers != null) {
            gamePlayers.forEach(i -> i.setPlayerData(this));
        }
        this.gamePlayers = gamePlayers;
    }

    public PlayerData gamePlayers(Set<GamePlayer> gamePlayers) {
        this.setGamePlayers(gamePlayers);
        return this;
    }

    public PlayerData addGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayers.add(gamePlayer);
        gamePlayer.setPlayerData(this);
        return this;
    }

    public PlayerData removeGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayers.remove(gamePlayer);
        gamePlayer.setPlayerData(null);
        return this;
    }

    public Set<Tournament> getCreators() {
        return this.creators;
    }

    public void setCreators(Set<Tournament> tournaments) {
        if (this.creators != null) {
            this.creators.forEach(i -> i.setCreator(null));
        }
        if (tournaments != null) {
            tournaments.forEach(i -> i.setCreator(this));
        }
        this.creators = tournaments;
    }

    public PlayerData creators(Set<Tournament> tournaments) {
        this.setCreators(tournaments);
        return this;
    }

    public PlayerData addCreator(Tournament tournament) {
        this.creators.add(tournament);
        tournament.setCreator(this);
        return this;
    }

    public PlayerData removeCreator(Tournament tournament) {
        this.creators.remove(tournament);
        tournament.setCreator(null);
        return this;
    }

    public Set<TeamPlayer> getPlayers() {
        return this.players;
    }

    public void setPlayers(Set<TeamPlayer> teamPlayers) {
        if (this.players != null) {
            this.players.forEach(i -> i.setPlayer(null));
        }
        if (teamPlayers != null) {
            teamPlayers.forEach(i -> i.setPlayer(this));
        }
        this.players = teamPlayers;
    }

    public PlayerData players(Set<TeamPlayer> teamPlayers) {
        this.setPlayers(teamPlayers);
        return this;
    }

    public PlayerData addPlayer(TeamPlayer teamPlayer) {
        this.players.add(teamPlayer);
        teamPlayer.setPlayer(this);
        return this;
    }

    public PlayerData removePlayer(TeamPlayer teamPlayer) {
        this.players.remove(teamPlayer);
        teamPlayer.setPlayer(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlayerData)) {
            return false;
        }
        return id != null && id.equals(((PlayerData) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlayerData{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", overwatchUsername='" + getOverwatchUsername() + "'" +
            ", profile='" + getProfile() + "'" +
            ", profileContentType='" + getProfileContentType() + "'" +
            ", bio='" + getBio() + "'" +
            ", primaryLanguage='" + getPrimaryLanguage() + "'" +
            ", device='" + getDevice() + "'" +
            ", matchesPlayed=" + getMatchesPlayed() +
            ", tournamentsPlayed=" + getTournamentsPlayed() +
            ", matchWins=" + getMatchWins() +
            ", tournamentWins=" + getTournamentWins() +
            ", tournamentTop10s=" + getTournamentTop10s() +
            "}";
    }
}
