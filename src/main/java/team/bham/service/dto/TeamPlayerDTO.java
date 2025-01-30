package team.bham.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;
import team.bham.domain.enumeration.TeamRole;

/**
 * A DTO for the {@link team.bham.domain.TeamPlayer} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeamPlayerDTO implements Serializable {

    private Long id;

    @NotNull
    private TeamRole role;

    private PlayerDataDTO player;

    private TeamDTO team;

    @NotNull
    private boolean accepted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TeamRole getRole() {
        return role;
    }

    public void setRole(TeamRole role) {
        this.role = role;
    }

    public PlayerDataDTO getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDataDTO player) {
        this.player = player;
    }

    public TeamDTO getTeam() {
        return team;
    }

    public void setTeam(TeamDTO team) {
        this.team = team;
    }

    public boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TeamPlayerDTO)) {
            return false;
        }

        TeamPlayerDTO teamPlayerDTO = (TeamPlayerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, teamPlayerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeamPlayerDTO{" +
            "id=" + getId() +
            ", role='" + getRole() + "'" +
            ", player=" + getPlayer() +
            ", team=" + getTeam() +
            ", accepted=" + getAccepted() +
            "}";
    }
}
