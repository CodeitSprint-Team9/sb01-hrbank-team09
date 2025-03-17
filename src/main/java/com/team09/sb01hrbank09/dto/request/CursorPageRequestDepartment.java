package com.team09.sb01hrbank09.dto.request;

public record CursorPageRequestDepartment(
	String nameOrDescription,
	Long idAfter,
	String cursor,
	int size,
	String sortField,
	String sortDirection
) {
}
