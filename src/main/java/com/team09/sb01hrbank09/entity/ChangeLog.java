package com.team09.sb01hrbank09.entity;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.FetchType.*;

import java.time.Instant;

import com.team09.sb01hrbank09.entity.Enum.ChangeLogType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "change_logs")
@Getter
public class ChangeLog {

	@Id
	@GeneratedValue
	private Long id;

	@Enumerated(STRING)
	@Column(nullable = false, length = 20)
	private ChangeLogType type;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "employee_number")
	private Employee employee;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String details;

	private String memo;

	@Column(nullable = false, length = 50)
	private String ipAddress;

	@Column(nullable = false)
	private Instant at;

	protected ChangeLog() {
	}

	private ChangeLog(ChangeLogType type, Employee employee, String details, String ipAddress, String memo) {
		this.type = type;
		this.employee = employee;
		this.details = details;
		this.ipAddress = ipAddress;
		this.memo = memo;
		this.at = Instant.now();
	}

	public static ChangeLog createChangeLog(ChangeLogType type, Employee employee, String details, String ipAddress,
		String memo) {
		return new ChangeLog(type, employee, details, ipAddress, memo);
	}

	public static ChangeLog createChangeLog(ChangeLogType type, Employee employee, String details, String ipAddress) {
		return new ChangeLog(type, employee, details, ipAddress, null);
	}

}
