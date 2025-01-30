package team.bham.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import team.bham.domain.OverwatchMap;

/**
 * Spring Data JPA repository for the OverwatchMap entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OverwatchMapRepository extends JpaRepository<OverwatchMap, Long> {}
