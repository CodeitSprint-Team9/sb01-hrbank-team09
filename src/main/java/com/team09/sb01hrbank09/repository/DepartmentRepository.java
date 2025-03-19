package com.team09.sb01hrbank09.repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team09.sb01hrbank09.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

	Department save(Department department);

	Optional<Department> findById(Long id);
	Optional<Department> findByName(String name);


	List<Department> findAll();

	void deleteById(Long id);

	// List<Department> findByIdGreaterThanAndNameContainingOrDescriptionContaining(
	// 		Long idAfter, String nameSearchTerm, String descriptionSearchTerm, Pageable pageable
	// );
	//
	// List<Department> findByNameContainingOrDescriptionContaining(
	// 		String nameSearchTerm, String descriptionSearchTerm, Pageable pageable
	// );
	//
	// Long countByIdGreaterThanAndNameContainingOrDescriptionContaining(
	// 		Long idAfter, String nameSearchTerm, String descriptionSearchTerm
	// );
	//
	// Long countByNameContainingOrDescriptionContaining(
	// 		String nameSearchTerm, String descriptionSearchTerm
	// );
	//
	// Optional<Department> findFirstByIdGreaterThanAndNameContainingOrDescriptionContaining(
	// 		Long idAfter, String nameSearchTerm, String descriptionSearchTerm, Pageable pageable
	// );
	//
	// Optional<Department> findFirstByNameContainingOrDescriptionContaining(
	// 		String nameSearchTerm, String descriptionSearchTerm, Pageable pageable
	// );

}
