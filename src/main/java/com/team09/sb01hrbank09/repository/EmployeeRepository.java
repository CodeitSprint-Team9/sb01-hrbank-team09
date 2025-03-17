package com.team09.sb01hrbank09.repository;

import java.time.Instant;
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

	Long countByStatusAndCreatedAtBetween(EmployeeStatus status, Instant start, Instant end);


	@Query("SELECT FUNCTION('DATE_FORMAT', e.hireDateFrom, :gap) AS date, COUNT(e) AS count " +
		"FROM Employee e " +
		"WHERE e.hireDateFrom BETWEEN :startedAt AND :endedAt " +
		"GROUP BY date " +
		"ORDER BY e.hireDateFrom")
	List<Object[]> findEmployeeTrend(@Param("startedAt") Instant startedAt,
		@Param("endedAt") Instant endedAt,
		@Param("gap") String gap);



}
