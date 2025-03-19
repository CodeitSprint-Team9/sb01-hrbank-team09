package com.team09.sb01hrbank09.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.team09.sb01hrbank09.entity.Backup;
import com.team09.sb01hrbank09.entity.Enum.BackupStatus;

@Repository
public interface BackupRepository extends JpaRepository<Backup, Long> {

	Backup save(Backup backup);

	Optional<Backup> findById(Long id);

	List<Backup> findAll();

	Optional<Backup> findFirstByStatusOrderByStartedAtDesc(BackupStatus status);

	void deleteById(Long id);

	@Query("""
		SELECT b FROM Backup b
		WHERE :worker IS NULL OR b.worker LIKE %:worker%
		AND :status IS NULL OR b.status = :status
		AND :startedAtFrom IS NULL OR b.startedAt >= :startedAtFrom
		AND :startedAtTo IS NULL OR b.startedAt <= :startedAtTo
		AND :idAfter IS NULL OR :idAfter > b.id
		ORDER BY :sortField DESC
		LIMIT :size
		""")
	List<Backup> findBackupsByCursorOrderByDesc(
		String worker,
		String status,
		Instant startedAtFrom,
		Instant startedAtTo,
		Long idAfter,
		String sortField,
		Integer size
	);

	@Query("""
		SELECT b FROM Backup b
		WHERE :worker IS NULL OR b.worker LIKE %:worker%
		AND :status IS NULL OR b.status = :status
		AND :startedAtFrom IS NULL OR b.startedAt >= :startedAtFrom
		AND :startedAtTo IS NULL OR b.startedAt <= :startedAtTo
		AND :idAfter IS NULL OR :idAfter < b.id
		ORDER BY :sortField ASC
		LIMIT :size
		""")
	List<Backup> findBackupsByCursorOrderByAsc(
		String worker,
		String status,
		Instant startedAtFrom,
		Instant startedAtTo,
		Long idAfter,
		String sortField,
		Integer size
	);

	@Query("""
		SELECT COUNT(b.id) FROM Backup b
		WHERE :worker IS NULL OR b.worker LIKE %:worker%
		AND :status IS NULL OR b.status = :status
		AND :startedAtFrom IS NULL OR b.startedAt >= :startedAtFrom
		AND :startedAtTo IS NULL OR b.startedAt <= :startedAtTo
		""")
	Long countBackup(
		String worker,
		String status,
		Instant startedAtFrom,
		Instant startedAtTo
	);
}
