package com.team09.sb01hrbank09.dto.request;

import java.time.Instant;

import com.team09.sb01hrbank09.entity.Enum.BackupStatus;

public record CursorPageRequestBackupDto(
	String worker, // 작업자
	BackupStatus status, // 상태 (IN_PROGRESS, COMPLETED, FAILED)
	Instant startedAtFrom, // 시작 시간 (부터)
	Instant startedAtTo, // 시작 시간 (까지)
	Long idAfter, // 이전 페이지 마지막 요소 ID
	Instant cursor, // 커서 (이전 페이지의 마지막 ID)
	Integer size, // 페이지 크기 (기본값: 10)
	String sortField, // 정렬 필드 (startedAt, endedAt, status)
	String sortDirection // 정렬 방향 (ASC, DESC)
) {
}
