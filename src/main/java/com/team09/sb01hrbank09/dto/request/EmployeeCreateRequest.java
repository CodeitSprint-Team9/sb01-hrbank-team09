package com.team09.sb01hrbank09.dto.request;

import java.time.LocalDate;

public record EmployeeCreateRequest(
	String name,
	String email,
	Long departmentId,
	String position,
	LocalDate hireDate,
	String memo) {
}
