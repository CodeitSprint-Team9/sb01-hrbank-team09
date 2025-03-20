package com.team09.sb01hrbank09.dto.entityDto;

import java.time.Instant;
import java.time.LocalDate;

public record DepartmentDto(
	Long id,
	String name,
	String description,
	LocalDate establishedDate,
	int employeeCount) {
}




