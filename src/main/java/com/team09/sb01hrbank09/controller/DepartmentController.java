package com.team09.sb01hrbank09.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team09.sb01hrbank09.dto.entityDto.DepartmentDto;
import com.team09.sb01hrbank09.dto.request.CursorPageRequestDepartment;
import com.team09.sb01hrbank09.dto.request.DepartmentCreateRequest;
import com.team09.sb01hrbank09.dto.request.DepartmentUpdateRequest;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseDepartmentDto;
import com.team09.sb01hrbank09.service.DepartmentServiceInterface;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

	//구현체로 변경 예정
	private final DepartmentServiceInterface departmentService;

	//부서 등록
	@PostMapping("")
	public ResponseEntity<DepartmentDto> createDepartment(@RequestBody DepartmentCreateRequest request) {

		DepartmentDto response = departmentService.createDepartment(request);
		return ResponseEntity.ok(response);
	}

	//부서 목록 조회
	@GetMapping("")
	public ResponseEntity<CursorPageResponseDepartmentDto> findDepartmentList(
		@RequestParam(required = false) String nameOrDescription,
		@RequestParam(required = false) Long idAfter,
		@RequestParam(required = false) String cursor,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "name") String sortField,
		@RequestParam(defaultValue = "asc") String sortDirection) {
		if (nameOrDescription == null) {
			nameOrDescription = "";
		}
		CursorPageRequestDepartment request = new CursorPageRequestDepartment(
			nameOrDescription, idAfter, cursor, size, sortField, sortDirection
		);

		CursorPageResponseDepartmentDto response = departmentService.findDepartmentList(request);

		return ResponseEntity.ok(response);
	}

	//부서 상세 조회
	@GetMapping("/{id}")
	public ResponseEntity<DepartmentDto> findDepartment(@PathVariable Long id) {
		DepartmentDto response = departmentService.findDepartmentById(id);

		return ResponseEntity.ok(response);
	}

	//부서 수정
	@PatchMapping("/{id}")
	public ResponseEntity<DepartmentDto> updateDepartment(@PathVariable Long id,
		@RequestBody DepartmentUpdateRequest request) {
		DepartmentDto response = departmentService.updateDepartment(id, request);

		return ResponseEntity.ok(response);
	}

	//부서 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
		departmentService.deleteDepartment(id);

		return ResponseEntity.noContent().build();
	}
}
