package team.bham.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class TournamentAccessibilityDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TournamentAccessibilityDTO.class);
        TournamentAccessibilityDTO tournamentAccessibilityDTO1 = new TournamentAccessibilityDTO();
        tournamentAccessibilityDTO1.setId(1L);
        TournamentAccessibilityDTO tournamentAccessibilityDTO2 = new TournamentAccessibilityDTO();
        assertThat(tournamentAccessibilityDTO1).isNotEqualTo(tournamentAccessibilityDTO2);
        tournamentAccessibilityDTO2.setId(tournamentAccessibilityDTO1.getId());
        assertThat(tournamentAccessibilityDTO1).isEqualTo(tournamentAccessibilityDTO2);
        tournamentAccessibilityDTO2.setId(2L);
        assertThat(tournamentAccessibilityDTO1).isNotEqualTo(tournamentAccessibilityDTO2);
        tournamentAccessibilityDTO1.setId(null);
        assertThat(tournamentAccessibilityDTO1).isNotEqualTo(tournamentAccessibilityDTO2);
    }
}
