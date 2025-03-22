package com.team09.sb01hrbank09.api;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "파일 관리", description = "파일 관리 API")
public interface FileApi {

	@Operation(summary = "파일 다운로드", description = "주어진 ID에 해당하는 파일을 다운로드합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "파일 다운로드 성공"),
		// @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	ResponseEntity<StreamingResponseBody> downloadFile(Long id) throws IOException;
}
