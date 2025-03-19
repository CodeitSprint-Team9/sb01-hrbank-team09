package com.team09.sb01hrbank09.dto.request;

import java.time.Instant;

public record CursorPageRequestChangeLog(
	String employeeNumber,
	String type,
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
	public static CursorPageRequestChangeLog copy(CursorPageRequestChangeLog dto, Long nextIdAfter, String nextCursor) {
		return new CursorPageRequestChangeLog(
			dto.employeeNumber(),
			dto.type(),
			dto.memo(),
			dto.ipAddress(),
			dto.atFrom(),
			dto.atTo(),
			nextIdAfter,  // nextIdAfter 값 설정
			nextCursor,   // nextCursor 값 설정
			dto.size(),
			dto.sortField(),
			dto.sortDirection()
		);
	}
}
