package com.team09.sb01hrbank09.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.team09.sb01hrbank09.dto.entityDto.DepartmentDto;
import com.team09.sb01hrbank09.dto.request.DepartmentCreateRequest;
import com.team09.sb01hrbank09.dto.request.DepartmentUpdateRequest;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseDepartmentDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "부서 관리", description = "부서 관리 API")
public interface DepartmentApi {

	@Operation(summary = "부서 등록", description = "새로운 부서를 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "부서 등록 성공", content = @Content(schema = @Schema(implementation = DepartmentDto.class))),
		// @ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
	ResponseEntity<DepartmentDto> createDepartment(@RequestBody DepartmentCreateRequest request);

	@Operation(summary = "부서 목록 조회", description = "부서 목록을 조건에 따라 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "부서 목록 조회 성공", content = @Content(schema = @Schema(implementation = CursorPageResponseDepartmentDto.class)))
	})
	ResponseEntity<CursorPageResponseDepartmentDto> findDepartmentList(
		@Parameter(description = "부서 이름 또는 설명 검색어") @RequestParam(required = false) String nameOrDescription,
		@Parameter(description = "커서 기반 페이지네이션을 위한 ID (이후)") @RequestParam(required = false) Long idAfter,
		@Parameter(description = "커서") @RequestParam(required = false) String cursor,
		@Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size,
		@Parameter(description = "정렬 필드") @RequestParam(defaultValue = "name") String sortField,
		@Parameter(description = "정렬 방향 (asc/desc)") @RequestParam(defaultValue = "asc") String sortDirection);

	@Operation(summary = "부서 상세 조회", description = "ID로 특정 부서를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "부서 상세 조회 성공", content = @Content(schema = @Schema(implementation = DepartmentDto.class))),
		// @ApiResponse(responseCode = "404", description = "부서를 찾을 수 없음")
	})
	ResponseEntity<DepartmentDto> findDepartment(@PathVariable Long id);

	@Operation(summary = "부서 수정", description = "ID로 특정 부서 정보를 수정합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "부서 수정 성공", content = @Content(schema = @Schema(implementation = DepartmentDto.class))),
		// @ApiResponse(responseCode = "404", description = "부서를 찾을 수 없음"),
		// @ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
	ResponseEntity<DepartmentDto> updateDepartment(@PathVariable Long id, @RequestBody DepartmentUpdateRequest request);

	@Operation(summary = "부서 삭제", description = "ID로 특정 부서를 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "부서 삭제 성공"),
		// @ApiResponse(responseCode = "404", description = "부서를 찾을 수 없음")
	})
	ResponseEntity<Void> deleteDepartment(@PathVariable Long id);
}