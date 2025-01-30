package team.bham.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class TeamPlayerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TeamPlayer.class);
        TeamPlayer teamPlayer1 = new TeamPlayer();
        teamPlayer1.setId(1L);
        TeamPlayer teamPlayer2 = new TeamPlayer();
        teamPlayer2.setId(teamPlayer1.getId());
        assertThat(teamPlayer1).isEqualTo(teamPlayer2);
        teamPlayer2.setId(2L);
        assertThat(teamPlayer1).isNotEqualTo(teamPlayer2);
        teamPlayer1.setId(null);
        assertThat(teamPlayer1).isNotEqualTo(teamPlayer2);
    }
}
