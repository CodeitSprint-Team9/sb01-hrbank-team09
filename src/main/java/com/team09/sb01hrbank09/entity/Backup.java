package com.team09.sb01hrbank09.entity;

import static jakarta.persistence.EnumType.*;

import java.time.Instant;

import com.team09.sb01hrbank09.entity.Enum.BackupStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "backups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Backup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "started_at", nullable = false)
	private Instant startedAt;

	@Column(name = "ended_at")
	private Instant endedAt;

	@Column
	@Enumerated(STRING)
	private BackupStatus status;

	@Column(name = "worker", nullable = false)
	private String worker;

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "employee_id", nullable = false)
	// private Employee employeeId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "file_id")
	private File file;

	private Backup(String worker, File file, BackupStatus backupStatus) {
		this.worker = worker;
		this.file = file;
		this.status = backupStatus;
		this.startedAt = Instant.now();
		this.endedAt = null;
	}

	public static Backup createBackup(String worker, Employee employeeId, File file) {
		return new Backup(worker, file, BackupStatus.IN_PROGRESS);
	}

	public static Backup createBackup(String worker) {
		return new Backup(worker, null, BackupStatus.IN_PROGRESS);
	}

	public void setStatusSkipped() {
		this.status = BackupStatus.SKIPPED;
		this.file = null;
		this.endedAt = Instant.now();
	}

	public void setStatusCompleted(File file) {
		this.status = BackupStatus.COMPLETED;
		this.file = file;
		this.endedAt = Instant.now();
	}

	public void setStatusFailed(File file) {
		this.status = BackupStatus.FAILED;
		this.file = file;
		this.endedAt = Instant.now();
	}

}
