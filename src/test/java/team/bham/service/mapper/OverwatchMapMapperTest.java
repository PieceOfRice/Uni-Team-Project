package team.bham.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OverwatchMapMapperTest {

    private OverwatchMapMapper overwatchMapMapper;

    @BeforeEach
    public void setUp() {
        overwatchMapMapper = new OverwatchMapMapperImpl();
    }
}
