package com.team09.sb01hrbank09.dto.entityDto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.team09.sb01hrbank09.entity.Enum.ChangeLogType;

@JsonPropertyOrder({"id", "type", "employeeNumber", "memo", "ipAddress", "at"})
public record ChangeLogDto(
	Long id,
	ChangeLogType type,
	String employeeNumber,
	String memo,
	String ipAddress,
	Instant at
) {
}
