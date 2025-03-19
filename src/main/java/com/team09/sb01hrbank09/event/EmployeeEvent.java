package com.team09.sb01hrbank09.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team09.sb01hrbank09.dto.entityDto.EmployeeDto;
import com.team09.sb01hrbank09.entity.Enum.ChangeLogType;

import lombok.Getter;

@Getter
public class EmployeeEvent {
	private final ChangeLogType type; // "CREATE", "UPDATE", "DELETE"
	private final String employeeNumber;
	private final String memo;
	private final String ipAddress;
	private final String beforeEmployee;
	private final String afterEmployee;
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public EmployeeEvent(ChangeLogType type, String employeeNumber, String memo, String ipAddress,
		EmployeeDto beforeEmployee, EmployeeDto afterEmployee) {
		this.type = type;
		this.employeeNumber = employeeNumber;
		this.memo = memo;
		this.ipAddress = ipAddress;
		this.beforeEmployee = toJson(beforeEmployee);
		this.afterEmployee = toJson(afterEmployee);
	}

	private static String toJson(EmployeeDto employee) {
		try {
			if (employee == null) {
				return "{}";
			}
			return objectMapper.writeValueAsString(employee); // 빈 JSON 객체
		} catch (JsonProcessingException e) {
			throw new RuntimeException("JSON 변환 실패", e);
		}
	}
}
