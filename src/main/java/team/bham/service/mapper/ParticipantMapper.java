package team.bham.service.mapper;

import org.mapstruct.*;
import team.bham.domain.Participant;
import team.bham.domain.Team;
import team.bham.domain.Tournament;
import team.bham.service.dto.ParticipantDTO;
import team.bham.service.dto.TeamDTO;
import team.bham.service.dto.TournamentDTO;

/**
 * Mapper for the entity {@link Participant} and its DTO {@link ParticipantDTO}.
 */
@Mapper(componentModel = "spring")
public interface ParticipantMapper extends EntityMapper<ParticipantDTO, Participant> {
    @Mapping(target = "team", source = "team", qualifiedByName = "teamId")
    @Mapping(target = "tournament", source = "tournament", qualifiedByName = "tournamentId")
    ParticipantDTO toDto(Participant s);

    @Named("teamId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TeamDTO toDtoTeamId(Team team);

    @Named("tournamentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TournamentDTO toDtoTournamentId(Tournament tournament);
}
