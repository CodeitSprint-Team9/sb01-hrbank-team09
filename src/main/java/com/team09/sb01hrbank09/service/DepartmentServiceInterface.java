package com.team09.sb01hrbank09.service;

import com.team09.sb01hrbank09.dto.entityDto.DepartmentDto;
import com.team09.sb01hrbank09.dto.request.CursorPageRequestDepartment;
import com.team09.sb01hrbank09.dto.request.DepartmentCreateRequest;
import com.team09.sb01hrbank09.dto.request.DepartmentUpdateRequest;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseDepartmentDto;
import com.team09.sb01hrbank09.entity.Department;

public interface DepartmentServiceInterface {

	DepartmentDto createDepartment(DepartmentCreateRequest request);

	DepartmentDto updateDepartment(Long id, DepartmentUpdateRequest request);

	void deleteDepartment(Long id);

	//부서 목록 조회
	CursorPageResponseDepartmentDto findDepartmentList(CursorPageRequestDepartment request);

	//부서 상세 조회
	DepartmentDto findDepartmentById(Long id);

	Department findDepartmentEntityById(Long id);

}
