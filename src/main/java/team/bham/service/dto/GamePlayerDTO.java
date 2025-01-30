package team.bham.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;
import team.bham.domain.enumeration.GameTeam;

/**
 * A DTO for the {@link team.bham.domain.GamePlayer} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GamePlayerDTO implements Serializable {

    private Long id;

    @NotNull
    private GameTeam team;

    private GameDTO game;

    private PlayerDataDTO playerData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GameTeam getTeam() {
        return team;
    }

    public void setTeam(GameTeam team) {
        this.team = team;
    }

    public GameDTO getGame() {
        return game;
    }

    public void setGame(GameDTO game) {
        this.game = game;
    }

    public PlayerDataDTO getPlayerData() {
        return playerData;
    }

    public void setPlayerData(PlayerDataDTO playerData) {
        this.playerData = playerData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GamePlayerDTO)) {
            return false;
        }

        GamePlayerDTO gamePlayerDTO = (GamePlayerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, gamePlayerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GamePlayerDTO{" +
            "id=" + getId() +
            ", team='" + getTeam() + "'" +
            ", game=" + getGame() +
            ", playerData=" + getPlayerData() +
            "}";
    }
}
