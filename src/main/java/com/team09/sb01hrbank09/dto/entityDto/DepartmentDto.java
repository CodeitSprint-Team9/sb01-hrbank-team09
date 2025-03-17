package com.team09.sb01hrbank09.dto.entityDto;

import java.time.Instant;

public record DepartmentDto(
	Long id,
	String name,
	String description,
	Instant establishedDate,
	int employeeCount) {
}
