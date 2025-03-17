package com.team09.sb01hrbank09.dto.entityDto;

import java.time.Instant;

import com.team09.sb01hrbank09.entity.Enum.BackupStatus;

public record BackupDto(
	Long id,
	Instant startedAt,
	Instant endedAt,
	BackupStatus status,
	String worker,
	Long fileId
) {
}
