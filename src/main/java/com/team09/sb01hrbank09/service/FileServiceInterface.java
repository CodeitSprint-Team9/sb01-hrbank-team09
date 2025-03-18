package com.team09.sb01hrbank09.service;

import org.springframework.web.multipart.MultipartFile;

import com.team09.sb01hrbank09.dto.entityDto.FileDto;
import com.team09.sb01hrbank09.entity.File;

public interface FileServiceInterface {

	String downloadFile(Long id);

	File createFile(MultipartFile file);

	boolean deleteFile(File file);
}
