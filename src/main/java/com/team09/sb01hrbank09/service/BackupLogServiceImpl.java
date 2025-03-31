package com.team09.sb01hrbank09.service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team09.sb01hrbank09.dto.entityDto.BackupDto;
import com.team09.sb01hrbank09.dto.request.CursorPageRequestBackupDto;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseBackupDto;
import com.team09.sb01hrbank09.entity.Backup;
import com.team09.sb01hrbank09.entity.Enum.BackupStatus;
import com.team09.sb01hrbank09.entity.File;
import com.team09.sb01hrbank09.mapper.BackupMapper;
import com.team09.sb01hrbank09.repository.BackupRepository;

@Service
public class BackupLogServiceImpl implements BackupLogServiceInterface {

	private final BackupRepository backupRepository;
	private final BackupMapper backupMapper;
	private final EmployeeServiceInterface employeeService;
	private final FileServiceInterface fileService;

	private Instant lastBackupTime = Instant.EPOCH;

	@Autowired
	public BackupLogServiceImpl(
		@Lazy BackupRepository backupRepository,
		@Lazy BackupMapper backupMapper,
		@Lazy EmployeeServiceInterface employeeService,
		@Lazy FileServiceInterface fileService
	) {
		this.backupRepository = backupRepository;
		this.backupMapper = backupMapper;
		this.employeeService = employeeService;
		this.fileService = fileService;
	}

	@Override
	@Transactional
	public BackupDto createBackup(String worker) {

		boolean isBackupInProgress = backupRepository.existsByStatus(BackupStatus.IN_PROGRESS);
		if (isBackupInProgress) {
			throw new RuntimeException("이미 백업이 진행 중입니다");
		}

		Backup backup = Backup.createBackup(worker);

		Instant lastEmployeeUpdate = employeeService.getUpdateTime();

		backupRepository.saveAndFlush(backup);

		if (lastEmployeeUpdate.isAfter(lastBackupTime)) {
			try {
				File backupFile = fileService.createCsvBackupFile();

				if (backupFile.getType().equals("csv")) {
					backup.setStatusCompleted(backupFile);
				} else if (backupFile.getType().equals("log")) {
					backup.setStatusFailed(backupFile);
				}
			} catch (IOException e) {
				throw new RuntimeException("백업 파일 및 로그 파일 생성 실패");
			}

			backupRepository.save(backup);

			lastBackupTime = lastEmployeeUpdate;

			return backupMapper.backupToDto(backup);
		}

		backup.setStatusSkipped();
		backupRepository.save(backup);

		return backupMapper.backupToDto(backup);
	}

	@Override
	@Transactional(readOnly = true)
	public CursorPageResponseBackupDto findBackupList(CursorPageRequestBackupDto request) {
		Sort sort = Sort.by(Sort.Direction.fromString(request.sortDirection()), request.sortField());
		Pageable pageable = PageRequest.of(0, request.size() + 1, sort);

		List<Backup> backups = getBackups(request, pageable);

		boolean hasNext = false;
		if (backups.size() > request.size()) {
			hasNext = true;
			backups = backups.subList(0, request.size());
		}

		Long totalCount = backupRepository.getCount(
			request.worker(),
			request.status(),
			request.startedAtFrom(),
			request.startedAtTo()
		);

		Long nextIdAfter = null;
		String nextCursor = null;
		if (!backups.isEmpty()) {
			Backup lastBackup = backups.get(backups.size() - 1);
			nextIdAfter = lastBackup.getId();

			if (request.sortField().equalsIgnoreCase("startedAt")) {
				nextCursor = String.valueOf(lastBackup.getStartedAt());
			} else {
				nextCursor = String.valueOf(lastBackup.getEndedAt());
			}
		}

		List<BackupDto> backupDtos = backups.stream()
			.map(backupMapper::backupToDto)
			.toList();

		return new CursorPageResponseBackupDto(
			backupDtos,
			nextCursor,
			nextIdAfter,
			request.size(),
			totalCount,
			hasNext
		);
	}

	private List<Backup> getBackups(CursorPageRequestBackupDto request, Pageable pageable) {

		if (request.sortField().equalsIgnoreCase("startedAt")) {
			if (request.sortDirection().equalsIgnoreCase("asc")) {
				return backupRepository.findBackupsByStartedAtOrderByIdAsc(
					request.worker(),
					request.status(),
					request.startedAtFrom(),
					request.startedAtTo(),
					request.idAfter(),
					request.cursor(),
					pageable
				);
			}

			return backupRepository.findBackupsByStartedAtOrderByIdDesc(
				request.worker(),
				request.status(),
				request.startedAtFrom(),
				request.startedAtTo(),
				request.idAfter(),
				request.cursor(),
				pageable
			);
		}

		if (request.sortDirection().equalsIgnoreCase("asc")) {
			return backupRepository.findBackupsByEndedAtOrderByIdAsc(
				request.worker(),
				request.status(),
				request.startedAtFrom(),
				request.startedAtTo(),
				request.idAfter(),
				request.cursor(),
				pageable
			);
		}

		return backupRepository.findBackupsByEndedAtOrderByIdDesc(
			request.worker(),
			request.status(),
			request.startedAtFrom(),
			request.startedAtTo(),
			request.idAfter(),
			request.cursor(),
			pageable
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
