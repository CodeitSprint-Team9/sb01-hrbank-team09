package com.team09.sb01hrbank09.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team09.sb01hrbank09.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

	Department save(Department department);

	Optional<Department> findById(Long id);

	List<Department> findAll();

	void deleteById(Long id);

	boolean existsByName(String name);
}
