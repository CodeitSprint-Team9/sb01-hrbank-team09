package com.team09.sb01hrbank09.entity;

import static jakarta.persistence.EnumType.*;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "change_logs")
public class ChangeLog {

	@Id
	@GeneratedValue
	private Long id;

	@Enumerated(STRING)
	@Column(nullable = false, length = 20)
	private ChangeLogType type;

	@ManyToOne
	@JoinColumn(name = "employee_number")
	private Employee employee;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String details;

	private String memo;

	@Column(nullable = false, length = 50)
	private String ipAddress;

	@Column(nullable = false)
	private LocalDateTime at;

	protected ChangeLog() {
	}

	private ChangeLog(ChangeLogType type, Employee employee, String details, String ipAddress, String memo,
		LocalDateTime at) {
		this.type = type;
		this.employee = employee;
		this.details = details;
		this.ipAddress = ipAddress;
		this.memo = memo;
		this.at = at;
	}

	public static ChangeLog createChangeLog(ChangeLogType type, Employee employee, String details, String ipAddress,
		String memo, LocalDateTime at) {
		return new ChangeLog(type, employee, details, ipAddress, memo, at);
	}

	public static ChangeLog createChangeLog(ChangeLogType type, Employee employee, String details, String ipAddress,
		LocalDateTime at) {
		return new ChangeLog(type, employee, details, ipAddress, null, at);
	}

	public static ChangeLog createChangeLog(ChangeLogType type, Employee employee, String details, String ipAddress,
		String memo) {
		return new ChangeLog(type, employee, details, ipAddress, memo, LocalDateTime.now());
	}

	public static ChangeLog createChangeLog(ChangeLogType type, Employee employee, String details, String ipAddress) {
		return new ChangeLog(type, employee, details, ipAddress, null, LocalDateTime.now());
	}

}
