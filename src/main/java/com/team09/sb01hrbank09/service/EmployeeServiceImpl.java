package com.team09.sb01hrbank09.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.team09.sb01hrbank09.dto.request.EmployeeCreateRequest;
import com.team09.sb01hrbank09.dto.request.EmployeeUpdateRequest;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseEmployeeDto;
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
		String employeeNumber = "EMP-" + "년도" + uniquePart;
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
	public CursorPageResponseEmployeeDto findEmployeeList(String nameOrEmail, String employeeNumber,
		String departmentName, String position, String hireDateFrom, String hireDateTo, String status, Long idAfter,
		String cursor, int size, String sortField, String sortDirection) {

		// 날짜를 Instant로 변환
		Instant hireDateFromInstant = hireDateFrom != null ? Instant.parse(hireDateFrom) : null;
		Instant hireDateToInstant = hireDateTo != null ? Instant.parse(hireDateTo) : null;

		// 정렬 필드와 방향을 설정
		Sort.Order sortOrder =
			sortDirection.equalsIgnoreCase("desc") ? Sort.Order.desc(sortField) : Sort.Order.asc(sortField);
		Sort sort = Sort.by(sortOrder);

		// 커서 기반 페이지네이션을 위한 Pageable 객체 생성
		Pageable pageable = PageRequest.of(cursor != null ? Integer.parseInt(cursor) : 0, size, sort);

		// 필터를 적용한 직원 리스트 조회
		LocalDateTime hireDateFromTimestamp =
			hireDateFromInstant != null ? LocalDateTime.from(hireDateFromInstant) : null;
		LocalDateTime hireDateToTimestamp = hireDateToInstant != null ? LocalDateTime.from(hireDateToInstant) : null;

		// 직원 목록을 필터링하여 조회 (필터 및 페이징 처리)
		Page<Employee> employeePage = employeeRepository.findEmployeesWithFilters(
			nameOrEmail, employeeNumber, departmentName, position,
			hireDateFromTimestamp, hireDateToTimestamp,
			status, idAfter, pageable
		);

		// 페이지네이션 처리된 직원 목록을 DTO로 변환
		List<EmployeeDto> employeeDtos = employeePage.getContent().stream()
			.map(employee -> new EmployeeDto(
				employee.getId(),
				employee.getName(),
				employee.getEmail(),
				employee.getEmployeeNumber(),
				employee.getDepartment().getId(),
				employee.getDepartment().getName(),
				employee.getPosition(),
				employee.getHireDateFrom(),
				employee.getStatus().toString(),
				Optional.ofNullable(employee.getFile()).map(file -> file.getId()).orElse(null)
			))
			.collect(Collectors.toList());

		// 커서 기반 페이지네이션을 위한 커서값 계산
		String nextCursor = null;
		Long nextIdAfter = null;
		boolean hasNext = employeePage.hasNext();

		// 다음 페이지 커서값 및 nextIdAfter 값 설정
		if (hasNext) {
			nextCursor = String.valueOf(employeePage.getNumber() + 1); // 다음 페이지의 커서값
			nextIdAfter = employeePage.getContent().get(employeePage.getContent().size() - 1).getId();
		}

		// 전체 직원 수 설정
		Long totalElements = employeePage.getTotalElements();

		// 결과를 CursorPageResponseEmployeeDto로 반환
		return new CursorPageResponseEmployeeDto(
			employeeDtos,
			nextCursor,
			nextIdAfter,
			size,
			totalElements,
			hasNext
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
		EmployeeDto newEmployee = employeeMapper.employeeToDto(employee);
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
		//만들어지면 넣기
		//changeLogServiceInterface.createChangeLog();

		EmployeeDto afterEmployee = employeeMapper.employeeToDto(employee);

		log.info("Change-logs 생성중...");
		changeLogServiceInterface.createChangeLog(
			ChangeLogType.UPDATED, employee.getEmployeeNumber(), "직원 삭제", ipAddress,
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
		return employeeRepository.countByStatusAndHireDateFromBetween(findStatus, startedAt, endedAt);
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
