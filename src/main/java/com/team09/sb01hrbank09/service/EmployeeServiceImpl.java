package com.team09.sb01hrbank09.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.team09.sb01hrbank09.dto.entityDto.EmployeeDistributionDto;
import com.team09.sb01hrbank09.dto.entityDto.EmployeeDto;
import com.team09.sb01hrbank09.dto.entityDto.EmployeeTrendDto;
import com.team09.sb01hrbank09.dto.request.EmployeeCreateRequest;
import com.team09.sb01hrbank09.dto.request.EmployeeUpdateRequest;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseEmployeeDto;
import com.team09.sb01hrbank09.entity.Department;
import com.team09.sb01hrbank09.entity.Employee;
import com.team09.sb01hrbank09.entity.Enum.EmployeeStatus;
import com.team09.sb01hrbank09.entity.File;
import com.team09.sb01hrbank09.mapper.EmployeeMapper;
import com.team09.sb01hrbank09.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeServiceInterface {

	private final EmployeeRepository employeeRepository;
	private final DepartmentServiceInterface departmentServiceInterface;
	private final FileServiceInterface fileServiceInterface;
	private final ChangeLogServiceInterface changeLogServiceInterface;
	private final EmployeeMapper employeeMapper;

	private Instant updateTime = null;

	@Override
	@Transactional
	public EmployeeDto creatEmployee(EmployeeCreateRequest employeeCreateRequest, MultipartFile profileImg) throws
		IOException {
		Department usingDepartment = departmentServiceInterface.findDepartmentEntityById(
			employeeCreateRequest.departmentId());
		if (usingDepartment == null) {
			throw new NoSuchElementException("Department 아이디가 존재하지 않음");
		}

		File file = null;
		if (profileImg != null) {
			file = fileServiceInterface.createImgFile(profileImg);
		}

		String uniquePart = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 14);
		String employeeNumber = "EMP-" + "년도" + uniquePart;
		Employee employee = Employee.createEmployee(employeeCreateRequest.name(), employeeCreateRequest.email(),
			employeeNumber, employeeCreateRequest.position(),
			employeeCreateRequest.hireDate(), EmployeeStatus.ACTIVE, file, usingDepartment);

		//만들어지면 넣기
		//changeLogServiceInterface.createChangeLog(ChnageLogDto request);

		updateTime = Instant.now();
		return employeeMapper.employeeToDto(employeeRepository.save(employee));
	}

	@Override
	@Transactional(readOnly = true)
	public EmployeeDto findEmployeeById(Long Id) {
		Employee employee = employeeRepository.findById(Id)
			.orElseThrow(() -> new NoSuchElementException("Message with id " + Id + " not found"));
		return employeeMapper.employeeToDto(employee);
	}

	@Override
	@Transactional(readOnly = true)
	public CursorPageResponseEmployeeDto findEmployeeList(String nameOrEmail, String employeeNumber,
		String departmentName, String position, String hireDateFrom, String hireDateTo, String status, Long idAfter,
		String cursor, int size, String sortField, String sortDirection) {

		// if (sortDirection == null || (!sortDirection.equalsIgnoreCase("ASC") && !sortDirection.equalsIgnoreCase("DESC"))) {
		// 	throw new IllegalArgumentException("유효하지 않은 정렬 방향입니다.");
		// }
		// if (sortField == null || (!sortField.equals("name") && !sortField.equals("hireDate"))) {
		// 	throw new IllegalArgumentException("유효하지 않은 정렬 필드입니다.");
		// }

		Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
		PageRequest pageRequest = PageRequest.of(0, size, sort);

		String escapedSearchTerm = escapeSpecialCharacters(nameOrEmail);

		List<Employee> employees;
		long totalElements;
		if (idAfter != null) {
			employees = employeeRepository.findByIdGreaterThanAndFilters(
				idAfter, escapedSearchTerm, employeeNumber, departmentName, position, Instant.parse(hireDateFrom),
				Instant.parse(hireDateTo), EmployeeStatus.valueOf(status.toUpperCase()), pageRequest
			);
			totalElements = employeeRepository.countByIdGreaterThanAndFilters(
				idAfter, escapedSearchTerm, employeeNumber, departmentName, position,
				Instant.parse(hireDateFrom), Instant.parse(hireDateTo), EmployeeStatus.valueOf(status.toUpperCase())
			);
		} else {
			employees = employeeRepository.findByFilters(
				escapedSearchTerm, employeeNumber, departmentName, position, Instant.parse(hireDateFrom),
				Instant.parse(hireDateTo), EmployeeStatus.valueOf(status.toUpperCase()), pageRequest
			);
			totalElements = employeeRepository.countByFilters(
				escapedSearchTerm, employeeNumber, departmentName, position,
				Instant.parse(hireDateFrom), Instant.parse(hireDateTo), EmployeeStatus.valueOf(status.toUpperCase())
			);
		}

		List<EmployeeDto> employeeDtos = employees.stream()
			.map(employeeMapper::employeeToDto)
			.toList();

		Long nextIdAfter = null;
		String nextCursor = null;
		boolean hasNext = false;
		if (!employees.isEmpty()) {
			nextIdAfter = employees.get(employees.size() - 1).getId();
			nextCursor = String.valueOf(nextIdAfter);
			if (idAfter != null) {
				hasNext = employeeRepository.findFirstByIdGreaterThanAndNameContainingOrDescriptionContaining(
					nextIdAfter, escapedSearchTerm, escapedSearchTerm, PageRequest.of(0, 1, sort)
				).isPresent();
			} else {
				hasNext = employeeRepository.findFirstByNameContainingOrDescriptionContaining(
					escapedSearchTerm, escapedSearchTerm, PageRequest.of(0, 1, sort)
				).isPresent();
			}
		}

		return new CursorPageResponseEmployeeDto(employeeDtos, nextCursor, nextIdAfter, size, totalElements, hasNext);
	}

	@Override
	@Transactional(readOnly = true)
	public List<EmployeeDto> getEmployeeAllList() {
		List<Employee> find = employeeRepository.findAll();
		return find.stream()
			.map(employeeMapper::employeeToDto)
			.toList();
	}

	@Override
	@Transactional
	public boolean deleteEmployee(Long id) {
		if (employeeRepository.existsById(id)) {
			Employee employee = employeeRepository.findById(id).get();
			fileServiceInterface.deleteFile(employee.getFile());
			employeeRepository.deleteById(id);
			updateTime = Instant.now();
			return true;
		}
		return false;
	}

	@Override
	@Transactional
	public EmployeeDto updateEmployee(Long id, EmployeeUpdateRequest employeeUpdateRequest,
		MultipartFile profileImg) throws
		IOException {

		Employee employee = employeeRepository.findById(id)

			.orElseThrow(() -> new NoSuchElementException("Message with id " + id + " not found"));
		File file = null;

		Department usingDepartment = departmentServiceInterface.findDepartmentEntityById(
			employeeUpdateRequest.departmentId());
		if (usingDepartment == null) {
			throw new NoSuchElementException("Department 아이디가 존재하지 않음");
		}
		EmployeeStatus status = EmployeeStatus.valueOf(employeeUpdateRequest.status().toUpperCase());

		employee.updateName(employeeUpdateRequest.name());
		employee.updateEmail(employeeUpdateRequest.email());
		employee.updateDepartment(usingDepartment);
		employee.updatePosition(employeeUpdateRequest.position());
		employee.updateHireDateFrom(employeeUpdateRequest.hireDate());
		employee.updateStatus(status);

		if (profileImg != null) {
			fileServiceInterface.deleteFile(employee.getFile());
			file = fileServiceInterface.createImgFile(profileImg);
			employee.updateFile(file);
		}

		//만들어지면 넣기
		//changeLogServiceInterface.createChangeLog(ChnageLogDto request,EmployeeDto old, EmployeeDto new);
		updateTime = Instant.now();
		return employeeMapper.employeeToDto(employee);
	}

	@Override
	@Transactional(readOnly = true)
	public List<EmployeeTrendDto> getEmployeeTrend(Instant startedAt, Instant endedAt, String gap) {

		List<Object[]> results = employeeRepository.findEmployeeTrend(startedAt, endedAt, gap);

		List<EmployeeTrendDto> trends = new ArrayList<>();
		long previousCount = 0;

		for (Object[] row : results) {
			Instant date = ((Timestamp)row[0]).toInstant();
			long count = ((Number)row[1]).longValue();

			long change = count - previousCount;
			double changeRate = (previousCount == 0) ? 0.0 : (change * 100.0 / previousCount);
			trends.add(new EmployeeTrendDto(date, count, change, changeRate));
			previousCount = count;
		}

		return trends;
	}

	@Override
	@Transactional(readOnly = true)
	public List<EmployeeDistributionDto> getEmployeeDistributaion(String groupBy, String status) {

		List<EmployeeDistributionDto> distribution;
		if (groupBy.equals("position")) {
			return convertDistributionPosition(status);
		} else if (groupBy.equals("department")) {
			return convertDistributionDepartment(status);
		} else {
			return convertDistributionDepartment(status);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Long countEmployee(String status, Instant startedAt, Instant endedAt) {
		EmployeeStatus findStatus = EmployeeStatus.valueOf(status.toUpperCase());
		return employeeRepository.countByStatusAndCreatedAtBetween(findStatus, startedAt, endedAt);
	}

	private List<EmployeeDistributionDto> convertDistributionPosition(String status) {
		List<EmployeeDistributionDto> distribution = new ArrayList<>();
		List<Object[]> results = employeeRepository.findDistributatinPosition(
			EmployeeStatus.valueOf(status.toUpperCase()));
		for (Object[] row : results) {
			String positionName = (String)row[0];
			Long totalEmployees = (Long)row[1];
			Long activeEmployees = (Long)row[2];
			double ratio = (activeEmployees == 0) ? 0.0 : ((double)totalEmployees * 100 / activeEmployees);
			distribution.add(new EmployeeDistributionDto(positionName, totalEmployees,
				ratio));
		}
		return distribution;
	}

	private List<EmployeeDistributionDto> convertDistributionDepartment(String status) {
		List<EmployeeDistributionDto> distribution = new ArrayList<>();
		List<Object[]> results = employeeRepository.findDistributatinDepartment(
			EmployeeStatus.valueOf(status.toUpperCase()));
		for (Object[] row : results) {
			Long departmentId = (Long)row[0];
			Long totalEmployees = (Long)row[1];
			Long activeEmployees = (Long)row[2];
			double ratio = (activeEmployees == 0) ? 0.0 : ((double)totalEmployees * 100 / activeEmployees);
			String departmentName = departmentServiceInterface.findDepartmentById(departmentId).name();
			distribution.add(new EmployeeDistributionDto(departmentName, totalEmployees,
				ratio));
		}
		return distribution;
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
	public Instant getUpdateTime() {
		return updateTime;
	}
}
