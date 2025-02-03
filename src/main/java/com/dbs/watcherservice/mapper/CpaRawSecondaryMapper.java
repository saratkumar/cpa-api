package com.dbs.watcherservice.mapper;

import com.dbs.watcherservice.datasource.secondary.model.CpaRawCon;
import com.dbs.watcherservice.dto.CpaRawSecondaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CpaRawSecondaryMapper {
    CpaRawSecondaryMapper INSTANCE = Mappers.getMapper(CpaRawSecondaryMapper.class);

    CpaRawCon toEntity(CpaRawSecondaryDto cpaRawSecondaryDto);

    CpaRawSecondaryDto toDTO(CpaRawCon cpaRaw);

    List<CpaRawCon> toEntity(List<CpaRawSecondaryDto> cpaRawSecondaryDtos);

    List<CpaRawSecondaryDto> toDTO(List<CpaRawCon> cpaRaw);
}
