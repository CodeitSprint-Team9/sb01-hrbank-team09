package com.team09.sb01hrbank09.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team09.sb01hrbank09.dto.entityDto.DepartmentDto;
import com.team09.sb01hrbank09.dto.request.CursorPageRequestDepartment;
import com.team09.sb01hrbank09.dto.request.DepartmentCreateRequest;
import com.team09.sb01hrbank09.dto.request.DepartmentUpdateRequest;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseDepartmentDto;
import com.team09.sb01hrbank09.entity.Department;
import com.team09.sb01hrbank09.mapper.DepartmentMapper;
import com.team09.sb01hrbank09.repository.DepartmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentServiceInterface {
	private final DepartmentRepository departmentRepository;
	private final DepartmentMapper departmentMapper;

	@Override
	public DepartmentDto createDepartment(DepartmentCreateRequest request) {
		Optional<Department> existingDepartment = departmentRepository.findByName(request.name());
		if (existingDepartment.isPresent()) {
			throw new IllegalArgumentException("생성하려는 부서명이 이미 존재합니다.");
		}

		Department department = Department.createDepartments(request.name(), request.description(),
			request.establishedDate());

		Department savedDepartment = departmentRepository.save(department);
		return departmentMapper.departmentToDto(savedDepartment);

	}

	@Override
	public DepartmentDto updateDepartment(Long id, DepartmentUpdateRequest request) {
		Department department = departmentRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("업데이트 하려는 부서가 존재하지 않습니다."));

		Optional<Department> existingDepartment = departmentRepository.findByName(request.name());
		if (existingDepartment.isPresent() && !existingDepartment.get().getId().equals(id)) {
			throw new IllegalArgumentException("업데이트 하려는 부서명이 이미 존재합니다.");
		}

		department.updateDepartmentsName(request.name());
		department.updateDepartmentDescription(request.description());
		department.updateEstablishedDate(request.establishedDate());

		Department updatedDepartment = departmentRepository.save(department);
		return departmentMapper.departmentToDto(updatedDepartment);
	}

	@Override
	public void deleteDepartment(Long id) {
		Department department = departmentRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부서입니다."));

		if (department.getEmployeeCount() > 0) {
			throw new IllegalStateException("소속된 직원이 있는 부서는 삭제할 수 없습니다.");
		}
		departmentRepository.deleteById(id);
	}

	//부서 목록 조회
	@Override
	@Transactional(readOnly = true)
	public CursorPageResponseDepartmentDto findDepartmentList(CursorPageRequestDepartment request) {
		validateRequest(request);

		String sortField = request.sortField();
		String sortDirection = request.sortDirection();

		Sort.Order sortOrder =
			sortDirection.equalsIgnoreCase("desc") ? Sort.Order.desc(sortField) : Sort.Order.asc(sortField);
		Sort sort = Sort.by(sortOrder);
		Pageable pageable = PageRequest.of(0, request.size() + 1, sort);

		// Pageable pageable = PageRequest.of(request.cursor() != null ? Integer.parseInt(request.cursor()) : 0,
		// 	request.size(), sort);

		Page<Department> departments;

		if (sortOrder.isAscending()) {
			departments = departmentRepository.findDepartmentOrderByIdAsc(request.nameOrDescription(), request.idAfter(), pageable);
		}
		else {
			departments = departmentRepository.findDepartmentOrderByIdDesc(request.nameOrDescription(), request.idAfter(), pageable);
		}

		// //Page<Department> departments = departmentRepository.findDepartment(request.nameOrDescription(),
		// 	request.idAfter(), pageable);
		List<Department> departmentList = departments.getContent();

		boolean hasNext = departmentList.size() > request.size();
		if (hasNext) {
			departmentList = departmentList.subList(0, request.size());
		}

		List<DepartmentDto> departmentDtos = departmentList.stream()
			.map(departmentMapper::departmentToDto)
			.toList();

		return toCursorPageResponse(departmentDtos, request, departments, hasNext);
	}

	private void validateRequest(CursorPageRequestDepartment request) {
		if (request.sortDirection() == null || (!request.sortDirection().equalsIgnoreCase("ASC")
			&& !request.sortDirection().equalsIgnoreCase("DESC"))) {
			throw new IllegalArgumentException("유효하지 않은 정렬 방향입니다.");
		}
		if (request.sortField() == null || (!request.sortField().equals("name") && !request.sortField()
			.equals("establishedDate"))) {
			throw new IllegalArgumentException("유효하지 않은 정렬 필드입니다.");
		}
	}

	private CursorPageResponseDepartmentDto toCursorPageResponse(List<DepartmentDto> dtos,
		CursorPageRequestDepartment request, Page<Department> departments, boolean hasNext) {
		Long nextIdAfter = null;
		String nextCursor = null;
		if (!dtos.isEmpty()) {
			DepartmentDto lastDepartment = dtos.get(dtos.size() - 1);
			nextIdAfter = lastDepartment.id();
			nextCursor = String.valueOf(lastDepartment.name()); // TODO:
		}

		Long totalElements = departmentRepository.getCount();

		//Long totalElements = departments.getTotalElements();

		return new CursorPageResponseDepartmentDto(
			dtos,
			nextCursor,
			nextIdAfter,
			request.size(),
			totalElements,
			hasNext
		);
	}

	@Override
	@Transactional(readOnly = true)
	public DepartmentDto findDepartmentById(Long id) {
		Department department = findDepartmentEntityById(id);
		return departmentMapper.departmentToDto(department);
	}

	@Transactional(readOnly = true)
	public Department findDepartmentEntityById(Long id) {
		return departmentRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부서입니다."));
	}

}