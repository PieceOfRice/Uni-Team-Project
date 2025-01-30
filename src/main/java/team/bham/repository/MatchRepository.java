package team.bham.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team.bham.domain.Match;
import team.bham.domain.Team;
import team.bham.domain.Tournament;

/**
 * Spring Data JPA repository for the Match entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findAllByTeamOneOrTeamTwo(Team teamOne, Team teamTwo);

    // @Query(
    //     value = "SELECT * FROM (SELECT * FROM MATCH WHERE TOURNAMENT_ID= :tournamentId AND SCORE_APPROVED=TRUE AND SCORE_SUBMITTED=TRUE ORDER BY MATCH_INDEX DESC) WHERE ROWNUM <=3",
    //     nativeQuery = true
    // )
    @Query(
        "select m from Match m WHERE m.tournament.id =:tournamentId AND m.scoreApproved=true AND m.scoreSubmitted=true ORDER BY m.matchIndex DESC"
    )
    List<Match> findFinalThreeMatchesByTournamentId(@Param("tournamentId") long tournamentId);

    @Query(
        value = "SELECT * FROM (SELECT * FROM MATCH WHERE TOURNAMENT_ID= :tournamentId AND SCORE_APPROVED=TRUE AND SCORE_SUBMITTED=TRUE ORDER BY MATCH_INDEX DESC) WHERE ROWNUM <=1",
        nativeQuery = true
    )
    Optional<Match> findFinalMatchByTournamentId(@Param("tournamentId") long tournamentId);

    List<Match> findAllByTournament(Tournament tournament);
}
