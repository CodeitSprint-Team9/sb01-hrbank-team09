package com.team09.sb01hrbank09.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import com.team09.sb01hrbank09.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

	Department save(Department department);

	Optional<Department> findById(Long id);

	Optional<Department> findByName(String name);

	List<Department> findAll();

	void deleteById(Long id);

	// @Query("SELECT d FROM Department d " +
	// 	"WHERE (:idAfter IS NULL OR d.id > :idAfter) AND " +
	// 	"(:cursor IS NULL OR d.id > :cursor) AND " +
	// 	"(:nameOrDescription IS NULL OR d.name LIKE CONCAT('%', CAST(:nameOrDescription AS string), '%') OR " +
	// 	" d.description LIKE CONCAT('%', CAST(:nameOrDescription AS string), '%')")
	// List<Department> findDepartmentsWithFilters(String nameOrDescription, Long idAfter, Long cursorLong, Pageable pageable);




	@Query("SELECT d FROM Department d " +
		"WHERE (:idAfter IS NULL OR d.id > :idAfter) AND " +
		"(:cursorLong IS NULL OR d.id > :cursorLong) AND " +
		"(:nameOrDescription IS NULL OR d.name LIKE %:nameOrDescription% OR " +
		" d.description LIKE %:nameOrDescription%)")
	List<Department> findDepartmentsWithFilters(String nameOrDescription, Long idAfter, Long cursorLong, Pageable pageable);

	@Query("""
		SELECT d FROM Department d WHERE
		  (d.name LIKE %:nameOrDescription% OR d.description LIKE %:nameOrDescription%) AND
		  (:idAfter IS NULL OR d.id > :idAfter)
		     		""")
	Page<Department> findDepartmentOrderByIdAsc(String nameOrDescription, Long idAfter, Pageable pageable);
	//findDepartment --> idAfter만 사용하는 기존 jpql. nameOrDescription 널체크부분만 제거

	@Query("""
		SELECT d FROM Department d WHERE
		  (d.name LIKE %:nameOrDescription% OR d.description LIKE %:nameOrDescription%) AND
		  (:idAfter IS NULL OR d.id < :idAfter)
		     		""")
	Page<Department> findDepartmentOrderByIdDesc(String nameOrDescription, Long idAfter, Pageable pageable);

	@Query("SELECT COUNT(d.id) FROM Department d")
	Long getCount();

}
