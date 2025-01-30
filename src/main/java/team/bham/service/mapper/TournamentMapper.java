package team.bham.service.mapper;

import org.mapstruct.*;
import team.bham.domain.PlayerData;
import team.bham.domain.Tournament;
import team.bham.service.dto.PlayerDataDTO;
import team.bham.service.dto.TournamentDTO;

/**
 * Mapper for the entity {@link Tournament} and its DTO {@link TournamentDTO}.
 */
@Mapper(componentModel = "spring")
public interface TournamentMapper extends EntityMapper<TournamentDTO, Tournament> {
    @Mapping(target = "creator", source = "creator", qualifiedByName = "playerDataId")
    TournamentDTO toDto(Tournament s);

    @Named("playerDataId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PlayerDataDTO toDtoPlayerDataId(PlayerData playerData);
}
