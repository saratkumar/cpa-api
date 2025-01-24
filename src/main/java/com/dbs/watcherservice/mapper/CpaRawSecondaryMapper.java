package com.dbs.watcherservice.mapper;

import com.dbs.watcherservice.datasource.secondary.model.CpaRawSecondary;
import com.dbs.watcherservice.dto.CpaRawSecondaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CpaRawSecondaryMapper {
    CpaRawSecondaryMapper INSTANCE = Mappers.getMapper(CpaRawSecondaryMapper.class);

    CpaRawSecondary toEntity(CpaRawSecondaryDto cpaRawSecondaryDto);

    CpaRawSecondaryDto toDTO(CpaRawSecondary cpaRaw);

    List<CpaRawSecondary> toEntity(List<CpaRawSecondaryDto> cpaRawSecondaryDtos);

    List<CpaRawSecondaryDto> toDTO(List<CpaRawSecondary> cpaRaw);
}
