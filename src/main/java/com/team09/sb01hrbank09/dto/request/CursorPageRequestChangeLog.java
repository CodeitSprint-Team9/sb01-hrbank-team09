package com.team09.sb01hrbank09.dto.request;

import java.time.Instant;

import com.team09.sb01hrbank09.entity.Enum.ChangeLogType;

public record CursorPageRequestChangeLog(
	String employeeNumber,
	ChangeLogType type,
	String memo,
	String ipAddress,
	Instant atFrom,
	Instant atTo,
	Long idAfter,
	String cursor,
	int size,
	String sortField,
	String sortDirection
) {
}
