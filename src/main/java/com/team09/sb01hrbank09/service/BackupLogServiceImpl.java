package com.team09.sb01hrbank09.service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
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

		Page<Backup> backups = getBackups(request, pageable);
		List<Backup> backupList = backups.getContent();

		boolean hasNext = backupList.size() > request.size();
		if (hasNext) {
			backupList = backupList.subList(0, request.size());
		}

		List<BackupDto> backupDtos = backupList.stream()
			.map(backupMapper::backupToDto)
			.toList();

		Long nextIdAfter = null;
		String nextCursor = null;
		if (!backupDtos.isEmpty()) {
			nextIdAfter = backupDtos.get(backupDtos.size() - 1).id();
			nextCursor = String.valueOf(backupDtos.get(backupDtos.size() - 1).startedAt());
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

	private Page<Backup> getBackups(CursorPageRequestBackupDto request, Pageable pageable) {
		if (request.sortDirection().equalsIgnoreCase("ASC")) {
			return backupRepository.findBackupsByCursorOrderByIdAsc(
				request.worker(),
				request.status(),
				request.startedAtFrom(),
				request.startedAtTo(),
				request.idAfter(),
				pageable
			);
		}

		return backupRepository.findBackupsByCursorOrderByIdDesc(
			request.worker(),
			request.status(),
			request.startedAtFrom(),
			request.startedAtTo(),
			request.idAfter(),
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
