package team.bham.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TournamentMapperTest {

    private TournamentMapper tournamentMapper;

    @BeforeEach
    public void setUp() {
        tournamentMapper = new TournamentMapperImpl();
    }
}
