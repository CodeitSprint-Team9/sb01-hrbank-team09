package com.team09.sb01hrbank09.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
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

	@Query(value = """
		SELECT d FROM Department d
		WHERE (d.name LIKE %:nameOrDescription% OR d.description LIKE %:nameOrDescription%)
				AND (:idAfter IS NULL OR
						(d.name > :cursorName OR 
								d.name = :cursorName AND d.id > :idAfter))
		""")
	List<Department> findDepartmentByNameOrderByIdAsc(
		String nameOrDescription,
		Long idAfter,
		String cursorName,
		Pageable pageable
	);
	//findDepartment --> idAfter만 사용하는 기존 jpql. nameOrDescription 널체크부분만 제거

	@Query(value = """
		SELECT d FROM Department d
		WHERE (d.name LIKE %:nameOrDescription% OR d.description LIKE %:nameOrDescription%)
				AND (:idAfter IS NULL OR
						(d.name < :cursorName OR 
								d.name = :cursorName AND d.id < :idAfter))
		""")
	List<Department> findDepartmentByNameOrderByIdDesc(
		String nameOrDescription,
		Long idAfter,
		String cursorName,
		Pageable pageable
	);

	@Query(value = """
		SELECT d FROM Department d
		WHERE (d.name LIKE %:nameOrDescription% OR d.description LIKE %:nameOrDescription%)
				AND (:idAfter IS NULL OR
						(d.establishedDate > :cursorEstablishedDate OR 
								d.establishedDate = :cursorEstablishedDate AND d.id > :idAfter))
		""")
	List<Department> findDepartmentByEstablishedDateOrderByIdAsc(
		String nameOrDescription,
		Long idAfter,
		LocalDate cursorEstablishedDate,
		Pageable pageable
	);

	@Query(value = """
		SELECT d FROM Department d
		WHERE (d.name LIKE %:nameOrDescription% OR d.description LIKE %:nameOrDescription%)
				AND (:idAfter IS NULL OR
						(d.establishedDate < :cursorEstablishedDate OR 
								d.establishedDate = :cursorEstablishedDate AND d.id < :idAfter))
		""")
	List<Department> findDepartmentByEstablishedDateOrderByIdDesc(
		String nameOrDescription,
		Long idAfter,
		LocalDate cursorEstablishedDate,
		Pageable pageable
	);

	@Query(value = """
		SELECT COUNT(d.id) FROM Department d
		WHERE (d.name LIKE %:nameOrDescription% OR d.description LIKE %:nameOrDescription%)
		""")
	Long getCount(String nameOrDescription);

}
