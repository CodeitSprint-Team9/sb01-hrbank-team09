package com.team09.sb01hrbank09.dto.request;

import java.time.Instant;
import java.time.LocalDate;

public record EmployeeUpdateRequest(
	String name,
	String email,
	Long departmentId,
	String position,
	LocalDate hireDate,
	String status,
	String memo
) {
}