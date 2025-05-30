package com.team09.sb01hrbank09.service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.team09.sb01hrbank09.dto.entityDto.EmployeeDistributionDto;
import com.team09.sb01hrbank09.dto.entityDto.EmployeeDto;
import com.team09.sb01hrbank09.dto.entityDto.EmployeeTrendDto;
import com.team09.sb01hrbank09.dto.request.EmployeeCreateRequest;
import com.team09.sb01hrbank09.dto.request.EmployeeUpdateRequest;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseEmployeeDto;
import com.team09.sb01hrbank09.entity.Enum.EmployeeStatus;

public interface EmployeeServiceInterface {

	EmployeeDto creatEmployee(EmployeeCreateRequest employeeCreateRequest, MultipartFile profileImg, String ipAdress
	) throws IOException;

	EmployeeDto findEmployeeById(Long Id);

	List<EmployeeDto> getEmployeeAllList();

	CursorPageResponseEmployeeDto findEmployeeList(String nameOrEmail, String employeeNumber, String departmentName,
		String position, LocalDate hireDateFrom, LocalDate hireDateTo, EmployeeStatus status, Long idAfter,
		String cursor, int size,
		String sortField, String sortDirection);

	boolean deleteEmployee(Long id, String ipAdress);

	EmployeeDto updateEmployee(Long id, EmployeeUpdateRequest employeeUpdateRequest, MultipartFile profileImg,
		String ipAdress
	) throws
		IOException;

	//새로운Dto 직원수 추이
	List<EmployeeTrendDto> getEmployeeTrend(LocalDate startedAt, LocalDate endedAt, String gap);

	//새로운Dto 직원 분포 조회
	List<EmployeeDistributionDto> getEmployeeDistributaion(String groupBy, String status);

	//직원 수 조회
	Long countEmployee(String status, LocalDate startedAt, LocalDate endedAt);

	Instant getUpdateTime();

	@Transactional(readOnly = true)
	Stream<EmployeeDto> getEmployeeStream();
}
