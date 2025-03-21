package com.team09.sb01hrbank09.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.team09.sb01hrbank09.entity.File;

public interface FileServiceInterface {

	byte[] downloadFile(Long id) throws IOException;

	File createCsvBackupFile() throws IOException;

	File createImgFile(MultipartFile file) throws IOException;

	boolean deleteFile(File file);

	File findById(Long Id);

	//String makeFileBackupError();
}
