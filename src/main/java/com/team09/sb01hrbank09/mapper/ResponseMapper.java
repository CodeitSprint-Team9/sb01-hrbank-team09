package com.team09.sb01hrbank09.mapper;

import org.springframework.data.domain.Page;

import com.team09.sb01hrbank09.dto.entityDto.BackupDto;
import com.team09.sb01hrbank09.dto.entityDto.ChangeLogDto;
import com.team09.sb01hrbank09.dto.entityDto.DepartmentDto;
import com.team09.sb01hrbank09.dto.entityDto.EmployeeDto;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseBackupDto;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseChangeLogDto;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseDepartmentDto;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseEmployeeDto;

public class ResponseMapper {

	public static CursorPageResponseBackupDto toCursorPageResponseBackupDto(Page<BackupDto> page) {
		Long nextCursor = null;
		if (!page.getContent().isEmpty()) {
			nextCursor = page.getContent().get(page.getContent().size() - 1).id();
		}

		return new CursorPageResponseBackupDto<BackupDto>(
			page.getContent(),
			nextCursor != null ? nextCursor.toString() : null,
			page.getNumber() + 1L,
			page.getSize(),
			page.getTotalElements(),
			page.hasNext()
		);
	}

	public static CursorPageResponseChangeLogDto toCursorPageResponseChangeLogDto(Page<ChangeLogDto> page) {
		Long nextCursor = null;
		if (!page.getContent().isEmpty()) {

			nextCursor = page.getContent().get(page.getContent().size() - 1).id();
		}

		return new CursorPageResponseChangeLogDto(
			page.getContent(),
			nextCursor != null ? nextCursor.toString() : null, // 커서 값이 있으면 문자열로 변환
			page.getNumber() + 1L, // 페이지 번호는 1부터 시작
			page.getSize(),
			page.getTotalElements(),
			page.hasNext() // 다음 페이지가 있는지 여부
		);
	}

	public static CursorPageResponseDepartmentDto toCursorPageResponseDepartmentDto(Page<DepartmentDto> page) {
		Long nextCursor = null;
		if (!page.getContent().isEmpty()) {
			nextCursor = page.getContent().get(page.getContent().size() - 1).id();
		}

		return new CursorPageResponseDepartmentDto(
			page.getContent(),
			nextCursor != null ? nextCursor.toString() : null,
			page.getNumber() + 1L,
			page.getSize(),
			page.getTotalElements(),
			page.hasNext()
		);
	}

	public static CursorPageResponseEmployeeDto toCursorPageResponseEmployeeDto(Page<EmployeeDto> page) {
		Long nextCursor = null;
		if (!page.getContent().isEmpty()) {

			nextCursor = page.getContent().get(page.getContent().size() - 1).id();
		}

		return new CursorPageResponseEmployeeDto(
			page.getContent(),
			nextCursor != null ? nextCursor.toString() : null,
			page.getNumber() + 1L,
			page.getSize(),
			page.getTotalElements(),
			page.hasNext()
		);
	}
}
