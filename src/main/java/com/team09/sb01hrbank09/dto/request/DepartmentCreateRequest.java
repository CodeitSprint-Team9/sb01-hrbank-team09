package com.team09.sb01hrbank09.dto.request;

import java.time.Instant;

public record DepartmentCreateRequest(
	String name,
	String description,
	Instant establishedDate
) {
}