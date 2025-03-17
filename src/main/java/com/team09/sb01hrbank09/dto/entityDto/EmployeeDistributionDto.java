package com.team09.sb01hrbank09.dto.entityDto;

public record EmployeeDistributionDto(
	String groupKey,
	Long count,
	Double percentage
) {
}