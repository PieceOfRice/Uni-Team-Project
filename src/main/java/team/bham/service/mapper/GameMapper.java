package team.bham.service.mapper;

import org.mapstruct.*;
import team.bham.domain.Game;
import team.bham.domain.Match;
import team.bham.domain.OverwatchMap;
import team.bham.service.dto.GameDTO;
import team.bham.service.dto.MatchDTO;
import team.bham.service.dto.OverwatchMapDTO;

/**
 * Mapper for the entity {@link Game} and its DTO {@link GameDTO}.
 */
@Mapper(componentModel = "spring")
public interface GameMapper extends EntityMapper<GameDTO, Game> {
    @Mapping(target = "overwatchMap", source = "overwatchMap", qualifiedByName = "overwatchMapId")
    @Mapping(target = "match", source = "match", qualifiedByName = "matchId")
    GameDTO toDto(Game s);

    @Named("overwatchMapId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OverwatchMapDTO toDtoOverwatchMapId(OverwatchMap overwatchMap);

    @Named("matchId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MatchDTO toDtoMatchId(Match match);
}
