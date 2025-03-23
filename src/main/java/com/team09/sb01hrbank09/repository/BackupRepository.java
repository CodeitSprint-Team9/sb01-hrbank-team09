package com.team09.sb01hrbank09.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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

	@Query(value = """
		SELECT b FROM Backup b
		WHERE (b.worker LIKE %:worker%)
				AND (:status IS NULL OR b.status = :status)
				AND (b.startedAt >= :startedAtFrom)
				AND (b.startedAt <= :startedAtTo)
				AND (:idAfter IS NULL OR
						(b.startedAt < :cursorStartedAt OR
								b.startedAt = :cursorStartedAt AND b.id < :idAfter))
		""")
	List<Backup> findBackupsByStartedAtOrderByIdDesc(
		@Param("worker") String worker,
		@Param("status") BackupStatus status,
		@Param("startedAtFrom") Instant startedAtFrom,
		@Param("startedAtTo") Instant startedAtTo,
		@Param("idAfter") Long idAfter,
		@Param("cursorStartedAt") Instant cursorStartedAt,
		Pageable pageable
	);

	@Query(value = """
		SELECT b FROM Backup b
		WHERE (b.worker LIKE %:worker%)
				AND (:status IS NULL OR b.status = :status)
				AND (b.startedAt >= :startedAtFrom)
				AND (b.startedAt <= :startedAtTo)
				AND (:idAfter IS NULL OR
						(b.startedAt > :cursorStartedAt OR
								b.startedAt = :cursorStartedAt AND b.id > :idAfter))
		""")
	List<Backup> findBackupsByStartedAtOrderByIdAsc(
		@Param("worker") String worker,
		@Param("status") BackupStatus status,
		@Param("startedAtFrom") Instant startedAtFrom,
		@Param("startedAtTo") Instant startedAtTo,
		@Param("idAfter") Long idAfter,
		@Param("cursorStartedAt") Instant cursorStartedAt,
		Pageable pageable
	);

	@Query(value = """
		SELECT b FROM Backup b
		WHERE (b.worker LIKE %:worker%)
				AND (:status IS NULL OR b.status = :status)
				AND (b.startedAt >= :startedAtFrom)
				AND (b.startedAt <= :startedAtTo)
				AND (:idAfter IS NULL OR
						(b.endedAt < :cursorEndedAt OR
								b.endedAt = :cursorEndedAt AND b.id < :idAfter))
		""")
	List<Backup> findBackupsByEndedAtOrderByIdDesc(
		@Param("worker") String worker,
		@Param("status") BackupStatus status,
		@Param("startedAtFrom") Instant startedAtFrom,
		@Param("startedAtTo") Instant startedAtTo,
		@Param("idAfter") Long idAfter,
		@Param("cursorEndedAt") Instant cursorEndedAt,
		Pageable pageable
	);

	@Query(value = """
		SELECT b FROM Backup b
		WHERE (b.worker LIKE %:worker%)
				AND (:status IS NULL OR b.status = :status)
				AND (b.startedAt >= :startedAtFrom)
				AND (b.startedAt <= :startedAtTo)
				AND (:idAfter IS NULL OR
						(b.endedAt > :cursorEndedAt OR
								b.endedAt = :cursorEndedAt AND b.id > :idAfter))
		""")
	List<Backup> findBackupsByEndedAtOrderByIdAsc(
		@Param("worker") String worker,
		@Param("status") BackupStatus status,
		@Param("startedAtFrom") Instant startedAtFrom,
		@Param("startedAtTo") Instant startedAtTo,
		@Param("idAfter") Long idAfter,
		@Param("cursorEndedAt") Instant cursorEndedAt,
		Pageable pageable
	);

	@Query(value = """
		SELECT COUNT(b) FROM Backup b
		WHERE (b.worker LIKE %:worker%)
				AND (:status IS NULL OR b.status = :status)
				AND (b.startedAt >= :startedAtFrom)
				AND (b.startedAt <= :startedAtTo)
		""")
	Long getCount(
		String worker,
		BackupStatus status,
		Instant startedAtFrom,
		Instant startedAtTo
	);

	boolean existsByStatus(BackupStatus status);
}
