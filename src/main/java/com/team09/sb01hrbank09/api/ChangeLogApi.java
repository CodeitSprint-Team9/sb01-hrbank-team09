package com.team09.sb01hrbank09.api;

import java.time.Instant;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.team09.sb01hrbank09.dto.entityDto.DiffDto;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseChangeLogDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "직원 정보 수정 이력 관리", description = "직원 정보 수정 이력 관리 API")
public interface ChangeLogApi {

	@Operation(summary = "직원 정보 수정 이력 목록 조회", description = "필터 조건을 통해 변경 이력 목록을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	ResponseEntity<CursorPageResponseChangeLogDto> findChangeLogList(
		@Parameter(description = "직원 번호") String employeeNumber,
		@Parameter(description = "변경 유형") String type,
		@Parameter(description = "메모") String memo,
		@Parameter(description = "IP 주소") String ipAddress,
		@Parameter(description = "조회 시작 시간") Instant atFrom,
		@Parameter(description = "조회 종료 시간") Instant atTo,
		@Parameter(description = "이후 ID") Long idAfter,
		@Parameter(description = "커서") String cursor,
		@Parameter(description = "페이지 크기") int size,
		@Parameter(description = "정렬 필드") String sortField,
		@Parameter(description = "정렬 방향") String sortDirection
	);

	@Operation(summary = "직원 정보 수정 이력 상세 조회", description = "특정 변경 이력의 상세 정보를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	ResponseEntity<List<DiffDto>> findChangeLogById(
		@Parameter(description = "변경 이력 ID", required = true) Long id
	);

	@Operation(summary = "수정 이력 건수 조회", description = "특정 기간 동안의 변경 이력 개수를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	ResponseEntity<Long> countChangeLogs(
		@Parameter(description = "조회 시작 날짜") Instant fromDate,
		@Parameter(description = "조회 종료 날짜") Instant toDate
	);

}
