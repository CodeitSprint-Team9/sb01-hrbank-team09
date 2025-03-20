package com.team09.sb01hrbank09.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team09.sb01hrbank09.service.FileServiceInterface;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/files")
public class FileController {

	private final FileServiceInterface fileServiceInterface;

	@CrossOrigin(origins = "http://localhost:8080")
	@GetMapping("/{id}/download")
	ResponseEntity<byte[]> downloadFile(@PathVariable Long id) throws IOException {
		byte[] response = fileServiceInterface.downloadFile(id);
		return ResponseEntity.ok(response);
	}

}
