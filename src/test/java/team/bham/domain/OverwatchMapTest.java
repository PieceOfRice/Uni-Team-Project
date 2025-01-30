package team.bham.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class OverwatchMapTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OverwatchMap.class);
        OverwatchMap overwatchMap1 = new OverwatchMap();
        overwatchMap1.setId(1L);
        OverwatchMap overwatchMap2 = new OverwatchMap();
        overwatchMap2.setId(overwatchMap1.getId());
        assertThat(overwatchMap1).isEqualTo(overwatchMap2);
        overwatchMap2.setId(2L);
        assertThat(overwatchMap1).isNotEqualTo(overwatchMap2);
        overwatchMap1.setId(null);
        assertThat(overwatchMap1).isNotEqualTo(overwatchMap2);
    }
}
