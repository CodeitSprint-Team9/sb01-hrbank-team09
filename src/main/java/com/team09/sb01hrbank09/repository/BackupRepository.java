package com.team09.sb01hrbank09.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.team09.sb01hrbank09.entity.Backup;
import com.team09.sb01hrbank09.entity.Enum.BackupStatus;

@Repository
public interface BackupRepository extends JpaRepository<Backup, Long> {

	Optional<Backup> findFirstByStatusOrderByStartedAtDesc(BackupStatus status);

	void deleteById(Long id);

	@Query("""
		SELECT b FROM Backup b
		WHERE (:worker IS NULL OR b.worker LIKE CONCAT('%', ?1, '%'))
				AND (:status IS NULL OR b.status = ?2)
				AND (:startedAtFrom IS NULL OR b.startedAt >= ?3)
				AND (:startedAtTo IS NULL OR b.startedAt <= ?4)
				AND (:idAfter IS NULL OR b.id < ?5)
		""")
	Page<Backup> findBackupsByCursorOrderByIdDesc(
		@Param("worker") String worker,
		@Param("status") BackupStatus status,
		@Param("startedAtFrom") Instant startedAtFrom,
		@Param("startedAtTo") Instant startedAtTo,
		@Param("idAfter") Long idAfter,
		Pageable pageable
	);

	@Query("""
		SELECT b FROM Backup b
		WHERE (:worker IS NULL OR b.worker LIKE CONCAT('%', ?1, '%'))
				AND (:status IS NULL OR b.status = ?2)
				AND (:startedAtFrom IS NULL OR b.startedAt >= ?3)
				AND (:startedAtTo IS NULL OR b.startedAt <= ?4)
				AND (:idAfter IS NULL OR b.id > ?5)
		""")
	Page<Backup> findBackupsByCursorOrderByIdAsc(
		@Param("worker") String worker,
		@Param("status") BackupStatus status,
		@Param("startedAtFrom") Instant startedAtFrom,
		@Param("startedAtTo") Instant startedAtTo,
		@Param("idAfter") Long idAfter,
		Pageable pageable
	);

	@Query("""
		SELECT COUNT(b.id) FROM Backup b
		WHERE (:worker IS NULL OR b.worker LIKE CONCAT('%', ?1, '%'))
				AND (:status IS NULL OR b.status = ?2)
				AND (:startedAtFrom IS NULL OR b.startedAt >= ?3)
				AND (:startedAtTo IS NULL OR b.startedAt <= ?4)
		""")
	Long countBackup(
		@Param("worker") String worker,
		@Param("status") BackupStatus status,
		@Param("startedAtFrom") Instant startedAtFrom,
		@Param("startedAtTo") Instant startedAtTo
	);
}
