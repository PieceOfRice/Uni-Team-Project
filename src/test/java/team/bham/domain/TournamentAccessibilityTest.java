package team.bham.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class TournamentAccessibilityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TournamentAccessibility.class);
        TournamentAccessibility tournamentAccessibility1 = new TournamentAccessibility();
        tournamentAccessibility1.setId(1L);
        TournamentAccessibility tournamentAccessibility2 = new TournamentAccessibility();
        tournamentAccessibility2.setId(tournamentAccessibility1.getId());
        assertThat(tournamentAccessibility1).isEqualTo(tournamentAccessibility2);
        tournamentAccessibility2.setId(2L);
        assertThat(tournamentAccessibility1).isNotEqualTo(tournamentAccessibility2);
        tournamentAccessibility1.setId(null);
        assertThat(tournamentAccessibility1).isNotEqualTo(tournamentAccessibility2);
    }
}
