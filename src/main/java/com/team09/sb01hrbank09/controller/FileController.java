package com.team09.sb01hrbank09.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.team09.sb01hrbank09.api.FileApi;
import com.team09.sb01hrbank09.entity.File;
import com.team09.sb01hrbank09.service.FileServiceInterface;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/files")
public class FileController implements FileApi {

	private final FileServiceInterface fileServiceInterface;

	@CrossOrigin(origins = "http://localhost:8080")
	@GetMapping("/{id}/download")
	public ResponseEntity<StreamingResponseBody> downloadFile(@PathVariable Long id) throws IOException {
		byte[] fileData = fileServiceInterface.downloadFile(id);
		File file = fileServiceInterface.findById(id);
		if (fileData == null || fileData.length == 0) {
			throw new IOException("empty");
		}

		InputStream inputStream = new ByteArrayInputStream(fileData);
		String fileName = file.getName();

		StreamingResponseBody responseBody = outputStream -> {
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			inputStream.close();
		};

		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.body(responseBody);
	}

}
