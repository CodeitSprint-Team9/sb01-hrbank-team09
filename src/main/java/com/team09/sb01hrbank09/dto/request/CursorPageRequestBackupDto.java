package com.team09.sb01hrbank09.dto.request;

import java.time.Instant;

public record CursorPageRequestBackupDto(
	String worker, // 작업자
	String status, // 상태 (IN_PROGRESS, COMPLETED, FAILED)
	Instant startedAtFrom, // 시작 시간 (부터)
	Instant startedAtTo, // 시작 시간 (까지)
	Long idAfter, // 이전 페이지 마지막 요소 ID
	String cursor, // 커서 (이전 페이지의 마지막 ID)
	Integer size, // 페이지 크기 (기본값: 10)
	String sortField, // 정렬 필드 (startedAt, endedAt, status)
	String sortDirection // 정렬 방향 (ASC, DESC)
) {
	public static CursorPageRequestBackupDto copy(CursorPageRequestBackupDto dto, Long nextIdAfter, String nextCursor) {
		return new CursorPageRequestBackupDto(
			dto.worker,
			dto.status,
			dto.startedAtFrom,
			dto.startedAtTo,
			nextIdAfter,
			nextCursor,
			dto.size,
			dto.sortField,
			dto.sortDirection
		);
	}
}
