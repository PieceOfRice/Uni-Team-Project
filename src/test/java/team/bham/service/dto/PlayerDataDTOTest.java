package team.bham.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class PlayerDataDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlayerDataDTO.class);
        PlayerDataDTO playerDataDTO1 = new PlayerDataDTO();
        playerDataDTO1.setId(1L);
        PlayerDataDTO playerDataDTO2 = new PlayerDataDTO();
        assertThat(playerDataDTO1).isNotEqualTo(playerDataDTO2);
        playerDataDTO2.setId(playerDataDTO1.getId());
        assertThat(playerDataDTO1).isEqualTo(playerDataDTO2);
        playerDataDTO2.setId(2L);
        assertThat(playerDataDTO1).isNotEqualTo(playerDataDTO2);
        playerDataDTO1.setId(null);
        assertThat(playerDataDTO1).isNotEqualTo(playerDataDTO2);
    }
}
