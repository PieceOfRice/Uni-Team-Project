package team.bham.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;
import team.bham.domain.enumeration.AccessStatus;
import team.bham.domain.enumeration.TournamentBracketType;
import team.bham.domain.enumeration.TournamentSetting;

/**
 * A DTO for the {@link team.bham.domain.Tournament} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TournamentDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 3, max = 40)
    private String name;

    @Size(max = 5000)
    private String description;

    private Float prizePool;

    private Float entryFee;

    @NotNull
    private ZonedDateTime startTime;

    private ZonedDateTime endTime;

    private String location;

    private TournamentBracketType bracketFormat;

    @NotNull
    private AccessStatus accessStatus;

    @NotNull
    private Boolean isLive;

    @NotNull
    private Boolean ended;

    @Lob
    private byte[] banner;

    private String bannerContentType;

    @NotNull
    private Integer gamesPerMatch;

    private Integer maxParticipants;

    @NotNull
    private TournamentSetting tournamentSetting;

    private PlayerDataDTO creator;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrizePool() {
        return prizePool;
    }

    public void setPrizePool(Float prizePool) {
        this.prizePool = prizePool;
    }

    public Float getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(Float entryFee) {
        this.entryFee = entryFee;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public TournamentBracketType getBracketFormat() {
        return bracketFormat;
    }

    public void setBracketFormat(TournamentBracketType bracketFormat) {
        this.bracketFormat = bracketFormat;
    }

    public AccessStatus getAccessStatus() {
        return accessStatus;
    }

    public void setAccessStatus(AccessStatus accessStatus) {
        this.accessStatus = accessStatus;
    }

    public Boolean getIsLive() {
        return isLive;
    }

    public void setIsLive(Boolean isLive) {
        this.isLive = isLive;
    }

    public Boolean getEnded() {
        return ended;
    }

    public void setEnded(Boolean ended) {
        this.ended = ended;
    }

    public byte[] getBanner() {
        return banner;
    }

    public void setBanner(byte[] banner) {
        this.banner = banner;
    }

    public String getBannerContentType() {
        return bannerContentType;
    }

    public void setBannerContentType(String bannerContentType) {
        this.bannerContentType = bannerContentType;
    }

    public Integer getGamesPerMatch() {
        return gamesPerMatch;
    }

    public void setGamesPerMatch(Integer gamesPerMatch) {
        this.gamesPerMatch = gamesPerMatch;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public TournamentSetting getTournamentSetting() {
        return tournamentSetting;
    }

    public void setTournamentSetting(TournamentSetting tournamentSetting) {
        this.tournamentSetting = tournamentSetting;
    }

    public PlayerDataDTO getCreator() {
        return creator;
    }

    public void setCreator(PlayerDataDTO creator) {
        this.creator = creator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TournamentDTO)) {
            return false;
        }

        TournamentDTO tournamentDTO = (TournamentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tournamentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TournamentDTO{" +
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
            ", gamesPerMatch=" + getGamesPerMatch() +
            ", maxParticipants=" + getMaxParticipants() +
            ", tournamentSetting='" + getTournamentSetting() + "'" +
            ", creator=" + getCreator() +
            "}";
    }
}
