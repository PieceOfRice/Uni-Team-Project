package team.bham.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class PlayerDataTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlayerData.class);
        PlayerData playerData1 = new PlayerData();
        playerData1.setId(1L);
        PlayerData playerData2 = new PlayerData();
        playerData2.setId(playerData1.getId());
        assertThat(playerData1).isEqualTo(playerData2);
        playerData2.setId(2L);
        assertThat(playerData1).isNotEqualTo(playerData2);
        playerData1.setId(null);
        assertThat(playerData1).isNotEqualTo(playerData2);
    }
}
