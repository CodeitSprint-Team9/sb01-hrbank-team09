package com.team09.sb01hrbank09.service;

import java.time.Instant;

import com.team09.sb01hrbank09.dto.entityDto.ChangeLogDto;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseChangeLogDto;

public interface ChangeLogServiceInterface {

	//직원 정보 수정 이력 목록 조회
	CursorPageResponseChangeLogDto findChangeLogList(Long id, ChangeLogDto changeLogDto, int size);

	//직원 정보 수정 이력 상세 조회
	ChangeLogDto findChangeLogById(Long id);

	//수정 이력 건수 조회
	Long countChangeLog(Instant fromDate, Instant toDate);

}
