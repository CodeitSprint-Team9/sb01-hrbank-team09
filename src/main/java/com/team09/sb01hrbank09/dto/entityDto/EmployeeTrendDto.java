package com.team09.sb01hrbank09.dto.entityDto;

import java.time.Instant;

public record EmployeeTrendDto(
	Instant date,
	Long count,
	Long change,
	Double changeRate
) {
}