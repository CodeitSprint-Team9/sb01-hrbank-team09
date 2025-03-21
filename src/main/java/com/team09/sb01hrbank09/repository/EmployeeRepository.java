package com.team09.sb01hrbank09.repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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

	Long countByStatusAndHireDateBetween(EmployeeStatus status, LocalDate start, LocalDate end);



	@Query("""
    SELECT 
        FUNCTION('DATE_TRUNC', :gap, e.hireDate) AS periodDate, 
        COUNT(e.id) AS cnt 
    FROM Employee e 
    WHERE e.hireDate BETWEEN :startedAt AND :endedAt 
    GROUP BY periodDate 
    ORDER BY MIN(e.hireDate)
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

	@Query(value = """
		SELECT e FROM Employee e
		WHERE (:nameOrEmail IS NULL OR e.name LIKE %:nameOrEmail% OR e.email LIKE %:nameOrEmail%)
		      AND (:employeeNumber IS NULL OR e.employeeNumber = :employeeNumber)
		      AND (:departmentName IS NULL OR e.department.name = :departmentName)
		      AND (:position IS NULL OR e.position = :position)
		      AND ( e.hireDate >= :hireDateFrom)
			  AND ( e.hireDate <= :hireDateTo)
		      AND (:status IS NULL OR e.status = :status)
		      AND ((:cursorHireDate IS NULL AND :cursorId IS NULL) OR
		           (e.hireDate < :cursorHireDate OR (e.hireDate = :cursorHireDate AND e.id < :cursorId)))
		ORDER BY e.hireDate DESC, e.id DESC
		""")
	Page<Employee> findEmployeesByCursorOrderByIdDesc(
		@Param("nameOrEmail") String nameOrEmail,
		@Param("employeeNumber") String employeeNumber,
		@Param("departmentName") String departmentName,
		@Param("position") String position,
		@Param("hireDateFrom") LocalDate hireDateFrom,
		@Param("hireDateTo") LocalDate hireDateTo,
		@Param("status") EmployeeStatus status,
		@Param("cursorHireDate") LocalDate cursorHireDate,
		@Param("cursorId") Long cursorId,
		Pageable pageable
	);

	@Query(value = """
   SELECT e FROM Employee e
   WHERE (:nameOrEmail IS NULL OR e.name LIKE %:nameOrEmail% OR e.email LIKE %:nameOrEmail%)
         AND (:employeeNumber IS NULL OR e.employeeNumber = :employeeNumber)
         AND (:departmentName IS NULL OR e.department.name = :departmentName)
         AND (:position IS NULL OR e.position = :position)
         AND ( e.hireDate >= :hireDateFrom)
         AND ( e.hireDate <= :hireDateTo)
         AND (:status IS NULL OR e.status = :status)
         AND ((:cursorHireDate IS NULL AND :cursorId IS NULL) OR
              (e.hireDate > :cursorHireDate OR (e.hireDate = :cursorHireDate AND e.id > :cursorId)))
   ORDER BY e.hireDate ASC, e.id ASC
   """)
	Page<Employee> findEmployeesByCursorOrderByIdAsc(
		@Param("nameOrEmail") String nameOrEmail,
		@Param("employeeNumber") String employeeNumber,
		@Param("departmentName") String departmentName,
		@Param("position") String position,
		@Param("hireDateFrom") LocalDate hireDateFrom,
		@Param("hireDateTo") LocalDate hireDateTo,
		@Param("status") EmployeeStatus status,
		@Param("cursorHireDate") LocalDate cursorHireDate,
		@Param("cursorId") Long cursorId,
		Pageable pageable
	);

	@Query("SELECT e FROM Employee e " +
		"LEFT JOIN e.department d " +
		"WHERE (:idAfter IS NULL OR e.id > :idAfter) AND " +
		"(:cursor IS NULL OR e.id > :cursor) AND " +
		"(:nameOrEmail IS NULL OR e.name LIKE CONCAT('%', CAST(:nameOrEmail AS string), '%') OR " +
		" e.email LIKE CONCAT('%', CAST(:nameOrEmail AS string), '%')) AND " +
		"(:employeeNumber IS NULL OR e.employeeNumber LIKE CONCAT('%', CAST(:employeeNumber AS string), '%')) AND " +
		"(:departmentName IS NULL OR d.name LIKE CONCAT('%', CAST(:departmentName AS string), '%')) AND " +
		"(:position IS NULL OR e.position LIKE CONCAT('%', CAST(:position AS string), '%')) AND " +
		"(:hireDateFrom IS NULL OR e.hireDate >= :hireDateFrom) AND " +
		"(:hireDateTo IS NULL OR e.hireDate <= :hireDateTo) AND " +
		"(:status IS NULL OR CAST(e.status AS string) = :status) ")
	List<Employee> findEmployeesWithAdvancedFilters(
		@Param("nameOrEmail") String nameOrEmail,
		@Param("employeeNumber") String employeeNumber,
		@Param("departmentName") String departmentName,
		@Param("position") String position,
		@Param("hireDateFrom") LocalDate hireDateFrom,
		@Param("hireDateTo") LocalDate hireDateTo,
		@Param("status") String status,
		@Param("idAfter") Long idAfter,
		@Param("cursor") Long cursor,
		Pageable pageable);



}
