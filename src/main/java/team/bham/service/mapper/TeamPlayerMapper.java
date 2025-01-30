package team.bham.service.mapper;

import org.mapstruct.*;
import team.bham.domain.PlayerData;
import team.bham.domain.Team;
import team.bham.domain.TeamPlayer;
import team.bham.service.dto.PlayerDataDTO;
import team.bham.service.dto.TeamDTO;
import team.bham.service.dto.TeamPlayerDTO;

/**
 * Mapper for the entity {@link TeamPlayer} and its DTO {@link TeamPlayerDTO}.
 */
@Mapper(componentModel = "spring")
public interface TeamPlayerMapper extends EntityMapper<TeamPlayerDTO, TeamPlayer> {
    @Mapping(target = "player", source = "player", qualifiedByName = "playerDataId")
    @Mapping(target = "team", source = "team", qualifiedByName = "teamId")
    TeamPlayerDTO toDto(TeamPlayer s);

    @Named("playerDataId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PlayerDataDTO toDtoPlayerDataId(PlayerData playerData);

    @Named("teamId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TeamDTO toDtoTeamId(Team team);
}
