package com.team09.sb01hrbank09.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team09.sb01hrbank09.api.ChangeLogApi;
import com.team09.sb01hrbank09.dto.entityDto.DiffDto;
import com.team09.sb01hrbank09.dto.request.CursorPageRequestChangeLog;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseChangeLogDto;
import com.team09.sb01hrbank09.service.ChangeLogServiceInterface;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/change-logs")
@RequiredArgsConstructor
@Slf4j
public class ChangeLogController implements ChangeLogApi {

	private final ChangeLogServiceInterface changeLogService;

	//직원 정보 수정 이력 목록 조회
	@GetMapping("")
	public ResponseEntity<CursorPageResponseChangeLogDto> findChangeLogList(
		@RequestParam(required = false) String employeeNumber,
		@RequestParam(required = false) String type,
		@RequestParam(required = false) String memo,
		@RequestParam(required = false) String ipAddress,
		@RequestParam(required = false) Instant atFrom,
		@RequestParam(required = false) Instant atTo,
		@RequestParam(required = false) String cursor,
		@RequestParam(required = false) Long idAfter,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "at") String sortField,
		@RequestParam(defaultValue = "desc") String sortDirection) {

		employeeNumber = employeeNumber == null ? "" : employeeNumber;
		memo = memo == null ? "" : memo;
		ipAddress = ipAddress == null ? "" : ipAddress;
		atFrom = atFrom != null ? atFrom : Instant.parse("1970-01-01T00:00:00Z");
		atTo = atTo != null ? atTo : Instant.parse("9999-12-31T23:59:59Z");

		CursorPageRequestChangeLog request = new CursorPageRequestChangeLog(employeeNumber, type, memo,
			ipAddress, atFrom, atTo, idAfter, cursor, size, sortField, sortDirection);
		CursorPageResponseChangeLogDto response = changeLogService.findChangeLogList(request);

		return ResponseEntity.ok(response);
	}

	//직원 정보 수정 이력 상세 조회
	@GetMapping("/{id}/diffs")
	public ResponseEntity<List<DiffDto>> findChangeLogById(@PathVariable("id") Long id) {
		log.info("상세 조회 시작");
		List<DiffDto> response = changeLogService.findChangeLogById(id);
		log.info("상세 조회 완료");
		return ResponseEntity.ok(response);
	}

	//수정 이력 건수 조회
	@GetMapping("/count")
	public ResponseEntity<Long> countChangeLogs(
		@RequestParam(required = false) Instant fromDate,
		@RequestParam(required = false) Instant toDate) {

		Long count = changeLogService.countChangeLog(fromDate, toDate);
		return ResponseEntity.ok(count);
	}
}
