package com.team09.sb01hrbank09.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team09.sb01hrbank09.entity.ChangeLog;

@Repository
public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

	ChangeLog save(ChangeLog changeLog);

	Optional<ChangeLog> findById(Long id);

	Page<ChangeLog> findByIdGreaterThan(Long idAfter, Pageable pageable);

	void deleteById(Long id);

	Long countByAtBetween(Instant atAfter, Instant atBefore);

	Page<ChangeLog> findAll(Specification<ChangeLog> spec, Pageable pageable);
}
