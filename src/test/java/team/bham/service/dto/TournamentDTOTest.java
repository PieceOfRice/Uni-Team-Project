package team.bham.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class TournamentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TournamentDTO.class);
        TournamentDTO tournamentDTO1 = new TournamentDTO();
        tournamentDTO1.setId(1L);
        TournamentDTO tournamentDTO2 = new TournamentDTO();
        assertThat(tournamentDTO1).isNotEqualTo(tournamentDTO2);
        tournamentDTO2.setId(tournamentDTO1.getId());
        assertThat(tournamentDTO1).isEqualTo(tournamentDTO2);
        tournamentDTO2.setId(2L);
        assertThat(tournamentDTO1).isNotEqualTo(tournamentDTO2);
        tournamentDTO1.setId(null);
        assertThat(tournamentDTO1).isNotEqualTo(tournamentDTO2);
    }
}
