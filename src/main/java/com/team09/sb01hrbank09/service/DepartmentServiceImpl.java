package com.team09.sb01hrbank09.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

		Sort sort = Sort.by(Sort.Direction.fromString(request.sortDirection()), request.sortField());

		Pageable pageable = PageRequest.of(0, request.size() + 1, sort);

		List<Department> departments;
		if (request.sortField().equalsIgnoreCase("name")) {
			if (request.sortDirection().equalsIgnoreCase("asc")) {
				departments = departmentRepository.findDepartmentByNameOrderByIdAsc(
					request.nameOrDescription(),
					request.idAfter(),
					request.cursor(),
					pageable
				);
			} else {
				departments = departmentRepository.findDepartmentByNameOrderByIdDesc(
					request.nameOrDescription(),
					request.idAfter(),
					request.cursor(),
					pageable
				);
			}
		} else {
			if (request.sortDirection().equalsIgnoreCase("asc")) {
				departments = departmentRepository.findDepartmentByEstablishedDateOrderByIdAsc(
					request.nameOrDescription(),
					request.idAfter(),
					LocalDate.parse(request.cursor()),
					pageable
				);
			} else {
				departments = departmentRepository.findDepartmentByEstablishedDateOrderByIdDesc(
					request.nameOrDescription(),
					request.idAfter(),
					LocalDate.parse(request.cursor()),
					pageable
				);
			}
		}

		boolean hasNext = false;
		if (departments.size() > request.size()) {
			hasNext = true;
			departments = departments.subList(0, request.size());
		}
		Long nextIdAfter = null;

		Long totalElements = departmentRepository.getCount(request.nameOrDescription());

		String nextCursor = null;
		if (!departments.isEmpty()) {
			Department lastDepartment = departments.get(departments.size() - 1);
			nextIdAfter = lastDepartment.getId();

			if (request.sortField().equalsIgnoreCase("name")) {
				nextCursor = String.valueOf(lastDepartment.getName());
			} else {
				nextCursor = String.valueOf(lastDepartment.getEstablishedDate());
			}
		}

		List<DepartmentDto> departmentDtos = departments.stream()
			.map(departmentMapper::departmentToDto)
			.toList();

		return new CursorPageResponseDepartmentDto(
			departmentDtos,
			nextCursor,
			nextIdAfter,
			request.size(),
			totalElements,
			hasNext
		);
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

	// private CursorPageResponseDepartmentDto toCursorPageResponse(List<DepartmentDto> dtos,
	// 	CursorPageRequestDepartment request, Page<Department> departments, boolean hasNext, String sortField) {
	// 	Long nextIdAfter = null;
	// 	String nextCursor = null;
	// 	if (!dtos.isEmpty()) {
	// 		DepartmentDto lastDepartment = dtos.get(dtos.size() - 1);
	// 		nextIdAfter = lastDepartment.id();
	// 		if (sortField.equals("name")) {
	// 			nextCursor = String.valueOf(lastDepartment.name());
	// 		} else
	// 			nextCursor = String.valueOf(lastDepartment.establishedDate());
	// 	}
	//
	// 	Long totalElements = departmentRepository.getCount(request.nameOrDescription());
	//
	// 	return new CursorPageResponseDepartmentDto(
	// 		dtos,
	// 		nextCursor,
	// 		nextIdAfter,
	// 		request.size(),
	// 		totalElements,
	// 		hasNext
	// 	);
	// }

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