package team.bham.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import team.bham.web.rest.TestUtil;

class GamePlayerDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(GamePlayerDTO.class);
        GamePlayerDTO gamePlayerDTO1 = new GamePlayerDTO();
        gamePlayerDTO1.setId(1L);
        GamePlayerDTO gamePlayerDTO2 = new GamePlayerDTO();
        assertThat(gamePlayerDTO1).isNotEqualTo(gamePlayerDTO2);
        gamePlayerDTO2.setId(gamePlayerDTO1.getId());
        assertThat(gamePlayerDTO1).isEqualTo(gamePlayerDTO2);
        gamePlayerDTO2.setId(2L);
        assertThat(gamePlayerDTO1).isNotEqualTo(gamePlayerDTO2);
        gamePlayerDTO1.setId(null);
        assertThat(gamePlayerDTO1).isNotEqualTo(gamePlayerDTO2);
    }
}
