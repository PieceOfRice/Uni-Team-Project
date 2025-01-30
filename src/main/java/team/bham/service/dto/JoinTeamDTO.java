package team.bham.service.dto;

import team.bham.domain.enumeration.TeamRole;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link team.bham.domain.Team} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class JoinTeamDTO implements Serializable {

    @NotNull
    private Long id;

    @NotNull
    private TeamRole role;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TeamRole getRole() {return role;}

    public void setRole(TeamRole role) {this.role = role;}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JoinTeamDTO)) {
            return false;
        }

        JoinTeamDTO teamDTO = (JoinTeamDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, teamDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "JoinTeamDTO{" +
            "id=" + getId() +
            "}";
    }
}
