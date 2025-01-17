package com.dbs.watcherservice.mapper;

import com.dbs.watcherservice.dto.CpaRawDto;
import com.dbs.watcherservice.model.CpaRaw;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CpaRawMapper {
    CpaRawMapper INSTANCE = Mappers.getMapper(CpaRawMapper.class);

    CpaRaw toEntity(CpaRawDto cpaRawDto);

    CpaRawDto toDTO(CpaRaw cpaRaw);


    List<CpaRaw> toEntity(List<CpaRawDto> cpaRawDto);

    List<CpaRawDto> toDTO(List<CpaRaw> cpaRaw);
}