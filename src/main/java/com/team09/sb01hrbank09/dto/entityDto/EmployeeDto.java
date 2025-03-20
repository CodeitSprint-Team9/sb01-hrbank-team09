package com.team09.sb01hrbank09.dto.entityDto;

import java.time.Instant;
import java.time.LocalDate;

public record EmployeeDto(
	Long id,
	String name,
	String email,
	String employeeNumber,
	Long departmentId,
	String departmentName,
	String position,
	LocalDate hireDate,
	String status,
	Long profileImageId) {
}
