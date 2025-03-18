package com.team09.sb01hrbank09.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
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

	@GetMapping("{/id}/download")
	ResponseEntity<String> downloadFile(@PathVariable Long id) throws IOException {
		String response = fileServiceInterface.downloadFile(id);
		return ResponseEntity.ok(response);
	}

}
