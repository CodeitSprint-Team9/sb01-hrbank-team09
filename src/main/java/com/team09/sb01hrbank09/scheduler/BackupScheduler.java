package com.team09.sb01hrbank09.scheduler;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.team09.sb01hrbank09.service.BackupLogServiceInterface;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BackupScheduler {

	private final BackupLogServiceInterface backupService;

	@Scheduled(cron = "0 0 * * * ?")
	public void scheduledBackup() throws UnknownHostException {
		backupService.createBackup(InetAddress.getLocalHost().getHostAddress());
	}
}
