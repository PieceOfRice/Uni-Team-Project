package team.bham.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import team.bham.domain.enumeration.AccessStatus;

/**
 * A Team.
 */
@Entity
@Table(name = "team")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Team implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Lob
    @Column(name = "logo")
    private byte[] logo;

    @Column(name = "logo_content_type")
    private String logoContentType;

    @Lob
    @Column(name = "banner")
    private byte[] banner;

    @Column(name = "banner_content_type")
    private String bannerContentType;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_status", nullable = true)
    private AccessStatus accessStatus;

    @OneToMany(mappedBy = "teamOne")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "games", "tournament", "teamOne", "teamTwo" }, allowSetters = true)
    private Set<Match> teamOnes = new HashSet<>();

    @OneToMany(mappedBy = "teamTwo")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "games", "tournament", "teamOne", "teamTwo" }, allowSetters = true)
    private Set<Match> teamTwos = new HashSet<>();

    @OneToMany(mappedBy = "team")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "player", "team" }, allowSetters = true)
    private Set<TeamPlayer> teams = new HashSet<>();

    @OneToMany(mappedBy = "team")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "team", "tournament" }, allowSetters = true)
    private Set<Participant> participants = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Team id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Team name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getLogo() {
        return this.logo;
    }

    public Team logo(byte[] logo) {
        this.setLogo(logo);
        return this;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public String getLogoContentType() {
        return this.logoContentType;
    }

    public Team logoContentType(String logoContentType) {
        this.logoContentType = logoContentType;
        return this;
    }

    public void setLogoContentType(String logoContentType) {
        this.logoContentType = logoContentType;
    }

    public byte[] getBanner() {
        return this.banner;
    }

    public Team banner(byte[] banner) {
        this.setBanner(banner);
        return this;
    }

    public void setBanner(byte[] banner) {
        this.banner = banner;
    }

    public String getBannerContentType() {
        return this.bannerContentType;
    }

    public Team bannerContentType(String bannerContentType) {
        this.bannerContentType = bannerContentType;
        return this;
    }

    public void setBannerContentType(String bannerContentType) {
        this.bannerContentType = bannerContentType;
    }

    public String getDescription() {
        return this.description;
    }

    public Team description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AccessStatus getAccessStatus() {
        return this.accessStatus;
    }

    public Team accessStatus(AccessStatus accessStatus) {
        this.setAccessStatus(accessStatus);
        return this;
    }

    public void setAccessStatus(AccessStatus accessStatus) {
        this.accessStatus = accessStatus;
    }

    public Set<Match> getTeamOnes() {
        return this.teamOnes;
    }

    public void setTeamOnes(Set<Match> matches) {
        if (this.teamOnes != null) {
            this.teamOnes.forEach(i -> i.setTeamOne(null));
        }
        if (matches != null) {
            matches.forEach(i -> i.setTeamOne(this));
        }
        this.teamOnes = matches;
    }

    public Team teamOnes(Set<Match> matches) {
        this.setTeamOnes(matches);
        return this;
    }

    public Team addTeamOne(Match match) {
        this.teamOnes.add(match);
        match.setTeamOne(this);
        return this;
    }

    public Team removeTeamOne(Match match) {
        this.teamOnes.remove(match);
        match.setTeamOne(null);
        return this;
    }

    public Set<Match> getTeamTwos() {
        return this.teamTwos;
    }

    public void setTeamTwos(Set<Match> matches) {
        if (this.teamTwos != null) {
            this.teamTwos.forEach(i -> i.setTeamTwo(null));
        }
        if (matches != null) {
            matches.forEach(i -> i.setTeamTwo(this));
        }
        this.teamTwos = matches;
    }

    public Team teamTwos(Set<Match> matches) {
        this.setTeamTwos(matches);
        return this;
    }

    public Team addTeamTwo(Match match) {
        this.teamTwos.add(match);
        match.setTeamTwo(this);
        return this;
    }

    public Team removeTeamTwo(Match match) {
        this.teamTwos.remove(match);
        match.setTeamTwo(null);
        return this;
    }

    public Set<TeamPlayer> getTeams() {
        return this.teams;
    }

    public void setTeams(Set<TeamPlayer> teamPlayers) {
        if (this.teams != null) {
            this.teams.forEach(i -> i.setTeam(null));
        }
        if (teamPlayers != null) {
            teamPlayers.forEach(i -> i.setTeam(this));
        }
        this.teams = teamPlayers;
    }

    public Team teams(Set<TeamPlayer> teamPlayers) {
        this.setTeams(teamPlayers);
        return this;
    }

    public Team addTeam(TeamPlayer teamPlayer) {
        this.teams.add(teamPlayer);
        teamPlayer.setTeam(this);
        return this;
    }

    public Team removeTeam(TeamPlayer teamPlayer) {
        this.teams.remove(teamPlayer);
        teamPlayer.setTeam(null);
        return this;
    }

    public Set<Participant> getParticipants() {
        return this.participants;
    }

    public void setParticipants(Set<Participant> participants) {
        if (this.participants != null) {
            this.participants.forEach(i -> i.setTeam(null));
        }
        if (participants != null) {
            participants.forEach(i -> i.setTeam(this));
        }
        this.participants = participants;
    }

    public Team participants(Set<Participant> participants) {
        this.setParticipants(participants);
        return this;
    }

    public Team addParticipant(Participant participant) {
        this.participants.add(participant);
        participant.setTeam(this);
        return this;
    }

    public Team removeParticipant(Participant participant) {
        this.participants.remove(participant);
        participant.setTeam(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Team)) {
            return false;
        }
        return id != null && id.equals(((Team) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Team{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", logo='" + getLogo() + "'" +
            ", logoContentType='" + getLogoContentType() + "'" +
            ", banner='" + getBanner() + "'" +
            ", bannerContentType='" + getBannerContentType() + "'" +
            ", description='" + getDescription() + "'" +
            ", accessStatus='" + getAccessStatus() + "'" +
            "}";
    }
}
