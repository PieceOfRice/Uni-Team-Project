package team.bham.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerDataMapperTest {

    private PlayerDataMapper playerDataMapper;

    @BeforeEach
    public void setUp() {
        playerDataMapper = new PlayerDataMapperImpl();
    }
}
