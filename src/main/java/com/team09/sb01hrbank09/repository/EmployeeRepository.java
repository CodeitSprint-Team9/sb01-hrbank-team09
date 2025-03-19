package com.team09.sb01hrbank09.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.team09.sb01hrbank09.entity.Employee;
import com.team09.sb01hrbank09.entity.Enum.EmployeeStatus;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	Employee save(Employee employee);

	Optional<Employee> findById(Long id);

	List<Employee> findAll();

	void deleteById(Long id);

	Long countByStatusAndCreatedAtBetween(EmployeeStatus status, Instant start, Instant end);

	@Query("SELECT FUNCTION('DATE_FORMAT', e.hireDateFrom, :gap) AS date, COUNT(e) AS count " +
		"FROM Employee e " +
		"WHERE e.hireDateFrom BETWEEN :startedAt AND :endedAt " +
		"GROUP BY date " +
		"ORDER BY e.hireDateFrom")
	List<Object[]> findEmployeeTrend(@Param("startedAt") Instant startedAt,
		@Param("endedAt") Instant endedAt,
		@Param("gap") String gap);

	@Query("SELECT e.position, COUNT(e), " +
		"SUM(CASE WHEN e.status = :status THEN 1 ELSE 0 END) " +
		"FROM Employee e " +
		"GROUP BY e.position")
	List<Object[]> findDistributatinPosition(@Param("status") EmployeeStatus status);

	@Query("SELECT e.department.id, COUNT(e), " +
		"SUM(CASE WHEN e.status = :status THEN 1 ELSE 0 END) " +
		"FROM Employee e " +
		"GROUP BY e.department.id")
	List<Object[]> findDistributatinDepartment(@Param("status") EmployeeStatus status);

	Optional<Employee> findFirstByIdGreaterThanAndNameContainingOrDescriptionContaining(
		Long idAfter, String name, String description, Pageable pageable
	);

	Optional<Employee> findFirstByNameContainingOrDescriptionContaining(
		String name, String description, Pageable pageable
	);

	List<Employee> findByIdGreaterThanAndFilters(
		Long idAfter, String name, String employeeNumber, String departmentName, String position,
		Instant hireDateFrom, Instant hireDateTo, EmployeeStatus status, Pageable pageable
	);

	long countByIdGreaterThanAndFilters(
		Long idAfter, String name, String employeeNumber, String departmentName,
		String position, Instant hireDateFrom, Instant hireDateTo, EmployeeStatus status
	);

	List<Employee> findByFilters(
		String name, String employeeNumber, String departmentName, String position,
		Instant hireDateFrom, Instant hireDateTo, EmployeeStatus status, Pageable pageable
	);

	long countByFilters(
		String name, String employeeNumber, String departmentName, String position,
		Instant hireDateFrom, Instant hireDateTo, EmployeeStatus status
	);


	Employee findByEmployeeNumber(String employeeNumber);
}
