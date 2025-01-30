package team.bham.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameMapperTest {

    private GameMapper gameMapper;

    @BeforeEach
    public void setUp() {
        gameMapper = new GameMapperImpl();
    }
}
