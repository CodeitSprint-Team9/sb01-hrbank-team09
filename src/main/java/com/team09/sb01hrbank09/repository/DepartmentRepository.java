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

	@Query("""
		SELECT d FROM Department d WHERE
		  (:nameOrDescription IS NULL OR d.name LIKE %:nameOrDescription% OR d.description LIKE %:nameOrDescription%) AND
		  (:idAfter IS NULL OR d.id > :idAfter)
		     		""")
	Page<Department> findDepartment(String nameOrDescription, Long idAfter, Pageable pageable);

}
