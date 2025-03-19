package com.team09.sb01hrbank09.controller;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.team09.sb01hrbank09.dto.entityDto.EmployeeDistributionDto;
import com.team09.sb01hrbank09.dto.entityDto.EmployeeDto;
import com.team09.sb01hrbank09.dto.entityDto.EmployeeTrendDto;
import com.team09.sb01hrbank09.dto.request.EmployeeCreateRequest;
import com.team09.sb01hrbank09.dto.request.EmployeeUpdateRequest;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseEmployeeDto;
import com.team09.sb01hrbank09.service.EmployeeServiceInterface;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

	private final EmployeeServiceInterface employeeServiceInterface;

	@PostMapping
	public ResponseEntity<EmployeeDto> creatEmployee(
		@RequestPart("employeeCreateRequest") EmployeeCreateRequest employeeCreateRequest,
		@RequestPart(value = "profile", required = false) MultipartFile profileImage,
		HttpServletRequest request
	) throws IOException {
		String ipAddress = request.getHeader("X-Forwarded-For");
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
		}
		System.out.println(ipAddress);
		EmployeeDto response = employeeServiceInterface.creatEmployee(employeeCreateRequest, profileImage, ipAddress);

		return ResponseEntity.ok(response);
	}

	@GetMapping
	ResponseEntity<CursorPageResponseEmployeeDto> findEmployeeList(
		@RequestParam(required = false) String nameOrEmail,
		@RequestParam(required = false) String employeeNumber,
		@RequestParam(required = false) String departmentName,
		@RequestParam(required = false) String position,
		@RequestParam(required = false) String hireDateFrom,
		@RequestParam(required = false) String hireDateTo,
		@RequestParam(required = false) String status,
		@RequestParam(required = false) Long idAfter,
		@RequestParam(required = false) String cursor,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "name") String sortField,
		@RequestParam(defaultValue = "asc") String sortDirection
	) {
		LocalDateTime parsedHireDateFrom = null;
		LocalDateTime parsedHireDateTo = null;
		if (hireDateFrom != null) {
			parsedHireDateFrom = LocalDateTime.parse(hireDateFrom);
		}
		if (hireDateTo != null) {
			parsedHireDateTo = LocalDateTime.parse(hireDateTo);
		}

		// status 필드 검증
		List<String> validStatuses = Arrays.asList("ACTIVE", "ON_LEAVE", "RESIGNED");
		CursorPageResponseEmployeeDto response = employeeServiceInterface.findEmployeeList(
			nameOrEmail, employeeNumber, departmentName, position,
			hireDateFrom, hireDateTo, status, idAfter, cursor,
			size, sortField, sortDirection
		);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	ResponseEntity<EmployeeDto> findEmployeeById(@PathVariable Long id) {
		EmployeeDto response = employeeServiceInterface.findEmployeeById(id);

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	ResponseEntity<EmployeeDto> deleteEmployee(@PathVariable Long id, HttpServletRequest request) {
		String ipAddress = request.getHeader("X-Forwarded-For");
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
		}
		boolean deleted = employeeServiceInterface.deleteEmployee(id, ipAddress);
		if (deleted) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@PatchMapping("/{id}")
	ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Long id,
		@RequestPart("employeeUpdateRequest") EmployeeUpdateRequest employeeUpdateRequest,
		@RequestPart(value = "profile", required = false) MultipartFile profileImage,
		HttpServletRequest request) throws IOException {
		String ipAddress = request.getHeader("X-Forwarded-For");
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
		}
		EmployeeDto response = employeeServiceInterface.updateEmployee(id, employeeUpdateRequest, profileImage,
			ipAddress);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/stats/trend")
	ResponseEntity<List<EmployeeTrendDto>> getEmployeeTrend(
		@RequestParam(required = false) Instant from,
		@RequestParam(required = false) Instant to,
		@RequestParam(required = false, defaultValue = "month") String unit) {

		if (unit == null || unit.isBlank()) {
			unit = "month";
		}
		if (to == null) {
			to = Instant.now();
		}
		if (from == null) {
			from = convertInstant(unit, from, to);
		}

		List<EmployeeTrendDto> response = employeeServiceInterface.getEmployeeTrend(from, to, unit);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/stats/distribution")
	ResponseEntity<List<EmployeeDistributionDto>> getEmployeeDistributaion(
		@RequestParam(required = false, defaultValue = "department") String groupBy,
		@RequestParam(required = false, defaultValue = "ACTIVE") String status) {
		List<EmployeeDistributionDto> response = employeeServiceInterface.getEmployeeDistributaion(groupBy, status);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/count")
	public ResponseEntity<Long> getEmployeeCount(
		@RequestParam(required = false, defaultValue = "ALL") String status,//all에대한 예외 필요
		@RequestParam(required = false) Instant fromDate,
		@RequestParam(required = false) Instant toDate) {

		if (fromDate == null) {
			fromDate = Instant.MIN;
		}
		if (toDate == null) {
			toDate = Instant.now();
		}
		long count = employeeServiceInterface.countEmployee(status, fromDate, toDate);
		return ResponseEntity.ok(count);
	}

	private Instant convertInstant(String unit, Instant time, Instant to) {
		if ("month".equals(unit)) {
			return to.minus(12, ChronoUnit.MONTHS);
		} else if ("day".equals(unit)) {
			return to.minus(12, ChronoUnit.DAYS);
		} else if ("week".equals(unit)) {
			return to.minus(12, ChronoUnit.WEEKS);
		} else if ("quarter".equals(unit)) {
			return to.minus(36, ChronoUnit.MONTHS);
		} else if ("year".equals(unit)) {
			return to.minus(12, ChronoUnit.YEARS);
		}
		return time;
	}
}