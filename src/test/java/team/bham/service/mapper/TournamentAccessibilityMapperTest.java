package team.bham.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TournamentAccessibilityMapperTest {

    private TournamentAccessibilityMapper tournamentAccessibilityMapper;

    @BeforeEach
    public void setUp() {
        tournamentAccessibilityMapper = new TournamentAccessibilityMapperImpl();
    }
}
