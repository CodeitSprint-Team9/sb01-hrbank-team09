package com.team09.sb01hrbank09.dto.response;

import java.util.List;

import com.team09.sb01hrbank09.dto.entityDto.ChangeLogDto;

public record CursorPageResponseChangeLogDto(
	List<ChangeLogDto> content,
	String nextCursor,
	Long nextIdAfter,
	int size,
	Long totalElements,
	boolean hasNext
) {
}
