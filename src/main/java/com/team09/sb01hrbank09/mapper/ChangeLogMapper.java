package com.team09.sb01hrbank09.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.team09.sb01hrbank09.dto.entityDto.ChangeLogDto;
import com.team09.sb01hrbank09.entity.ChangeLog;

@Mapper(componentModel = "spring")
public interface ChangeLogMapper {

	@Mapping(source = "employee.id", target = "employeeId")
	ChangeLogDto changeLogToDto(ChangeLog changeLog);

	//ChangeLog dtoToChangeLog(ChangeLogDto changeLogDto);

}
