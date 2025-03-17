package com.team09.sb01hrbank09.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;
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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeServiceInterface {

	private final EmployeeRepository employeeRepository;
	private final DepartmentServiceInterface departmentServiceInterface;
	private final FileServiceInterface fileServiceInterface;
	private final ChangeLogServiceInterface changeLogServiceInterface;
	private final EmployeeMapper employeeMapper;

	@Override
	@Transactional
	public EmployeeDto creatEmployee(EmployeeCreateRequest employeeCreateRequest, MultipartFile profileImg) {
		Department usingDepartment = departmentServiceInterface.findDepartmentEntityById(
			employeeCreateRequest.departmentId());
		if (usingDepartment == null) {
			throw new NoSuchElementException("Department 아이디가 존재하지 않음");
		}

		File file = null;
		if (profileImg != null) {
			file = fileServiceInterface.createFile(profileImg);
		}

		String uniquePart = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 14);
		String employeeNumber = "EMP-" + uniquePart;

		Employee employee = Employee.createEmployee(employeeCreateRequest.name(), employeeCreateRequest.email(),
			employeeNumber, employeeCreateRequest.position(),
			employeeCreateRequest.hireDate(), EmployeeStatus.ACTIVE, file, usingDepartment);

		//만들어지면 넣기
		//changeLogServiceInterface.createChangeLog(ChnageLogDto request);

		employeeRepository.save(employee);
		return employeeMapper.employeeToDto(employee);
	}

	@Override
	@Transactional
	public EmployeeDto findEmployeeById(Long Id) {
		Employee employee = employeeRepository.findById(Id)
			.orElseThrow(() -> new NoSuchElementException("Message with id " + Id + " not found"));
		return employeeMapper.employeeToDto(employee);
	}

	@Override
	@Transactional
	public CursorPageResponseEmployeeDto findEmployeeList(String nameOrEmail, String employeeNumber,
		String departmentName, String position, String hireDateFrom, String hireDateTo, String status, Long idAfter,
		String cursor, int size, String sortField, String sortDirection) {
		return null;
	}

	@Override
	@Transactional
	public boolean deleteEmployee(Long id) {
		if (employeeRepository.existsById(id)) {
			Employee employee = employeeRepository.findById(id).get();
			fileServiceInterface.deleteFile(employee.getFile());
			employeeRepository.deleteById(id);
			return true;
		}
		return false;
	}

	@Override
	@Transactional
	public EmployeeDto updateEmployee(Long id, EmployeeUpdateRequest employeeUpdateRequest, MultipartFile profileImg) {
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
			file = fileServiceInterface.createFile(profileImg);
			employee.updateFile(file);
		}

		//만들어지면 넣기
		//changeLogServiceInterface.createChangeLog(ChnageLogDto request,EmployeeDto old, EmployeeDto new);

		return employeeMapper.employeeToDto(employee);
	}

	@Override
	@Transactional
	public List<EmployeeTrendDto> getEmployeeTrend(Instant startedAt, Instant endedAt, String gap) {

		List<Object[]> results = employeeRepository.findEmployeeTrend(startedAt, endedAt, gap);

		List<EmployeeTrendDto> trends = new ArrayList<>();
		long previousCount = 0;

		for (Object[] row : results) {
			Instant date = ((Timestamp) row[0]).toInstant();
			long count = ((Number) row[1]).longValue();

			long change = count - previousCount;
			double changeRate;
			if(previousCount==0)
				changeRate=0.0;
			else{
				changeRate=(change * 100.0 / previousCount);
			}

			trends.add(new EmployeeTrendDto(date, count, change, changeRate));
			previousCount = count;
		}

		return trends;
	}

	@Override
	@Transactional
	public EmployeeDistributionDto getEmployeeDistributaion(String groupBy, String status) {
		return null;
	}

	@Override
	@Transactional
	public Long countEmployee(String status, Instant startedAt, Instant endedAt) {
		EmployeeStatus findStatus = EmployeeStatus.valueOf(status.toUpperCase());
		return employeeRepository.countByStatusAndCreatedAtBetween(findStatus, startedAt, endedAt);
	}

}
