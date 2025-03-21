package com.team09.sb01hrbank09.service;

import com.team09.sb01hrbank09.dto.entityDto.DepartmentDto;
import com.team09.sb01hrbank09.dto.request.CursorPageRequestChangeLog;
import com.team09.sb01hrbank09.dto.request.CursorPageRequestDepartment;
import com.team09.sb01hrbank09.dto.request.DepartmentCreateRequest;
import com.team09.sb01hrbank09.dto.request.DepartmentUpdateRequest;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseDepartmentDto;
import com.team09.sb01hrbank09.entity.ChangeLog;
import com.team09.sb01hrbank09.entity.Department;
import com.team09.sb01hrbank09.mapper.DepartmentMapper;
import com.team09.sb01hrbank09.repository.DepartmentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



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




		Sort sort = Sort.by(Sort.Direction.fromString(request.sortDirection()), request.sortField());
		Pageable pageable = PageRequest.of(0, request.size(), sort);
		List<Department> departments = getDepartments(request);


		//Page<Department> departmentsPage = getDepartments(request, sortDirection);
		List<DepartmentDto> departmentDtos = departments.stream()
			.map(departmentMapper::departmentToDto)
			.toList();

		return toCursorPageResponse(departmentDtos, request);
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

	private List<Department> getDepartments(CursorPageRequestDepartment request) {
		String sortField = request.sortField();
		String direction = request.sortDirection();

		if ("asc".equalsIgnoreCase(direction)) {
			if ("establishedDate".equalsIgnoreCase(sortField)) {
				return departmentRepository.findDepartmentDateAsc(request.nameOrDescription(), request.idAfter(),
					request.sortField());
			} else
				return departmentRepository.findDepartmentNameAsc(request.nameOrDescription(), request.idAfter(),
					request.sortField());
		}
		else if ("establishedDate".equalsIgnoreCase(sortField)) {
			return departmentRepository.findDepartmentDateDesc(request.nameOrDescription(), request.idAfter(),
				request.sortField());
		}
		return departmentRepository.findDepartmentNameDesc(request.nameOrDescription(), request.idAfter(),
			request.sortField());

	}

	private CursorPageResponseDepartmentDto toCursorPageResponse(List<DepartmentDto> dtos, CursorPageRequestDepartment request) {
		Long nextIdAfter = null;
		String nextCursor = null;
		boolean hasNext = false;

		if (!dtos.isEmpty()) {
			nextIdAfter = dtos.get(dtos.size() - 1).id();
			nextCursor = String.valueOf(nextIdAfter);

			// 다음 페이지의 데이터가 있는지 확인
			List<Department> nextDepartment = getDepartments(
				CursorPageRequestDepartment.copy(request, nextIdAfter, nextCursor));
			hasNext = !nextDepartment.isEmpty();
		}

			// if (departmentsPage.hasNext()) {
			// 	Pageable nextPageable = departmentsPage.nextPageable();
			// 	Page<Department> nextDepartmentsPage = getDepartments(CursorPageRequestDepartment.copy(request, nextIdAfter, nextCursor), nextPageable);
			// 	hasNext = !nextDepartmentsPage.getContent().isEmpty();
			// }


		long totalCount = departmentRepository.getTotalElements(request.nameOrDescription());

		return new CursorPageResponseDepartmentDto(
			dtos,
			nextCursor,
			nextIdAfter,
			request.size(),
			totalCount,
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
