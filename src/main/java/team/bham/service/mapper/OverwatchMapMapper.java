package team.bham.service.mapper;

import org.mapstruct.*;
import team.bham.domain.OverwatchMap;
import team.bham.service.dto.OverwatchMapDTO;

/**
 * Mapper for the entity {@link OverwatchMap} and its DTO {@link OverwatchMapDTO}.
 */
@Mapper(componentModel = "spring")
public interface OverwatchMapMapper extends EntityMapper<OverwatchMapDTO, OverwatchMap> {}
