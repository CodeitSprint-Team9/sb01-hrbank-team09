package com.team09.sb01hrbank09.api;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import com.team09.sb01hrbank09.dto.entityDto.BackupDto;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseBackupDto;
import com.team09.sb01hrbank09.entity.Enum.BackupStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "데이터 백업 관리", description = "데이터 백업 관리 API")
public interface BackupApi {

	@Operation(summary = "백업 생성", description = "새로운 백업을 생성합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "백업 생성 성공")
	})
	ResponseEntity<BackupDto> createBackup(HttpServletRequest request);

	@Operation(summary = "백업 목록 조회", description = "백업 목록을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "백업 목록 조회 성공")
	})
	ResponseEntity<CursorPageResponseBackupDto> findBackupList(
		String worker,
		BackupStatus status,
		Instant startedAtFrom,
		Instant startedAtTo,
		Long idAfter,
		String cursor,
		int size,
		String sortField,
		String sortDirection
	);

	@Operation(summary = "최신 백업 조회", description = "가장 최근의 백업을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "최신 백업 조회 성공")
	})
	ResponseEntity<BackupDto> findLatestBackup(@RequestParam(defaultValue = "COMPLETED") String status);
}
