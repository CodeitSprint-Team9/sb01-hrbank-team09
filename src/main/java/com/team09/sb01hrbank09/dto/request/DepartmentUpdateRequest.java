package com.team09.sb01hrbank09.dto.request;

import java.time.Instant;
import java.time.LocalDate;

public record DepartmentUpdateRequest(
	String name,
	String description,
	LocalDate establishedDate
) {
}
