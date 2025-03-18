package com.team09.sb01hrbank09.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team09.sb01hrbank09.dto.entityDto.DiffDto;
import com.team09.sb01hrbank09.dto.request.CursorPageRequestChangeLog;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseChangeLogDto;
import com.team09.sb01hrbank09.entity.Enum.ChangeLogType;
import com.team09.sb01hrbank09.service.ChangeLogServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/change-logs")
@RequiredArgsConstructor
public class ChangeLogController {

	private final ChangeLogServiceImpl changeLogService;

	//직원 정보 수정 이력 목록 조회
	@GetMapping("")
	public ResponseEntity<CursorPageResponseChangeLogDto> findChangeLogList(
		@RequestParam(required = false) String employeeNumber,
		@RequestParam(required = false) String type,
		@RequestParam(required = false) String memo,
		@RequestParam(required = false) String ipAddress,
		@RequestParam(required = false) Instant atFrom,
		@RequestParam(required = false) Instant atTo,
		@RequestParam(required = false) Long idAfter,
		@RequestParam(required = false) String cursor,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "at") String sortField,
		@RequestParam(defaultValue = "desc") String sortDirection) {

		ChangeLogType changeLogType = null;
		//type null이 아니라면 enum타입으로 변환
		if (type != null) {
			changeLogType = ChangeLogType.valueOf(type.toUpperCase());
		}

		CursorPageRequestChangeLog request = new CursorPageRequestChangeLog(employeeNumber, changeLogType, memo,
			ipAddress, atFrom, atTo, idAfter, cursor, size, sortField, sortDirection);

		CursorPageResponseChangeLogDto response = changeLogService.findChangeLogList(request);

		return ResponseEntity.ok(response);
	}

	//직원 정보 수정 이력 상세 조회
	@GetMapping("/{id}/diffs")
	public ResponseEntity<List<DiffDto>> findChangeLogById(@PathVariable Long id) {
		List<DiffDto> response = changeLogService.findChangeLogById(id);

		return ResponseEntity.ok(response);
	}

	//수정 이력 건수 조회
	@GetMapping("/count")
	public ResponseEntity<Long> countChangeLogs(
		@RequestParam(required = false) Instant fromDate,
		@RequestParam(required = false) Instant toDate) {

		//fromDate가 없으면 7일 전, toDate가 없다면 현재 시간으로 설정
		Instant now = Instant.now();
		if (fromDate == null) {
			fromDate = now.minus(7, ChronoUnit.DAYS);
		}
		if (toDate == null) {
			toDate = now;
		}

		Long count = changeLogService.countChangeLog(fromDate, toDate);
		return ResponseEntity.ok(count);
	}
}
