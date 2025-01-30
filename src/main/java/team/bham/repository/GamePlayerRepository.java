package team.bham.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import team.bham.domain.GamePlayer;
import team.bham.domain.PlayerData;

/**
 * Spring Data JPA repository for the GamePlayer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {
    List<GamePlayer> findAllByGameId(Long gameId);
    List<GamePlayer> findAllByPlayerData(PlayerData playerData);

    void deleteByGameId(Long gameId);
}
