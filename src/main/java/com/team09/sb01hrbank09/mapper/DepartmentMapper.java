package com.team09.sb01hrbank09.mapper;

import org.mapstruct.Mapper;

import com.team09.sb01hrbank09.dto.entityDto.DepartmentDto;
import com.team09.sb01hrbank09.entity.Department;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
	DepartmentDto departmentToDto(Department department);

	//Department dtoToDepartment(DepartmentDto departmentDto);
}
