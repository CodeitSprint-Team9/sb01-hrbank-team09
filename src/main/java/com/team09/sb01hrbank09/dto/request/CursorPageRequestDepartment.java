package com.team09.sb01hrbank09.dto.request;

public record CursorPageRequestDepartment(
	String nameOrDescription,
	Long idAfter,
	String cursor,
	int size,
	String sortField,
	String sortDirection
) {

	public static CursorPageRequestDepartment copy(CursorPageRequestDepartment request, Long idAfter, String cursor) {
		return new CursorPageRequestDepartment(
			request.nameOrDescription(),
			idAfter,
			cursor,
			request.size(),
			request.sortField(),
			request.sortDirection()
		);
	}
}
