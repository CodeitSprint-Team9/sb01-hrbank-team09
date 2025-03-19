package com.team09.sb01hrbank09.mapper;

import org.mapstruct.Mapper;

import com.team09.sb01hrbank09.dto.entityDto.ChangeLogDto;
import com.team09.sb01hrbank09.entity.ChangeLog;

@Mapper(componentModel = "spring")
public interface ChangeLogMapper {

	ChangeLogDto changeLogToDto(ChangeLog changeLog);

	//ChangeLog dtoToChangeLog(ChangeLogDto changeLogDto);

}
