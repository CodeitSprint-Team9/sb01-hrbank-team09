package com.team09.sb01hrbank09.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.team09.sb01hrbank09.dto.entityDto.EmployeeDistributionDto;
import com.team09.sb01hrbank09.dto.entityDto.EmployeeDto;
import com.team09.sb01hrbank09.dto.entityDto.EmployeeTrendDto;
import com.team09.sb01hrbank09.dto.request.CursorPageRequestBackupDto;
import com.team09.sb01hrbank09.dto.request.EmployeeCreateRequest;
import com.team09.sb01hrbank09.dto.request.EmployeeUpdateRequest;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseEmployeeDto;
import com.team09.sb01hrbank09.entity.Backup;
import com.team09.sb01hrbank09.entity.Department;
import com.team09.sb01hrbank09.entity.Employee;
import com.team09.sb01hrbank09.entity.Enum.ChangeLogType;
import com.team09.sb01hrbank09.entity.Enum.EmployeeStatus;
import com.team09.sb01hrbank09.entity.File;
import com.team09.sb01hrbank09.mapper.EmployeeMapper;
import com.team09.sb01hrbank09.repository.EmployeeRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeServiceInterface {

	private final EmployeeRepository employeeRepository;
	private final DepartmentServiceInterface departmentServiceInterface;
	private final FileServiceInterface fileServiceInterface;
	private final ChangeLogServiceInterface changeLogServiceInterface;
	private final EmployeeMapper employeeMapper;

	@Autowired
	public EmployeeServiceImpl(
		@Lazy EmployeeRepository employeeRepository,
		@Lazy DepartmentServiceInterface departmentServiceInterface,
		@Lazy FileServiceInterface fileServiceInterface,
		@Lazy ChangeLogServiceInterface changeLogServiceInterface,
		@Lazy EmployeeMapper employeeMapper) {
		this.employeeRepository = employeeRepository;
		this.departmentServiceInterface = departmentServiceInterface;
		this.fileServiceInterface = fileServiceInterface;
		this.changeLogServiceInterface = changeLogServiceInterface;
		this.employeeMapper = employeeMapper;
	}

	private Instant updateTime = Instant.EPOCH;

	@Override
	@Transactional
	public EmployeeDto creatEmployee(EmployeeCreateRequest employeeCreateRequest, MultipartFile profileImg,
		String ipAddress) throws
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
		String employeeNumber = "EMP-" + Year.now().getValue()+"-" + uniquePart;
		Employee employee = Employee.createEmployee(employeeCreateRequest.name(), employeeCreateRequest.email(),
			employeeNumber, employeeCreateRequest.position(),
			employeeCreateRequest.hireDate(), EmployeeStatus.ACTIVE, file, usingDepartment);

		//EmployeeDto newEmployee=employeeMapper.employeeToDto(employee);
		//만들어지면 넣기
		String memo;
		if (employeeCreateRequest.memo() == null) {
			memo = "신규 직원 등록";
		} else {
			memo = employeeCreateRequest.memo();
		}
		log.info("changelog 생성 시작...");
		changeLogServiceInterface.createChangeLog(
			ChangeLogType.CREATED, employee.getEmployeeNumber(), memo, ipAddress, null,
			employeeMapper.employeeToDto(employee)
		);
		log.info("change-logs 생성 완료");

		usingDepartment.increaseCount();
		this.updateTime = Instant.now();
		return employeeMapper.employeeToDto(employeeRepository.save(employee));
	}

	@Override
	@Transactional(readOnly = true)
	public EmployeeDto findEmployeeById(Long Id) {
		Employee employee = employeeRepository.findById(Id)
			.orElseThrow(() -> new NoSuchElementException("employee with id " + Id + " not found"));
		return employeeMapper.employeeToDto(employee);
	}

	@Override
	@Transactional(readOnly = true)
	public CursorPageResponseEmployeeDto findEmployeeList(
		String nameOrEmail, String employeeNumber, String departmentName, String position,
		LocalDate hireDateFrom, LocalDate hireDateTo, String status, Long idAfter,
		Object cursor, int size, String sortField, String sortDirection) {

		Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
		Sort sort = Sort.by(direction, sortField);

		Pageable pageable = PageRequest.of(0, size + 1, sort);

		List<Employee> employees = employeeRepository.findEmployeesWithAdvancedFilters(
			nameOrEmail, employeeNumber, departmentName, position, hireDateFrom,
			hireDateTo, status, idAfter, (Long)cursor, pageable);
		boolean hasNext = false;
		if (employees.size() > size) {
			hasNext = true;
			employees = employees.subList(0, size);
		}

		Long nextIdAfter = hasNext ? employees.get(employees.size() - 1).getId() : null;

		Long nextCursor = hasNext ? employees.get(employees.size() - 1).getId() : null;

		List<EmployeeDto> employeeDtos = employees.stream()
			.map(employeeMapper::employeeToDto)
			.collect(Collectors.toList());

		return new CursorPageResponseEmployeeDto(
			employeeDtos,
			nextCursor != null ? nextCursor.toString() : null,
			nextIdAfter, size, employeeRepository.count(), hasNext
		);
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
	public boolean deleteEmployee(Long id, String ipAddress) {
		if (employeeRepository.existsById(id)) {

			Employee employee = employeeRepository.findById(id).get();
			fileServiceInterface.deleteFile(employee.getFile());
			employee.getDepartment().decreaseCount();
			employeeRepository.deleteById(id);
			this.updateTime = Instant.now();
			//로그작업


			log.info("이벤트 발행시작...");
			changeLogServiceInterface.createChangeLog(
				ChangeLogType.DELETED, employee.getEmployeeNumber(), "직원 삭제", ipAddress,
				employeeMapper.employeeToDto(employee), null
			);
			log.info("change-logs 생성 완료");

			return true;
		}
		return false;
	}

	@Override
	@Transactional
	public EmployeeDto updateEmployee(Long id, EmployeeUpdateRequest employeeUpdateRequest,
		MultipartFile profileImg, String ipAddress) throws
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

		employee.getDepartment().decreaseCount();

		employee.updateName(employeeUpdateRequest.name());
		employee.updateEmail(employeeUpdateRequest.email());
		employee.updateDepartment(usingDepartment);
		employee.updatePosition(employeeUpdateRequest.position());
		employee.updateHireDateFrom(employeeUpdateRequest.hireDate());
		employee.updateStatus(status);

		if (profileImg != null) {
			if (employee.getFile() != null) {
				File oldFile = fileServiceInterface.findById(employee.getFile().getId());
				employee.updateFile(null);
				fileServiceInterface.deleteFile(oldFile);
			}
			file = fileServiceInterface.createImgFile(profileImg);
			employee.updateFile(file);
		} else {
			employee.updateFile(file);
		}
		employee.getDepartment().increaseCount();

		updateTime = Instant.now();

		EmployeeDto oldEmployee = employeeMapper.employeeToDto(employee);


		EmployeeDto afterEmployee = employeeMapper.employeeToDto(employee);

		log.info("Change-logs 생성중...");
		changeLogServiceInterface.createChangeLog(
			ChangeLogType.UPDATED, employee.getEmployeeNumber(), "직원 수정", ipAddress,
			oldEmployee, afterEmployee

		);
		log.info("change-logs 생성 완료");

		return employeeMapper.employeeToDto(employee);
	}

	@Override
	@Transactional(readOnly = true)
	public List<EmployeeTrendDto> getEmployeeTrend(LocalDate startedAt, LocalDate endedAt, String gap) {

		List<Object[]> results = employeeRepository.findEmployeeTrend(startedAt, endedAt, gap);
		List<EmployeeTrendDto> trendList = new ArrayList<>();
		Long previousCount = null;

		for (Object[] result : results) {
			Instant periodDate;

			if (result[0] instanceof Timestamp) {
				periodDate = ((Timestamp) result[0]).toInstant();
			} else if (result[0] instanceof LocalDate) {
				periodDate = ((LocalDate) result[0]).atStartOfDay(ZoneOffset.UTC).toInstant();
			} else {
				periodDate = (Instant) result[0];
			}

			Long count = ((Number) result[1]).longValue();
			Long change = (previousCount == null) ? 0L : count - previousCount;
			Double changeRate = (previousCount == null || previousCount == 0L) ? 0.0 : (double) change / previousCount;

			trendList.add(new EmployeeTrendDto(periodDate, count, change, changeRate));
			previousCount = count;
		}
		return trendList;
	}

	@Override
	@Transactional(readOnly = true)
	public List<EmployeeDistributionDto> getEmployeeDistributaion(String groupBy, String status) {

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
	public Long countEmployee(String status, LocalDate startedAt, LocalDate endedAt) {
		boolean isValidStatus = Arrays.stream(EmployeeStatus.values())
			.anyMatch(e -> e.name().equalsIgnoreCase(status));
		EmployeeStatus findStatus = null;
		if (!isValidStatus) {
			findStatus = EmployeeStatus.ACTIVE;
		} else
			findStatus = EmployeeStatus.valueOf(status.toUpperCase());
		return employeeRepository.countByStatusAndHireDateBetween(findStatus, startedAt, endedAt);
	}

	private List<EmployeeDistributionDto> convertDistributionPosition(String status) {
		List<EmployeeDistributionDto> distribution = new ArrayList<>();
		List<Object[]> results = employeeRepository.findDistributionPosition(
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
		List<Object[]> results = employeeRepository.findDistributionDepartment(
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

	@Override
	public Instant getUpdateTime() {
		return updateTime;
	}

	private Page<Employee> getEmployees(
		String nameOrEmail, String employeeNumber, String departmentName, String position,
		LocalDate hireDateFrom, LocalDate hireDateTo, EmployeeStatus status,
		LocalDate cursorHireDate, Long cursorId, Pageable pageable, String sortDirection) {

		if (sortDirection.equalsIgnoreCase("asc")) {
			return employeeRepository.findEmployeesByCursorOrderByIdAsc(
				nameOrEmail, employeeNumber, departmentName, position,
				hireDateFrom, hireDateTo, status, cursorHireDate, cursorId, pageable
			);
		} else {
			return employeeRepository.findEmployeesByCursorOrderByIdDesc(
				nameOrEmail, employeeNumber, departmentName, position,
				hireDateFrom, hireDateTo, status, cursorHireDate, cursorId, pageable
			);
		}
	}
}
