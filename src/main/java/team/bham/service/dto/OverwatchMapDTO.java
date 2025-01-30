package team.bham.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;
import team.bham.domain.enumeration.MapMode;

/**
 * A DTO for the {@link team.bham.domain.OverwatchMap} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OverwatchMapDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private MapMode mode;

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

    public MapMode getMode() {
        return mode;
    }

    public void setMode(MapMode mode) {
        this.mode = mode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OverwatchMapDTO)) {
            return false;
        }

        OverwatchMapDTO overwatchMapDTO = (OverwatchMapDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, overwatchMapDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OverwatchMapDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", mode='" + getMode() + "'" +
            "}";
    }
}
