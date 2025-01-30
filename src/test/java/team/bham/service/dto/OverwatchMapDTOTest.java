package team.bham.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class OverwatchMapDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OverwatchMapDTO.class);
        OverwatchMapDTO overwatchMapDTO1 = new OverwatchMapDTO();
        overwatchMapDTO1.setId(1L);
        OverwatchMapDTO overwatchMapDTO2 = new OverwatchMapDTO();
        assertThat(overwatchMapDTO1).isNotEqualTo(overwatchMapDTO2);
        overwatchMapDTO2.setId(overwatchMapDTO1.getId());
        assertThat(overwatchMapDTO1).isEqualTo(overwatchMapDTO2);
        overwatchMapDTO2.setId(2L);
        assertThat(overwatchMapDTO1).isNotEqualTo(overwatchMapDTO2);
        overwatchMapDTO1.setId(null);
        assertThat(overwatchMapDTO1).isNotEqualTo(overwatchMapDTO2);
    }
}
