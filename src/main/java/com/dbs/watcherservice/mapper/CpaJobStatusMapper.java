package com.dbs.watcherservice.mapper;

import com.dbs.watcherservice.dto.CpaJobStatusDto;
import com.dbs.watcherservice.datasource.primary.model.CpaJobStatus;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CpaJobStatusMapper {
    CpaJobStatusMapper INSTANCE = Mappers.getMapper(CpaJobStatusMapper.class);

    CpaJobStatus toEntity(CpaJobStatusDto cpaJobStatusDto);

    CpaJobStatusDto toDTO(CpaJobStatus cpaJobStatus);


    List<CpaJobStatus> toEntity(List<CpaJobStatusDto> cpaJobStatusDtos);

    List<CpaJobStatusDto> toDTO(List<CpaJobStatus> cpaJobStatuses);
}
