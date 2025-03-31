package com.team09.sb01hrbank09.controller;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team09.sb01hrbank09.api.BackupApi;
import com.team09.sb01hrbank09.dto.entityDto.BackupDto;
import com.team09.sb01hrbank09.dto.request.CursorPageRequestBackupDto;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseBackupDto;
import com.team09.sb01hrbank09.entity.Enum.BackupStatus;
import com.team09.sb01hrbank09.mapper.BackupMapper;
import com.team09.sb01hrbank09.service.BackupLogServiceInterface;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/backups")
public class BackupContoller implements BackupApi {
	private final BackupLogServiceInterface backupLogServiceInterface;
	private final BackupMapper backupMapper;

	@PostMapping
	public ResponseEntity<BackupDto> createBackup(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-Forwarded-For");
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
		}
		BackupDto response = backupLogServiceInterface.createBackup(ipAddress);

		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<CursorPageResponseBackupDto> findBackupList(
		@RequestParam(required = false) String worker,
		@RequestParam(required = false) BackupStatus status,
		@RequestParam(required = false) Instant startedAtFrom,
		@RequestParam(required = false) Instant startedAtTo,
		@RequestParam(required = false) Long idAfter,
		@RequestParam(required = false) Instant cursor,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "startedAt") String sortField,
		@RequestParam(defaultValue = "desc") String sortDirection
	) {
		System.out.println("startedAtFrom = " + startedAtFrom);

		worker = worker != null ? worker : "";
		startedAtFrom = startedAtFrom != null ? startedAtFrom : Instant.parse("1970-01-01T00:00:00Z");
		startedAtTo = startedAtTo != null ? startedAtTo : Instant.parse("9999-12-31T23:59:59Z");

		if (sortDirection.equalsIgnoreCase("asc")) {
			cursor = cursor != null ? cursor : Instant.parse("1970-01-01T00:00:00Z");
		} else {
			cursor = cursor != null ? cursor : Instant.parse("9999-12-31T23:59:59Z");
		}

		CursorPageRequestBackupDto requestDto = new CursorPageRequestBackupDto(
			worker, status, startedAtFrom, startedAtTo, idAfter, cursor, size, sortField, sortDirection
		);

		System.out.println("requestDto = " + requestDto);
		CursorPageResponseBackupDto response = backupLogServiceInterface.findBackupList(requestDto);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/latest")
	public ResponseEntity<BackupDto> findLatestBackup(@RequestParam(defaultValue = "COMPLETED") String status) {
		BackupDto response = backupLogServiceInterface.findLatestBackup(status);

		return ResponseEntity.ok(response);

	}
}
