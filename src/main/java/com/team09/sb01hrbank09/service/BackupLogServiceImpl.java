package com.team09.sb01hrbank09.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.team09.sb01hrbank09.dto.entityDto.BackupDto;
import com.team09.sb01hrbank09.dto.request.CursorPageRequestBackupDto;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseBackupDto;
import com.team09.sb01hrbank09.entity.Backup;
import com.team09.sb01hrbank09.entity.Enum.BackupStatus;
import com.team09.sb01hrbank09.mapper.BackupMapper;
import com.team09.sb01hrbank09.repository.BackupRepository;
import com.team09.sb01hrbank09.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BackupLogServiceImpl implements BackupLogServiceInterface {

	private final BackupRepository backupRepository;
	private final BackupMapper backupMapper;
	private final EmployeeRepository employeeRepository;
	//private final FileServiceInterface fileService; // Todo: 합칠 때 fileService 와 연동

	@Override
	@Transactional
	public BackupDto createBackup(String worker) {
		Backup latestCompletedBackup = backupRepository.findFirstByStatusOrderByStartedAtDesc(BackupStatus.COMPLETED)
			.orElse(null);

		Instant lastBackupTime = Instant.EPOCH;
		if (latestCompletedBackup != null) {
			lastBackupTime = latestCompletedBackup.getStartedAt();
		}

		/* Todo: EmployeeRepository 메소드 추가
			@Query("SELECT MAX(e.updatedAt) FROM Employee AS e")
			Instant findLatestModifiedAt();
		*/
		Instant lastEmployeeUpdate = employeeRepository.findLatestModifiedAt();

		if (!lastEmployeeUpdate.isAfter(lastBackupTime)) {
			Backup skippedBackup = Backup.createBackup(worker);
			skippedBackup.setStatusSkipped();
			backupRepository.save(skippedBackup);

			return backupMapper.backupToDto(skippedBackup);
		}

		Backup backup = Backup.createBackup(worker);

		/* Todo: 합칠 때 fileService 와 연동
			 try {

				File backupFile = fileService.createEmployeeCsvBackup();
				backup.setStatusCompleted(backupFile);
			 } catch (Exception e) {

				File errorLogFile = fileService.createErrorLogFile(e);
				backup.setStatusFailed(errorLogFile);
			 }
		*/

		backupRepository.save(backup);

		return backupMapper.backupToDto(backup);
	}

	@Override
	@Transactional(readOnly = true)
	public CursorPageResponseBackupDto findBackupList(CursorPageRequestBackupDto request) {
		List<Backup> backups = getBackups(request);

		List<BackupDto> backupDtos = backups.stream()
			.map(backupMapper::backupToDto)
			.toList();

		Long nextIdAfter = null;
		String nextCursor = null;
		boolean hasNext = false;
		if (!backups.isEmpty()) {
			nextIdAfter = backups.get(backups.size() - 1).getId();
			nextCursor = String.valueOf(backups.get(backups.size() - 1).getStartedAt());

			List<Backup> nextBackups = getBackups(CursorPageRequestBackupDto.copy(request, nextIdAfter, nextCursor));
			hasNext = !CollectionUtils.isEmpty(nextBackups);
		}

		Long totalCount = backupRepository.countBackup(
			request.worker(),
			request.status(),
			request.startedAtFrom(),
			request.startedAtTo()
		);

		return new CursorPageResponseBackupDto(
			backupDtos,
			nextCursor,
			nextIdAfter,
			request.size(),
			totalCount,
			hasNext
		);
	}

	private List<Backup> getBackups(CursorPageRequestBackupDto request) {
		if (request.sortDirection().equals("asc")) {
			return backupRepository.findBackupsByCursorOrderByAsc(
				request.worker(),
				request.status(),
				request.startedAtFrom(),
				request.startedAtTo(),
				request.idAfter(),
				request.sortField(),
				request.size()
			);
		}

		return backupRepository.findBackupsByCursorOrderByDesc(
			request.worker(),
			request.status(),
			request.startedAtFrom(),
			request.startedAtTo(),
			request.idAfter(),
			request.sortField(),
			request.size()
		);
	}

	@Override
	@Transactional(readOnly = true)
	public BackupDto findLatestBackup(String status) {
		Backup latestBackup = backupRepository.findFirstByStatusOrderByStartedAtDesc(
				BackupStatus.valueOf(status.toUpperCase()))
			.orElse(null);

		return backupMapper.backupToDto(latestBackup);
	}
}
