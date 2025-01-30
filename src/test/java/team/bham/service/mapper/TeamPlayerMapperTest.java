package team.bham.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TeamPlayerMapperTest {

    private TeamPlayerMapper teamPlayerMapper;

    @BeforeEach
    public void setUp() {
        teamPlayerMapper = new TeamPlayerMapperImpl();
    }
}
