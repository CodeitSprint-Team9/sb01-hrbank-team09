package com.team09.sb01hrbank09.dto.entityDto;

import java.time.LocalDate;

public record EmployeeTrendDto(
	LocalDate date,
	int count,
	int change,
	Double changeRate
) {
}