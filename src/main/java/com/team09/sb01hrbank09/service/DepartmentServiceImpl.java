package com.team09.sb01hrbank09.service;

import com.team09.sb01hrbank09.dto.entityDto.DepartmentDto;
import com.team09.sb01hrbank09.dto.request.CursorPageRequestDepartment;
import com.team09.sb01hrbank09.dto.request.DepartmentCreateRequest;
import com.team09.sb01hrbank09.dto.request.DepartmentUpdateRequest;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseDepartmentDto;
import com.team09.sb01hrbank09.entity.Department;
import com.team09.sb01hrbank09.mapper.DepartmentMapper;
import com.team09.sb01hrbank09.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentServiceInterface{
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    @Override
    public DepartmentDto createDepartment(DepartmentCreateRequest request) {
        Optional<Department> existingDepartment = departmentRepository.findByName(request.name());
        if (existingDepartment.isPresent()) {
            throw new IllegalArgumentException("생성하려는 부서명이 이미 존재합니다.");
        }


        Department department = Department.createDepartments(request.name(), request.description(), request.establishedDate());

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
        // if (request.sortDirection() == null || (!request.sortDirection().equalsIgnoreCase("ASC") && !request.sortDirection().equalsIgnoreCase("DESC"))) {
        //     throw new IllegalArgumentException("유효하지 않은 정렬 방향입니다.");
        // }
        // if (request.sortField() == null || (!request.sortField().equals("name") && !request.sortField().equals("establishedDate"))) {
        //     throw new IllegalArgumentException("유효하지 않은 정렬 필드입니다.");
        // }
        // Sort sort = Sort.by(Sort.Direction.fromString(request.sortDirection()), request.sortField()); //소팅전략
        // PageRequest pageRequest = PageRequest.of(0, request.size(), sort);
        //
        // String escapedSearchTerm = escapeSpecialCharacters(request.nameOrDescription()); //injection, 특수문자 방지
        //
        // List<Department> departments;
        // long totalElements;
        // if (request.idAfter() != null) {
        //     departments = departmentRepository.findByIdGreaterThanAndNameContainingOrDescriptionContaining(
        //             request.idAfter(), escapedSearchTerm, escapedSearchTerm, pageRequest
        //     );
        //     totalElements = departmentRepository.countByIdGreaterThanAndNameContainingOrDescriptionContaining(
        //             request.idAfter(), escapedSearchTerm, escapedSearchTerm
        //     );
        // } else {
        //     departments = departmentRepository.findByNameContainingOrDescriptionContaining(
        //             escapedSearchTerm, escapedSearchTerm, pageRequest
        //     );
        //     totalElements = departmentRepository.countByNameContainingOrDescriptionContaining(
        //             escapedSearchTerm, escapedSearchTerm
        //     );
        // }
        //
        // List<DepartmentDto> departmentDtos = departments.stream()
        //         .map(departmentMapper::departmentToDto)
        //         .toList();
        //
        // Long nextIdAfter = null;
        // String nextCursor = null;
        // boolean hasNext = false;
        // if (!departments.isEmpty()) {
        //     nextIdAfter = departments.get(departments.size() - 1).getId();
        //     nextCursor = String.valueOf(nextIdAfter);
        //
        //     if (request.idAfter() != null) {
        //         hasNext = departmentRepository.findFirstByIdGreaterThanAndNameContainingOrDescriptionContaining(
        //                 nextIdAfter, escapedSearchTerm, escapedSearchTerm, PageRequest.of(0, 1, sort)
        //         ).isPresent();
        //     } else {
        //         hasNext = departmentRepository.findFirstByNameContainingOrDescriptionContaining(
        //                 escapedSearchTerm, escapedSearchTerm, PageRequest.of(0, 1, sort)
        //         ).isPresent();
        //     }
        // }

        //return new CursorPageResponseDepartmentDto(departmentDtos, nextCursor, nextIdAfter, request.size(), totalElements, hasNext);
        return null;
    }
    
    private String escapeSpecialCharacters(String searchTerm) {
        if (searchTerm == null) {
            return null;
        }
        Pattern specialCharacters = Pattern.compile("[\\(\\)\\[\\]\\{\\}\\^\\$\\.\\*\\+\\?\\|\\\\]");
        Matcher matcher = specialCharacters.matcher(searchTerm);
        return matcher.replaceAll("\\\\$0");
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
