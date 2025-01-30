package team.bham.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team.bham.domain.Team;

/**
 * Spring Data JPA repository for the Team entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findOneByName(String name);
    Optional<Team> findOneById(long id);

    // SELECT TEAM.ID,TEAM.NAME,TEAM.LOGO,TEAM.LOGO_CONTENT_TYPE FROM TEAM LEFT JOIN TEAM_PLAYER ON TEAM_PLAYER.TEAM_ID=TEAM.ID WHERE TEAM_PLAYER.PLAYER_ID=1051

    @Query(
        value = "SELECT TEAM.* FROM TEAM LEFT JOIN TEAM_PLAYER ON TEAM_PLAYER.TEAM_ID=TEAM.ID WHERE TEAM_PLAYER.PLAYER_ID=:playerId",
        nativeQuery = true
    )
    List<Team> findAllByPlayerId(@Param("playerId") long playerId);

    @Query("SELECT t FROM Team t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :namePattern, '%'))")
    List<Team> getTeamByNameLike(@Param("namePattern") String namePattern);
    // @Query()
    // List<Team> getWinningTeamsByTournamentId(@Param("tournamentId") long tournamentId);
}
