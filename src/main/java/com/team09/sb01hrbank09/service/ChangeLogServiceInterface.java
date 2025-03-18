package com.team09.sb01hrbank09.service;

import java.time.Instant;
import java.util.List;

import com.team09.sb01hrbank09.dto.entityDto.DiffDto;
import com.team09.sb01hrbank09.dto.request.CursorPageRequestChangeLog;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseChangeLogDto;

public interface ChangeLogServiceInterface {

	//직원 정보 수정 이력 목록 조회
	CursorPageResponseChangeLogDto findChangeLogList(CursorPageRequestChangeLog request);

	//직원 정보 수정 이력 상세 조회
	List<DiffDto> findChangeLogById(Long id);

	//수정 이력 건수 조회
	Long countChangeLog(Instant fromDate, Instant toDate);

}
