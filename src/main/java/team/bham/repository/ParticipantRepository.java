package team.bham.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import team.bham.domain.Participant;

/**
 * Spring Data JPA repository for the Participant entity.
 */
@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findAllByTournamentId(Long tournamentId);
}
