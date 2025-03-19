package com.team09.sb01hrbank09.dto;

import java.time.Instant;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"timestamp", "status", "message", "details"})
public class ErrorResponse {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
	private Instant timestamp;
	private HttpStatus status;
	private String message;
	private String details;
	
	// 정적 팩토리 메소드
	public static ErrorResponse of(HttpStatus status, String message, String details) {
		return new ErrorResponse(Instant.now(), status, message, details);
	}
}