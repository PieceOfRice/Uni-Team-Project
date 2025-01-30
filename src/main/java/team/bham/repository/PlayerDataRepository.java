package team.bham.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team.bham.domain.PlayerData;
import team.bham.domain.User;

/**
 * Spring Data JPA repository for the PlayerData entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlayerDataRepository extends JpaRepository<PlayerData, Long> {
    Optional<PlayerData> findOneById(Long id);
    Optional<PlayerData> findOneByUser(User user);
    Optional<PlayerData> findOneByUserId(Long userId);
    Optional<PlayerData> findOneByName(String name);

    @Query(
        "SELECT p FROM PlayerData p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :namePattern, '%')) OR LOWER(p.overwatchUsername) LIKE LOWER(CONCAT('%', :userPattern, '%'))"
    )
    List<PlayerData> findAllByNameLikeOrOverwatchUsernameLike(
        @Param("namePattern") String namePattern,
        @Param("userPattern") String userPattern
    );

    @Query(
        "SELECT p, te FROM PlayerData p JOIN TeamPlayer tp ON p.id = tp.player.id JOIN Team te ON te.id = tp.team.id WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :namePattern, '%')) OR LOWER(p.overwatchUsername) LIKE LOWER(CONCAT('%', :userPattern, '%'))"
    )
    List<Object[]> findAllByNameLikeOrOverwatchUsernameLikeWithTeams(
        @Param("namePattern") String namePattern,
        @Param("userPattern") String userPattern
    );

    // name or username
    List<PlayerData> findAllByIdIn(List<Long> playerIds);

    // do the SQL query on the right backend

    @Query("SELECT pd, tp.role FROM PlayerData pd JOIN TeamPlayer tp ON pd.id = tp.player.id WHERE tp.team.id = :teamId")
    List<Object[]> findAllByTeamIdWithRole(@Param("teamId") Long teamId);
}
