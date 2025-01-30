package team.bham.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team.bham.domain.PlayerData;
import team.bham.domain.Team;
import team.bham.domain.TeamPlayer;
import team.bham.domain.enumeration.TeamRole;

/**
 * Spring Data JPA repository for the TeamPlayer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TeamPlayerRepository extends JpaRepository<TeamPlayer, Long> {
    Optional<TeamPlayer> findOneByTeamAndPlayer(Team Team, PlayerData playerData);
    Optional<TeamPlayer> findOneByPlayerIdAndTeamId(Long pdId, Long teamId);
    Optional<TeamPlayer> findOneByPlayerId(Long playerId);
    List<TeamPlayer> findAllByTeam(Team team);
    List<TeamPlayer> findAllByTeamId(long id);
    List<TeamPlayer> findAllByRoleAndPlayerId(TeamRole role, Long pdId);

    @Query("SELECT tp FROM TeamPlayer tp JOIN FETCH tp.player playerData WHERE tp.team.id = :teamId")
    List<TeamPlayer> findAllByTeamIdWithPlayerData(@Param("teamId") long teamId);

    List<TeamPlayer> findAllByPlayer(PlayerData playerData);
}
