package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import team.bham.domain.enumeration.AccessStatus;
import team.bham.domain.enumeration.TournamentBracketType;
import team.bham.domain.enumeration.TournamentSetting;

/**
 * A Tournament.
 */
@Entity
@Table(name = "tournament")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Tournament implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 40)
    @Column(name = "name", length = 40, nullable = false, unique = true)
    private String name;

    @Size(max = 5000)
    @Column(name = "description", length = 5000)
    private String description;

    @Column(name = "prize_pool")
    private Float prizePool;

    @Column(name = "entry_fee")
    private Float entryFee;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private ZonedDateTime startTime;

    @Column(name = "end_time")
    private ZonedDateTime endTime;

    @Column(name = "location")
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "bracket_format")
    private TournamentBracketType bracketFormat;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "access_status", nullable = false)
    private AccessStatus accessStatus;

    @Column(name = "is_live")
    private Boolean isLive = false;

    @Column(name = "ended")
    private Boolean ended = false;

    @Lob
    @Column(name = "banner")
    private byte[] banner;

    @Column(name = "banner_content_type")
    private String bannerContentType;

    @NotNull
    @Column(name = "games_per_match", nullable = false)
    private Integer gamesPerMatch;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tournament_setting", nullable = false)
    private TournamentSetting tournamentSetting;

    @OneToMany(mappedBy = "tournament")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "games", "tournament", "teamOne", "teamTwo" }, allowSetters = true)
    private Set<Match> matches = new HashSet<>();

    @OneToMany(mappedBy = "tournament")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "tournament" }, allowSetters = true)
    private Set<TournamentAccessibility> tournamentAccessibilities = new HashSet<>();

    @OneToMany(mappedBy = "tournament")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "team", "tournament" }, allowSetters = true)
    private Set<Participant> participants = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "gamePlayers", "creators", "players" }, allowSetters = true)
    private PlayerData creator;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Tournament id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Tournament name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Tournament description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrizePool() {
        return this.prizePool;
    }

    public Tournament prizePool(Float prizePool) {
        this.setPrizePool(prizePool);
        return this;
    }

    public void setPrizePool(Float prizePool) {
        this.prizePool = prizePool;
    }

    public Float getEntryFee() {
        return this.entryFee;
    }

    public Tournament entryFee(Float entryFee) {
        this.setEntryFee(entryFee);
        return this;
    }

    public void setEntryFee(Float entryFee) {
        this.entryFee = entryFee;
    }

    public ZonedDateTime getStartTime() {
        return this.startTime;
    }

    public Tournament startTime(ZonedDateTime startTime) {
        this.setStartTime(startTime);
        return this;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public ZonedDateTime getEndTime() {
        return this.endTime;
    }

    public Tournament endTime(ZonedDateTime endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return this.location;
    }

    public Tournament location(String location) {
        this.setLocation(location);
        return this;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public TournamentBracketType getBracketFormat() {
        return this.bracketFormat;
    }

    public Tournament bracketFormat(TournamentBracketType bracketFormat) {
        this.setBracketFormat(bracketFormat);
        return this;
    }

    public void setBracketFormat(TournamentBracketType bracketFormat) {
        this.bracketFormat = bracketFormat;
    }

    public AccessStatus getAccessStatus() {
        return this.accessStatus;
    }

    public Tournament accessStatus(AccessStatus accessStatus) {
        this.setAccessStatus(accessStatus);
        return this;
    }

    public void setAccessStatus(AccessStatus accessStatus) {
        this.accessStatus = accessStatus;
    }

    public Boolean getIsLive() {
        return this.isLive;
    }

    public Tournament isLive(Boolean isLive) {
        this.setIsLive(isLive);
        return this;
    }

    public void setIsLive(Boolean isLive) {
        this.isLive = isLive;
    }

    public Boolean getEnded() {
        return this.ended;
    }

    public Tournament ended(Boolean ended) {
        this.setEnded(ended);
        return this;
    }

    public void setEnded(Boolean ended) {
        this.ended = ended;
    }

    public byte[] getBanner() {
        return this.banner;
    }

    public Tournament banner(byte[] banner) {
        this.setBanner(banner);
        return this;
    }

    public void setBanner(byte[] banner) {
        this.banner = banner;
    }

    public String getBannerContentType() {
        return this.bannerContentType;
    }

    public Tournament bannerContentType(String bannerContentType) {
        this.bannerContentType = bannerContentType;
        return this;
    }

    public void setBannerContentType(String bannerContentType) {
        this.bannerContentType = bannerContentType;
    }

    public Integer getGamesPerMatch() {
        return this.gamesPerMatch;
    }

    public Tournament gamesPerMatch(Integer gamesPerMatch) {
        this.setGamesPerMatch(gamesPerMatch);
        return this;
    }

    public void setGamesPerMatch(Integer gamesPerMatch) {
        this.gamesPerMatch = gamesPerMatch;
    }

    public Integer getMaxParticipants() {
        return this.maxParticipants;
    }

    public Tournament maxParticipants(Integer maxParticipants) {
        this.setMaxParticipants(maxParticipants);
        return this;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public TournamentSetting getTournamentSetting() {
        return this.tournamentSetting;
    }

    public Tournament tournamentSetting(TournamentSetting tournamentSetting) {
        this.setTournamentSetting(tournamentSetting);
        return this;
    }

    public void setTournamentSetting(TournamentSetting tournamentSetting) {
        this.tournamentSetting = tournamentSetting;
    }

    public Set<Match> getMatches() {
        return this.matches;
    }

    public void setMatches(Set<Match> matches) {
        if (this.matches != null) {
            this.matches.forEach(i -> i.setTournament(null));
        }
        if (matches != null) {
            matches.forEach(i -> i.setTournament(this));
        }
        this.matches = matches;
    }

    public Tournament matches(Set<Match> matches) {
        this.setMatches(matches);
        return this;
    }

    public Tournament addMatch(Match match) {
        this.matches.add(match);
        match.setTournament(this);
        return this;
    }

    public Tournament removeMatch(Match match) {
        this.matches.remove(match);
        match.setTournament(null);
        return this;
    }

    public Set<TournamentAccessibility> getTournamentAccessibilities() {
        return this.tournamentAccessibilities;
    }

    public void setTournamentAccessibilities(Set<TournamentAccessibility> tournamentAccessibilities) {
        if (this.tournamentAccessibilities != null) {
            this.tournamentAccessibilities.forEach(i -> i.setTournament(null));
        }
        if (tournamentAccessibilities != null) {
            tournamentAccessibilities.forEach(i -> i.setTournament(this));
        }
        this.tournamentAccessibilities = tournamentAccessibilities;
    }

    public Tournament tournamentAccessibilities(Set<TournamentAccessibility> tournamentAccessibilities) {
        this.setTournamentAccessibilities(tournamentAccessibilities);
        return this;
    }

    public Tournament addTournamentAccessibility(TournamentAccessibility tournamentAccessibility) {
        this.tournamentAccessibilities.add(tournamentAccessibility);
        tournamentAccessibility.setTournament(this);
        return this;
    }

    public Tournament removeTournamentAccessibility(TournamentAccessibility tournamentAccessibility) {
        this.tournamentAccessibilities.remove(tournamentAccessibility);
        tournamentAccessibility.setTournament(null);
        return this;
    }

    public Set<Participant> getParticipants() {
        return this.participants;
    }

    public void setParticipants(Set<Participant> participants) {
        if (this.participants != null) {
            this.participants.forEach(i -> i.setTournament(null));
        }
        if (participants != null) {
            participants.forEach(i -> i.setTournament(this));
        }
        this.participants = participants;
    }

    public Tournament participants(Set<Participant> participants) {
        this.setParticipants(participants);
        return this;
    }

    public Tournament addParticipant(Participant participant) {
        this.participants.add(participant);
        participant.setTournament(this);
        return this;
    }

    public Tournament removeParticipant(Participant participant) {
        this.participants.remove(participant);
        participant.setTournament(null);
        return this;
    }

    public PlayerData getCreator() {
        return this.creator;
    }

    public void setCreator(PlayerData playerData) {
        this.creator = playerData;
    }

    public Tournament creator(PlayerData playerData) {
        this.setCreator(playerData);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tournament)) {
            return false;
        }
        return id != null && id.equals(((Tournament) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tournament{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", prizePool=" + getPrizePool() +
            ", entryFee=" + getEntryFee() +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", location='" + getLocation() + "'" +
            ", bracketFormat='" + getBracketFormat() + "'" +
            ", accessStatus='" + getAccessStatus() + "'" +
            ", isLive='" + getIsLive() + "'" +
            ", ended='" + getEnded() + "'" +
            ", banner='" + getBanner() + "'" +
            ", bannerContentType='" + getBannerContentType() + "'" +
            ", gamesPerMatch=" + getGamesPerMatch() +
            ", maxParticipants=" + getMaxParticipants() +
            ", tournamentSetting='" + getTournamentSetting() + "'" +
            "}";
    }
}
