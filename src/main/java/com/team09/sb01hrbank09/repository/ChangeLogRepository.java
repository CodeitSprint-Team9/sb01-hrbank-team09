package com.team09.sb01hrbank09.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team09.sb01hrbank09.entity.ChangeLog;

@Repository
public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

	ChangeLog save(ChangeLog changeLog);

	Optional<ChangeLog> findById(Long id);

	List<ChangeLog> findAll();

	void deleteById(Long id);
}
