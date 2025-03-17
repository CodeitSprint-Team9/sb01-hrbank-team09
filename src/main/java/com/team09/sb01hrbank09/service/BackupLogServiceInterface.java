package com.team09.sb01hrbank09.service;

import com.team09.sb01hrbank09.dto.entityDto.BackupDto;
import com.team09.sb01hrbank09.dto.request.CursorPageRequestBackupDto;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseBackupDto;

public interface BackupLogServiceInterface {

	//swagger에서 따로 request를받는 부분이 없어 ipaddress만 파라미터로 설정
	//데이터 백업 생성
	BackupDto createBackup(String worker);

	//데이터 백업 목록 조회
	CursorPageResponseBackupDto findBackupList(CursorPageRequestBackupDto request);

	//최근 백업 목록 조회
	//상태를 지정하지 않으면 성공적으로 완료된(COMPLETED) 백업을 반환합니다.
	BackupDto findLatestBackup(String status);

}
