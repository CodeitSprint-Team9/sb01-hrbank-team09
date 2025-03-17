package com.team09.sb01hrbank09.service;

import java.time.Instant;

import com.team09.sb01hrbank09.dto.entityDto.EmployeeDto;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseEmployeeDto;
import com.team09.sb01hrbank09.entity.Department;
import com.team09.sb01hrbank09.entity.Enum.EmployeeStatus;
import com.team09.sb01hrbank09.entity.File;

public interface EmployeeServiceInterface {

	EmployeeDto creatEmployee(String name, String email, String employeeNumber, String position,
		EmployeeStatus status, File file, Department department);

	EmployeeDto findEmployeeById(Long Id);

	CursorPageResponseEmployeeDto findEmployeeList();

	void deleteEmployee(Long id);

	EmployeeDto updateEmployee(EmployeeDto employeeDto);

	//새로운Dto 직원수 추이
	void getEmployeeTrend(Instant startedAt, Instant endedAt, String gap);

	//새로운Dto 직원 분포 조회
	void getEmployeeDistributaion(String group, String status);

	//직원 수 조회
	int countEmployee(String status, Instant startedAt, Instant endedAt);

}
