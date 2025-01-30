package team.bham.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team.bham.domain.Team;
import team.bham.domain.Tournament;
import team.bham.domain.TournamentAccessibility;

/**
 * Spring Data JPA repository for the Tournament entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    Optional<Tournament> findOneById(long id);
    Optional<Tournament> findOneByName(String name);

    @Query("SELECT t FROM Tournament t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :namePattern, '%'))")
    List<Tournament> getTournamentByNameLike(@Param("namePattern") String namePattern);
}
