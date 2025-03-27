package com.team09.sb01hrbank09.service;

import com.team09.sb01hrbank09.dto.request.CursorPageRequestEmployeeDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
  public EmployeeDto creatEmployee(EmployeeCreateRequest employeeCreateRequest,
      MultipartFile profileImg,
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
    String employeeNumber = "EMP-" + Year.now().getValue() + "-" + uniquePart;
    Employee employee = Employee.createEmployee(employeeCreateRequest.name(),
        employeeCreateRequest.email(),
        employeeNumber, employeeCreateRequest.position(),
        employeeCreateRequest.hireDate(), EmployeeStatus.ACTIVE, file, usingDepartment);

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
  public CursorPageResponseEmployeeDto findEmployeeList(CursorPageRequestEmployeeDto request) {

    Sort.Direction direction =
        "desc".equalsIgnoreCase(request.sortDirection()) ? Sort.Direction.DESC : Sort.Direction.ASC;
    Sort sort = Sort.by(direction, request.sortField());

    Pageable pageable = PageRequest.of(0, request.size() + 1, sort);

    List<Employee> employees = getEmployees(request, pageable);

    boolean hasNext = false;
    if (employees.size() > request.size()) {
      hasNext = true;
      employees = employees.subList(0, request.size());
    }

    Long totalElements = employeeRepository.getCount(
        request.nameOrEmail(),
        request.departmentName(),
        request.position(),
        request.status()
    );

    Long nextIdAfter = null;
    String nextCursor = null;
    if (!employees.isEmpty()) {
      Employee lastEmployee = employees.get(employees.size() - 1);
      nextIdAfter = lastEmployee.getId();

      if (request.sortField().equals("hireDate")) {
        nextCursor = lastEmployee.getHireDate().toString();
      } else if (request.sortField().equals("employeeNumber")) {
        nextCursor = lastEmployee.getEmployeeNumber();
      } else if (request.sortField().equals("name")) {
        nextCursor = lastEmployee.getName();
      }
    }

    List<EmployeeDto> employeeDtos = employees.stream()
        .map(employeeMapper::employeeToDto)
        .collect(Collectors.toList());

    return new CursorPageResponseEmployeeDto(
        employeeDtos,
        nextCursor,
        nextIdAfter,
        request.size(),
        totalElements,
        hasNext
    );
  }

  private List<Employee> getEmployees(CursorPageRequestEmployeeDto request, Pageable pageable) {
    List<Employee> employees = null;

    if (request.sortField().equals("hireDate")) {
      if ("desc".equalsIgnoreCase(request.sortDirection())) {
        employees = employeeRepository.findEmployeesWithHireDateDesc(
            request.nameOrEmail(),
            request.departmentName(),
            request.position(),
            request.status(),
            LocalDate.parse(request.cursor()),
            request.idAfter(),
            pageable
        );
      } else if ("asc".equalsIgnoreCase(request.sortDirection())) {
        employees = employeeRepository.findEmployeesWithHireDateAsc(
            request.nameOrEmail(),
            request.departmentName(),
            request.position(),
            request.status(),
            LocalDate.parse(request.cursor()),
            request.idAfter(),
            pageable
        );
      }
    } else if (request.sortField().equals("employeeNumber")) {
      if ("desc".equalsIgnoreCase(request.sortDirection())) {
        employees = employeeRepository.findEmployeesWithEmployeeNumberDesc(
            request.nameOrEmail(),
            request.departmentName(),
            request.position(),
            request.status(),
            request.cursor(),
            request.idAfter(),
            pageable
        );
      } else if ("asc".equalsIgnoreCase(request.sortDirection())) {
        employees = employeeRepository.findEmployeesWithEmployeeNumberAsc(
            request.nameOrEmail(),
            request.departmentName(),
            request.position(),
            request.status(),
            request.cursor(),
            request.idAfter(),
            pageable
        );
      }
    } else if (request.sortField().equals("name")) {
      if ("desc".equalsIgnoreCase(request.sortDirection())) {
        employees = employeeRepository.findEmployeesWithNameDesc(
            request.nameOrEmail(),
            request.departmentName(),
            request.position(),
            request.status(),
            request.cursor(),
            request.idAfter(),
            pageable
        );
      } else if ("asc".equalsIgnoreCase(request.sortDirection())) {
        employees = employeeRepository.findEmployeesWithNameAsc(
            request.nameOrEmail(),
            request.departmentName(),
            request.position(),
            request.status(),
            request.cursor(),
            request.idAfter(),
            pageable
        );
      }

    }
    return employees;
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

    EmployeeDto newEmployee = employeeMapper.employeeToDto(employee);

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

    EmployeeDto afterEmployee = employeeMapper.employeeToDto(employee);

    String memo;
    if (employeeUpdateRequest.memo() == null) {
      memo = "직원 수정";
    } else {
      memo = employeeUpdateRequest.memo();
    }

    log.info("Change-logs 생성중...");
    changeLogServiceInterface.createChangeLog(
        ChangeLogType.UPDATED, employee.getEmployeeNumber(), memo, ipAddress,
        newEmployee, afterEmployee

    );
    log.info("change-logs 생성 완료");

    return employeeMapper.employeeToDto(employee);
  }

  @Override
  @Transactional(readOnly = true)
  public List<EmployeeTrendDto> getEmployeeTrend(LocalDate startedAt, LocalDate endedAt,
      String gap) {

    List<EmployeeTrendDto> list = new ArrayList<>();
    LocalDate fromDate = LocalDate.of(1970, 1, 1); //POSIX 시간

    int beforeCount = employeeRepository.countEmployees(EmployeeStatus.ACTIVE, fromDate,
        startedAt);

    list.add(new EmployeeTrendDto(startedAt, beforeCount, 0, 0.0));

    while (!startedAt.isAfter(endedAt)) {
      LocalDate nextFrom;

      switch (gap) {
        case "day":
          nextFrom = startedAt.plusDays(1);
          break;
        case "week":
          nextFrom = startedAt.plusWeeks(1);
          break;
        case "quarter":
          nextFrom = startedAt.plusMonths(3);
          break;
        case "year":
          nextFrom = startedAt.plusYears(1);
          break;
        case "month":
        default:
          nextFrom = startedAt.plusMonths(1);
          break;
      }

      Integer activeCount = employeeRepository.countEmployees(EmployeeStatus.ACTIVE,
          fromDate, nextFrom);
      Integer onLeaveCount = employeeRepository.countEmployees(EmployeeStatus.ON_LEAVE,
          fromDate, nextFrom);

      int count =
          (activeCount != null ? activeCount : 0) + (onLeaveCount != null ? onLeaveCount : 0);
      int change = count - beforeCount;
      double percentage = calculatePercentage(count, change, beforeCount);

      percentage = Math.floor(percentage * 10) / 10;

      list.add(new EmployeeTrendDto(nextFrom, Math.abs(count), change, percentage));

      beforeCount = count;
      startedAt = nextFrom;
    }
    list.remove(list.size() - 1);

    return list;
  }

  private double calculatePercentage(int count, int change, int beforeCount) {
    if (beforeCount == 0) {
      return count > 0 ? 100.0 : 0.0;
    }
    return ((double) change / beforeCount) * 100.0;
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
    } else {
      findStatus = EmployeeStatus.valueOf(status.toUpperCase());
    }
    return employeeRepository.countByStatusAndHireDateBetween(findStatus, startedAt, endedAt);
  }

  private List<EmployeeDistributionDto> convertDistributionPosition(String status) {
    List<EmployeeDistributionDto> distribution = new ArrayList<>();
    List<Object[]> results = employeeRepository.findDistributionPosition(
        EmployeeStatus.valueOf(status.toUpperCase()));
    for (Object[] row : results) {
      String positionName = (String) row[0];
      Long totalEmployees = (Long) row[1];
      Long activeEmployees = (Long) row[2];
      double ratio =
          (activeEmployees == 0) ? 0.0 : (((double) activeEmployees * 100) / totalEmployees);
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
      Long departmentId = (Long) row[0];
      Long totalEmployees = (Long) row[1];
      Long activeEmployees = (Long) row[2];
      double ratio =
          (activeEmployees == 0) ? 0.0 : (((double) activeEmployees * 100) / totalEmployees);
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

  @Transactional(readOnly = true)
  @Override
  public Stream<EmployeeDto> getEmployeeStream() {
    return employeeRepository.findAllEmployeesStream();
  }
}
