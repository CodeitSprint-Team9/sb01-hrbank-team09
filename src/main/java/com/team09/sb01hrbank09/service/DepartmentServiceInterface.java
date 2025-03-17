package com.team09.sb01hrbank09.service;

import com.team09.sb01hrbank09.dto.entityDto.DepartmentDto;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseDepartmentDto;

public interface DepartmentServiceInterface {

	DepartmentDto createDepartment(String name, String description);

	DepartmentDto updateDepartment(DepartmentDto departmentDto);

	void deleteDepartment(Long id);

	//변경요소 있음
	CursorPageResponseDepartmentDto findDepartmentList(Long id, DepartmentDto departmentDto, int size);

	DepartmentDto findDepartmentById(Long id);

}
