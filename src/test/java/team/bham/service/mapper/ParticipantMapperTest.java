package team.bham.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParticipantMapperTest {

    private ParticipantMapper participantMapper;

    @BeforeEach
    public void setUp() {
        participantMapper = new ParticipantMapperImpl();
    }
}
