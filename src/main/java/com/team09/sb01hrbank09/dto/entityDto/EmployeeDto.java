package com.team09.sb01hrbank09.dto.entityDto;

import java.time.Instant;

public record EmployeeDto(
	Long id,
	String name,
	String email,
	String employeeNumber,
	Long departmentId,
	String departmentName,
	String position,
	Instant hireDateFrom,
	String status,
	Long profileImageId) {
}
