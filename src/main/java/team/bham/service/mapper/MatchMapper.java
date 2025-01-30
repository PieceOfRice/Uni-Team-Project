package team.bham.service.mapper;

import org.mapstruct.*;
import team.bham.domain.Match;
import team.bham.domain.Team;
import team.bham.domain.Tournament;
import team.bham.service.dto.MatchDTO;
import team.bham.service.dto.TeamDTO;
import team.bham.service.dto.TournamentDTO;

/**
 * Mapper for the entity {@link Match} and its DTO {@link MatchDTO}.
 */
@Mapper(componentModel = "spring")
public interface MatchMapper extends EntityMapper<MatchDTO, Match> {
    @Mapping(target = "tournament", source = "tournament", qualifiedByName = "tournamentId")
    @Mapping(target = "teamOne", source = "teamOne", qualifiedByName = "teamId")
    @Mapping(target = "teamTwo", source = "teamTwo", qualifiedByName = "teamId")
    MatchDTO toDto(Match s);

    @Named("tournamentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TournamentDTO toDtoTournamentId(Tournament tournament);

    @Named("teamId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TeamDTO toDtoTeamId(Team team);
}
