package team.bham.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import team.bham.domain.Team;
import team.bham.domain.TournamentAccessibility;

/**
 * Spring Data JPA repository for the TournamentAccessibility entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TournamentAccessibilityRepository extends JpaRepository<TournamentAccessibility, Long> {
    Optional<TournamentAccessibility> findOneById(long id);
}
