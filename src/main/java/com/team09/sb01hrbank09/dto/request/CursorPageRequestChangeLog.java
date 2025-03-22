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
	public static CursorPageRequestChangeLog copy(CursorPageRequestChangeLog request, String newCursor,
		Long newIdAfter) {
		return new CursorPageRequestChangeLog(
			request.employeeNumber(),
			request.type(),
			request.memo(),
			request.ipAddress(),
			request.atFrom(),
			request.atTo(),
			newIdAfter != null ? newIdAfter : request.idAfter(),
			newCursor,
			request.size(),
			request.sortField(),
			request.sortDirection()
		);
	}
}
