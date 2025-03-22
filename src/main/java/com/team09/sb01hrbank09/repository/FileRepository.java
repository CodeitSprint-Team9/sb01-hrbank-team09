package com.team09.sb01hrbank09.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.team09.sb01hrbank09.dto.entityDto.EmployeeDto;
import com.team09.sb01hrbank09.entity.File;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

	File save(File file);

	Optional<File> findById(Long id);

	List<File> findAll();

	void deleteById(Long id);


}
