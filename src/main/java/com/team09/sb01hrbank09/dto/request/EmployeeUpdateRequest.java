package com.team09.sb01hrbank09.dto.request;

import java.time.Instant;

public record EmployeeUpdateRequest(
	String name,
	String email,
	Long departmentId,
	String position,
	Instant hireDate,
	String status,
	String memo
) {
}