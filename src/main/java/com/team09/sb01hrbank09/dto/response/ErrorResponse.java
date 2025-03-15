package com.team09.sb01hrbank09.dto.response;

import java.time.Instant;

public record ErrorResponse(
	Instant timestamp,
	int status,
	String message,
	String details
) {
}