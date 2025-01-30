package team.bham.service.mapper;

import org.mapstruct.*;
import team.bham.domain.PlayerData;
import team.bham.domain.User;
import team.bham.service.dto.PlayerDataDTO;
import team.bham.service.dto.UserDTO;

/**
 * Mapper for the entity {@link PlayerData} and its DTO {@link PlayerDataDTO}.
 */
@Mapper(componentModel = "spring")
public interface PlayerDataMapper extends EntityMapper<PlayerDataDTO, PlayerData> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    PlayerDataDTO toDto(PlayerData s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
