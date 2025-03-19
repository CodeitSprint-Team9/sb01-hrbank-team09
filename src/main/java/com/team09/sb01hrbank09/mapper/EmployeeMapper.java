package com.team09.sb01hrbank09.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.team09.sb01hrbank09.dto.entityDto.EmployeeDto;
import com.team09.sb01hrbank09.entity.Employee;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
	@Mapping(source = "department.id", target = "departmentId")
	@Mapping(source = "department.name", target = "departmentName")
	@Mapping(source = "file.id", target = "profileImageId")
	EmployeeDto employeeToDto(Employee employee);

	//Employee dtoToEmployee(EmployeeDto employeeDto);
}
