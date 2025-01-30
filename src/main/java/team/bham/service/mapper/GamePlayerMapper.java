package team.bham.service.mapper;

import org.mapstruct.*;
import team.bham.domain.Game;
import team.bham.domain.GamePlayer;
import team.bham.domain.PlayerData;
import team.bham.service.dto.GameDTO;
import team.bham.service.dto.GamePlayerDTO;
import team.bham.service.dto.PlayerDataDTO;

/**
 * Mapper for the entity {@link GamePlayer} and its DTO {@link GamePlayerDTO}.
 */
@Mapper(componentModel = "spring")
public interface GamePlayerMapper extends EntityMapper<GamePlayerDTO, GamePlayer> {
    @Mapping(target = "game", source = "game", qualifiedByName = "gameId")
    @Mapping(target = "playerData", source = "playerData", qualifiedByName = "playerDataId")
    GamePlayerDTO toDto(GamePlayer s);

    @Named("gameId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    GameDTO toDtoGameId(Game game);

    @Named("playerDataId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PlayerDataDTO toDtoPlayerDataId(PlayerData playerData);
}
