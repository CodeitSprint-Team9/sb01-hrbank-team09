package com.team09.sb01hrbank09.service;

import java.time.Instant;
import java.util.List;

import com.team09.sb01hrbank09.dto.entityDto.DiffDto;
import com.team09.sb01hrbank09.dto.entityDto.EmployeeDto;
import com.team09.sb01hrbank09.dto.request.CursorPageRequestChangeLog;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseChangeLogDto;
import com.team09.sb01hrbank09.entity.Enum.ChangeLogType;

public interface ChangeLogServiceInterface {

	//이력 생성
	void createChangeLog(ChangeLogType type, String employeeNumber, String memo, String ipAddress,
		EmployeeDto beforeEmployee, EmployeeDto afterEmployee);

	//직원 정보 수정 이력 목록 조회
	CursorPageResponseChangeLogDto findChangeLogList(CursorPageRequestChangeLog request);

	//직원 정보 수정 이력 상세 조회
	List<DiffDto> findChangeLogById(Long id);

	//수정 이력 건수 조회
	Long countChangeLog(Instant fromDate, Instant toDate);

}
