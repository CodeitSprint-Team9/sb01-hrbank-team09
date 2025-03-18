package com.team09.sb01hrbank09.entity;

import static jakarta.persistence.EnumType.*;

import java.time.Instant;

import com.team09.sb01hrbank09.entity.Enum.ChangeLogType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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

	@Column(name = "employee_number", nullable = false, length = 50) // FK 대신 단순한 문자열 컬럼
	private String employeeNumber;

	private String memo;

	@Column(nullable = false, length = 50)
	private String ipAddress;

	@Column(nullable = false)
	private Instant at;

	@Column(columnDefinition = "jsonb") // JSON 저장
	private String before;

	@Column(columnDefinition = "jsonb") // JSON 저장
	private String after;

	protected ChangeLog() {
	}

	private ChangeLog(ChangeLogType type, String employeeNumber, String ipAddress, String memo, String beforeState,
		String afterState) {
		this.type = type;
		this.employeeNumber = employeeNumber;
		this.ipAddress = ipAddress;
		this.memo = memo;
		this.at = Instant.now();
		this.before = beforeState;
		this.after = afterState;
	}

	public static ChangeLog createChangeLog(ChangeLogType type, String employeeNumber, String ipAddress, String memo,
		String beforeState, String afterState) {
		return new ChangeLog(type, employeeNumber, ipAddress, memo, beforeState, afterState);
	}

	//메모 없을경우
	public static ChangeLog createChangeLog(ChangeLogType type, String employeeNumber, String ipAddress,
		String beforeState, String afterState) {
		return new ChangeLog(type, employeeNumber, ipAddress, null, beforeState, afterState);
	}

}
