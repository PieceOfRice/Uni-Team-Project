package team.bham.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;
import team.bham.domain.enumeration.PlayerDevice;
import team.bham.domain.enumeration.PlayerLanguage;

/**
 * A DTO for the {@link team.bham.domain.PlayerData} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PlayerDataDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 3, max = 50)
    private String name;

    @Size(min = 3, max = 40)
    private String overwatchUsername;

    @Lob
    private byte[] profile;

    private String profileContentType;

    @Size(max = 1000)
    private String bio;

    private PlayerLanguage primaryLanguage;

    private PlayerDevice device;

    @NotNull
    @Min(value = 0)
    private Integer matchesPlayed;

    @NotNull
    @Min(value = 0)
    private Integer tournamentsPlayed;

    @NotNull
    @Min(value = 0)
    private Integer matchWins;

    @NotNull
    @Min(value = 0)
    private Integer tournamentWins;

    @Min(value = 0)
    private Integer tournamentTop10s;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverwatchUsername() {
        return overwatchUsername;
    }

    public void setOverwatchUsername(String overwatchUsername) {
        this.overwatchUsername = overwatchUsername;
    }

    public byte[] getProfile() {
        return profile;
    }

    public void setProfile(byte[] profile) {
        this.profile = profile;
    }

    public String getProfileContentType() {
        return profileContentType;
    }

    public void setProfileContentType(String profileContentType) {
        this.profileContentType = profileContentType;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public PlayerLanguage getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(PlayerLanguage primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public PlayerDevice getDevice() {
        return device;
    }

    public void setDevice(PlayerDevice device) {
        this.device = device;
    }

    public Integer getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(Integer matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public Integer getTournamentsPlayed() {
        return tournamentsPlayed;
    }

    public void setTournamentsPlayed(Integer tournamentsPlayed) {
        this.tournamentsPlayed = tournamentsPlayed;
    }

    public Integer getMatchWins() {
        return matchWins;
    }

    public void setMatchWins(Integer matchWins) {
        this.matchWins = matchWins;
    }

    public Integer getTournamentWins() {
        return tournamentWins;
    }

    public void setTournamentWins(Integer tournamentWins) {
        this.tournamentWins = tournamentWins;
    }

    public Integer getTournamentTop10s() {
        return tournamentTop10s;
    }

    public void setTournamentTop10s(Integer tournamentTop10s) {
        this.tournamentTop10s = tournamentTop10s;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlayerDataDTO)) {
            return false;
        }

        PlayerDataDTO playerDataDTO = (PlayerDataDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, playerDataDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlayerDataDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", overwatchUsername='" + getOverwatchUsername() + "'" +
            ", profile='" + getProfile() + "'" +
            ", bio='" + getBio() + "'" +
            ", primaryLanguage='" + getPrimaryLanguage() + "'" +
            ", device='" + getDevice() + "'" +
            ", matchesPlayed=" + getMatchesPlayed() +
            ", tournamentsPlayed=" + getTournamentsPlayed() +
            ", matchWins=" + getMatchWins() +
            ", tournamentWins=" + getTournamentWins() +
            ", tournamentTop10s=" + getTournamentTop10s() +
            ", user=" + getUser() +
            "}";
    }
}
