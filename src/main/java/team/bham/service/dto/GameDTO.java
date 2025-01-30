package team.bham.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link team.bham.domain.Game} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GameDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 0)
    private Integer order;

    @NotNull
    private Integer scoreOne;

    @NotNull
    private Integer scoreTwo;

    private String replay;

    private OverwatchMapDTO overwatchMap;

    private MatchDTO match;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getScoreOne() {
        return scoreOne;
    }

    public void setScoreOne(Integer scoreOne) {
        this.scoreOne = scoreOne;
    }

    public Integer getScoreTwo() {
        return scoreTwo;
    }

    public void setScoreTwo(Integer scoreTwo) {
        this.scoreTwo = scoreTwo;
    }

    public String getReplay() {
        return replay;
    }

    public void setReplay(String replay) {
        this.replay = replay;
    }

    public OverwatchMapDTO getOverwatchMap() {
        return overwatchMap;
    }

    public void setOverwatchMap(OverwatchMapDTO overwatchMap) {
        this.overwatchMap = overwatchMap;
    }

    public MatchDTO getMatch() {
        return match;
    }

    public void setMatch(MatchDTO match) {
        this.match = match;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GameDTO)) {
            return false;
        }

        GameDTO gameDTO = (GameDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, gameDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GameDTO{" +
            "id=" + getId() +
            ", order=" + getOrder() +
            ", scoreOne=" + getScoreOne() +
            ", scoreTwo=" + getScoreTwo() +
            ", scoreTwo=" + getReplay() +
            ", overwatchMap=" + getOverwatchMap() +
            ", match=" + getMatch() +
            "}";
    }
}
