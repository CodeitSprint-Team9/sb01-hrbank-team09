package com.team09.sb01hrbank09.dto.entityDto;

import java.time.Instant;

import com.team09.sb01hrbank09.entity.Enum.ChangeLogType;

public record ChangeLogDto(
	Long id,
	ChangeLogType type,
	String employeeNumber,
	String details,
	String memo,
	String ipAddress,
	Instant at
) {
}
