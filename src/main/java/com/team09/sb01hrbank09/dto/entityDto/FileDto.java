package com.team09.sb01hrbank09.dto.entityDto;

public record FileDto(
	Long id,
	String name,
	String type,
	Long size,
	String path
) {
}
