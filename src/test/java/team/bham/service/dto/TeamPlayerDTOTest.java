package team.bham.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class TeamPlayerDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TeamPlayerDTO.class);
        TeamPlayerDTO teamPlayerDTO1 = new TeamPlayerDTO();
        teamPlayerDTO1.setId(1L);
        TeamPlayerDTO teamPlayerDTO2 = new TeamPlayerDTO();
        assertThat(teamPlayerDTO1).isNotEqualTo(teamPlayerDTO2);
        teamPlayerDTO2.setId(teamPlayerDTO1.getId());
        assertThat(teamPlayerDTO1).isEqualTo(teamPlayerDTO2);
        teamPlayerDTO2.setId(2L);
        assertThat(teamPlayerDTO1).isNotEqualTo(teamPlayerDTO2);
        teamPlayerDTO1.setId(null);
        assertThat(teamPlayerDTO1).isNotEqualTo(teamPlayerDTO2);
    }
}
