package team.bham.service.mapper;

import org.mapstruct.*;
import team.bham.domain.Team;
import team.bham.service.dto.TeamDTO;

/**
 * Mapper for the entity {@link Team} and its DTO {@link TeamDTO}.
 */
@Mapper(componentModel = "spring")
public interface TeamMapper extends EntityMapper<TeamDTO, Team> {}
