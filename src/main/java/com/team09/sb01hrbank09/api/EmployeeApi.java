package com.team09.sb01hrbank09.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.team09.sb01hrbank09.dto.entityDto.EmployeeDistributionDto;
import com.team09.sb01hrbank09.dto.entityDto.EmployeeDto;
import com.team09.sb01hrbank09.dto.entityDto.EmployeeTrendDto;
import com.team09.sb01hrbank09.dto.request.EmployeeCreateRequest;
import com.team09.sb01hrbank09.dto.request.EmployeeUpdateRequest;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseEmployeeDto;
import com.team09.sb01hrbank09.entity.Enum.EmployeeStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "직원 관리", description = "직원 관리 API")
public interface EmployeeApi {

	@Operation(summary = "직원 등록", description = "새로운 직원을 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "등록 성공"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	ResponseEntity<EmployeeDto> creatEmployee(EmployeeCreateRequest employee, MultipartFile profile,
		HttpServletRequest request) throws IOException;

	@Operation(summary = "직원 목록 조회", description = "직원 목록을 조회합니다.")
	ResponseEntity<CursorPageResponseEmployeeDto> findEmployeeList(String nameOrEmail, String employeeNumber,
		String departmentName, String position, LocalDate hireDateFrom, LocalDate hireDateTo, EmployeeStatus status,
		Long idAfter, String cursor, int size, String sortField, String sortDirection);

	@Operation(summary = "직원 상세 조회", description = "특정 직원 정보를 조회합니다.")
	ResponseEntity<EmployeeDto> findEmployeeById(Long id);

	@Operation(summary = "직원 삭제", description = "특정 직원을 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "삭제 성공"),
		// @ApiResponse(responseCode = "404", description = "직원 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	ResponseEntity<EmployeeDto> deleteEmployee(Long id, HttpServletRequest request);

	@Operation(summary = "직원 정보 수정", description = "특정 직원 정보를 수정합니다.")
	ResponseEntity<EmployeeDto> updateEmployee(Long id, EmployeeUpdateRequest employeeUpdateRequest,
		MultipartFile profileImage, HttpServletRequest request) throws
		IOException;

	@Operation(summary = "직원 추세 조회", description = "기간별 직원 변동 추세를 조회합니다.")
	ResponseEntity<List<EmployeeTrendDto>> getEmployeeTrend(LocalDate from, LocalDate to, String unit);

	@Operation(summary = "직원 분포 조회", description = "부서별 또는 상태별 직원 분포를 조회합니다.")
	ResponseEntity<List<EmployeeDistributionDto>> getEmployeeDistributaion(String groupBy, String status);

	@Operation(summary = "직원 수 조회", description = "특정 조건에 따른 직원 수를 조회합니다.")
	ResponseEntity<Long> getEmployeeCount(String status, LocalDate fromDate, LocalDate toDate);
}