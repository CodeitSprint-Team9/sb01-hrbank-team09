package com.team09.sb01hrbank09.repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
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

	Long countByStatusAndHireDateFromBetween(EmployeeStatus status, LocalDate start, LocalDate end);



	@Query("""
    SELECT 
        FUNCTION('DATE_TRUNC', :gap, e.hireDateFrom) AS periodDate, 
        COUNT(e.id) AS cnt 
    FROM Employee e 
    WHERE e.hireDateFrom BETWEEN :startedAt AND :endedAt 
    GROUP BY periodDate 
    ORDER BY MIN(e.hireDateFrom)
""")
	List<Object[]> findEmployeeTrend(@Param("startedAt") LocalDate startedAt,
		@Param("endedAt") LocalDate endedAt,
		@Param("gap") String gap);

	@Query("SELECT e.position, COUNT(e), " +
		"SUM(CASE WHEN e.status = :status THEN 1 ELSE 0 END) " +
		"FROM Employee e " +
		"GROUP BY e.position")
	List<Object[]> findDistributionPosition(@Param("status") EmployeeStatus status);

	@Query("SELECT e.department.id, COUNT(e), " +
		"SUM(CASE WHEN e.status = :status THEN 1 ELSE 0 END) " +
		"FROM Employee e " +
		"GROUP BY e.department.id")
	List<Object[]> findDistributionDepartment(@Param("status") EmployeeStatus status);

	@Query("SELECT e FROM Employee e " +
		"WHERE (:nameOrEmail IS NULL OR e.name LIKE %?1% OR e.email LIKE %?1%) " +
		"AND (:employeeNumber IS NULL OR e.employeeNumber = ?2) " +
		"AND (:departmentName IS NULL OR e.department.name LIKE %?3%) " +
		"AND (:position IS NULL OR e.position LIKE %?4%) " +
		"AND (:hireDateFrom IS NULL OR e.hireDateFrom >= ?5) " +
		"AND (:hireDateTo IS NULL OR e.hireDateFrom <= ?6) " +
		"AND (:status IS NULL OR e.status = ?7) " +
		"AND (:idAfter IS NULL OR e.id > ?8)")
	Page<Employee> findEmployeesWithFilters(
		@Param("nameOrEmail") String nameOrEmail,
		@Param("employeeNumber") String employeeNumber,
		@Param("departmentName") String departmentName,
		@Param("position") String position,
		@Param("hireDateFrom") LocalDateTime hireDateFrom,
		@Param("hireDateTo") LocalDateTime hireDateTo,
		@Param("status") String status,
		@Param("idAfter") Long idAfter,
		Pageable pageable);
}
