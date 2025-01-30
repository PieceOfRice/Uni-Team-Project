package team.bham.service.mapper;

import org.mapstruct.*;
import team.bham.domain.Tournament;
import team.bham.domain.TournamentAccessibility;
import team.bham.service.dto.TournamentAccessibilityDTO;
import team.bham.service.dto.TournamentDTO;

/**
 * Mapper for the entity {@link TournamentAccessibility} and its DTO {@link TournamentAccessibilityDTO}.
 */
@Mapper(componentModel = "spring")
public interface TournamentAccessibilityMapper extends EntityMapper<TournamentAccessibilityDTO, TournamentAccessibility> {
    @Mapping(target = "tournament", source = "tournament", qualifiedByName = "tournamentId")
    TournamentAccessibilityDTO toDto(TournamentAccessibility s);

    @Named("tournamentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TournamentDTO toDtoTournamentId(Tournament tournament);
}
