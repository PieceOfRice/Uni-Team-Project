package team.bham.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MatchMapperTest {

    private MatchMapper matchMapper;

    @BeforeEach
    public void setUp() {
        matchMapper = new MatchMapperImpl();
    }
}
