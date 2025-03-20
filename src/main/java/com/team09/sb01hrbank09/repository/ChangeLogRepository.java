package com.team09.sb01hrbank09.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.team09.sb01hrbank09.entity.ChangeLog;

@Repository
public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

	ChangeLog save(ChangeLog changeLog);

	Optional<ChangeLog> findById(Long id);

	void deleteById(Long id);

	@Query("""
		    SELECT COUNT(c) FROM ChangeLog c WHERE
		    (:atFrom IS NULL OR c.at >= :atFrom) AND
		    (:atTo IS NULL OR c.at <= :atTo)
		""")
	Long countByAtBetween(Instant atAfter, Instant atBefore);

	// 직원 번호, 메모, IP 주소, 변경 유형, 변경 시간에 따른 리스트 조회 (정렬 ASC)
	@Query("""
		    SELECT c FROM ChangeLog c WHERE
		    (:employeeNumber IS NULL OR c.employeeNumber LIKE %:employeeNumber%) AND
		    (:memo IS NULL OR c.memo LIKE %:memo%) AND
		    (:ipAddress IS NULL OR c.ipAddress LIKE %:ipAddress%) AND
		    (:type IS NULL OR c.type = :type) AND
		    (:atFrom IS NULL OR c.at >= :atFrom) AND
		    (:atTo IS NULL OR c.at <= :atTo) AND
		    (:idAfter IS NULL OR c.id > :idAfter)
		    ORDER BY c.at ASC
		""")
	Page<ChangeLog> findChangeLogsAsc(String employeeNumber, String memo, String ipAddress, String type, Instant atFrom,
		Instant atTo, Long idAfter, Pageable pageable);

	// 직원 번호, 메모, IP 주소, 변경 유형, 변경 시간에 따른 리스트 조회 (정렬 DESC)
	@Query("""
		    SELECT c FROM ChangeLog c WHERE
		    (:employeeNumber IS NULL OR c.employeeNumber LIKE %:employeeNumber%) AND
		    (:memo IS NULL OR c.memo LIKE %:memo%) AND
		    (:ipAddress IS NULL OR c.ipAddress LIKE %:ipAddress%) AND
		    (:type IS NULL OR c.type = :type) AND
		    (:atFrom IS NULL OR c.at >= :atFrom) AND
		    (:atTo IS NULL OR c.at <= :atTo) AND
		    (:idAfter IS NULL OR c.id > :idAfter)
		    ORDER BY c.at DESC
		""")
	Page<ChangeLog> findChangeLogsDesc(String employeeNumber, String memo, String ipAddress, String type,
		Instant atFrom, Instant atTo, Long idAfter, Pageable pageable);

	// 전체 ChangeLog 수 카운트
	@Query("""
		   SELECT COUNT(c) FROM ChangeLog c WHERE
		   (:employeeNumber IS NULL OR c.employeeNumber LIKE %:employeeNumber%) AND
		   (:memo IS NULL OR c.memo LIKE %:memo%) AND
		   (:ipAddress IS NULL OR c.ipAddress LIKE %:ipAddress%) AND
		   (:type IS NULL OR c.type = :type) AND
		   (:atFrom IS NULL OR c.at >= :atFrom) AND
		   (:atTo IS NULL OR c.at <= :atTo)
		""")
	Long countChangeLogs(String employeeNumber, String memo, String ipAddress, String type,
		Instant atFrom, Instant atTo);
}
