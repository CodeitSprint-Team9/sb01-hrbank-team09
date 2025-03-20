package com.team09.sb01hrbank09.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.team09.sb01hrbank09.entity.ChangeLog;
import com.team09.sb01hrbank09.entity.Enum.ChangeLogType;

@Repository
public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

	ChangeLog save(ChangeLog changeLog);

	@Query("SELECT c FROM ChangeLog c WHERE c.id = :id")
	Optional<ChangeLog> findById(Long id);

	void deleteById(Long id);

	@Query("SELECT c FROM ChangeLog c WHERE " +
		"(:employeeNumber IS NULL OR c.employeeNumber LIKE %?1%) AND " +
		"(:memo IS NULL OR c.memo LIKE %?2%) AND " +
		"(:ipAddress IS NULL OR c.ipAddress LIKE %?3%) AND " +
		"(:type IS NULL OR c.type = ?4) AND " +
		"(:atFrom IS NULL OR c.at >= ?5) AND " +
		"(:atTo IS NULL OR c.at <= ?6) AND " +
		"(:idAfter IS NULL OR c.id > ?7) " +
		"ORDER BY c.at ASC")
	Page<ChangeLog> findChangeLogsAsc(String employeeNumber, String memo, String ipAddress, ChangeLogType type,
		Instant atFrom,
		Instant atTo, Long idAfter, Pageable pageable);

	@Query("SELECT c FROM ChangeLog c WHERE " +
		"(:employeeNumber IS NULL OR c.employeeNumber LIKE %?1%) AND " +
		"(:memo IS NULL OR c.memo LIKE %?2%) AND " +
		"(:ipAddress IS NULL OR c.ipAddress LIKE %?3%) AND " +
		"(:type IS NULL OR c.type = ?4) AND " +
		"(:atFrom IS NULL OR c.at >= ?5) AND " +
		"(:atTo IS NULL OR c.at <= ?6) AND " +
		"(:idAfter IS NULL OR c.id > ?7) " +
		"ORDER BY c.at DESC")
	Page<ChangeLog> findChangeLogsDesc(String employeeNumber, String memo, String ipAddress, ChangeLogType type,
		Instant atFrom, Instant atTo, Long idAfter, Pageable pageable);

	@Query("SELECT c FROM ChangeLog c WHERE " +
		"(:employeeNumber IS NULL OR c.employeeNumber LIKE %?1%) AND " +
		"(:memo IS NULL OR c.memo LIKE %?2%) AND " +
		"(:ipAddress IS NULL OR c.ipAddress LIKE %?3%) AND " +
		"(:type IS NULL OR c.type = ?4) AND " +
		"(:atFrom IS NULL OR c.at >= ?5) AND " +
		"(:atTo IS NULL OR c.at <= ?6) " +
		"ORDER BY c.at DESC")
	Page<ChangeLog> findChangeLogsWithoutIdAfterDesc(String employeeNumber, String memo, String ipAddress,
		ChangeLogType type, Instant atFrom, Instant atTo,
		Pageable pageable);

	@Query("SELECT c FROM ChangeLog c WHERE " +
		"(:employeeNumber IS NULL OR c.employeeNumber LIKE %?1%) AND " +
		"(:memo IS NULL OR c.memo LIKE %?2%) AND " +
		"(:ipAddress IS NULL OR c.ipAddress LIKE %?3%) AND " +
		"(:type IS NULL OR c.type = ?4) AND " +
		"(:atFrom IS NULL OR c.at >= ?5) AND " +
		"(:atTo IS NULL OR c.at <= ?6) " +
		"ORDER BY c.at DESC")
	Page<ChangeLog> findChangeLogsWithoutIdAfterAsc(String employeeNumber, String memo, String ipAddress,
		ChangeLogType type, Instant atFrom, Instant atTo,
		Pageable pageable);

	@Query("SELECT c FROM ChangeLog c WHERE " +
		"(:employeeNumber IS NULL OR c.employeeNumber LIKE %?1%) AND " +
		"(:memo IS NULL OR c.memo LIKE %?2%) AND " +
		"(:ipAddress IS NULL OR c.ipAddress LIKE %?3%) AND " +
		"(:type IS NULL OR c.type = ?4) AND " +
		"(:atFrom IS NULL OR c.at >= ?5) AND " +
		"(:atTo IS NULL OR c.at <= ?6)")
	Long countChangeLogs(
		@Param("employeeNumber") String employeeNumber,
		@Param("memo") String memo,
		@Param("ipAddress") String ipAddress,
		@Param("type") ChangeLogType type,
		@Param("atFrom") Instant atFrom,
		@Param("atTo") Instant atTo
	);

	@Query("SELECT COUNT(c) FROM ChangeLog c WHERE " +
		"(c.at >= ?1) AND (c.at <= ?2)")
	Long countByAtBetween(Instant atFrom, Instant atTo);

}

