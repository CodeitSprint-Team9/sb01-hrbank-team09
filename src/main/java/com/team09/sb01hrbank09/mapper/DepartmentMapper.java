package com.team09.sb01hrbank09.mapper;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import org.mapstruct.Mapper;

import com.team09.sb01hrbank09.dto.entityDto.DepartmentDto;
import com.team09.sb01hrbank09.entity.Department;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
	DepartmentDto departmentToDto(Department department);

	//Department dtoToDepartment(DepartmentDto departmentDto);
}
