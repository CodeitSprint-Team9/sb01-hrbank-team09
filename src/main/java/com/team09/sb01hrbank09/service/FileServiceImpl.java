package com.team09.sb01hrbank09.service;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.team09.sb01hrbank09.dto.entityDto.EmployeeDto;
import com.team09.sb01hrbank09.entity.File;
import com.team09.sb01hrbank09.repository.FileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileServiceInterface {

	private final FileRepository fileRepository;
	private final EmployeeServiceInterface employeeServiceInterface;

	@Override
	public String downloadFile(Long id) throws IOException {
		File file = fileRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("file with id " + id + " not found"));
		Path getFile = file.getFilePath();
		if (Files.exists(getFile)) {
			byte[] fileContent = Files.readAllBytes(getFile);
			return fileContent.toString();//이부분은 이미지 보고 수정
		} else {
			throw new FileNotFoundException("File not found at path: " + getFile);
		}

	}

	@Override
	public File createCsvBackupFile() throws IOException {
		List<EmployeeDto> data = employeeServiceInterface.getEmployeeAllList();

		String directoryPath = System.getProperty("user.dir") + "/files/csv";
		String fileName = "employee_backup_" + Instant.now().toEpochMilli() + ".csv";
		Path filePath = Paths.get(directoryPath, fileName);

		Files.createDirectories(filePath);

		try (BufferedWriter writer = Files.newBufferedWriter(filePath);
			 CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
				 .withHeader("ID", "EmployeeNumber","Name", "Email",
					 "DepartmentName", "Position", "HireDate", "Status"))) {

			for (EmployeeDto employee : data) {
				csvPrinter.printRecord(
					employee.id(),
					employee.employeeNumber(),
					employee.name(),
					employee.email(),
					employee.departmentName(),
					employee.position(),
					employee.hireDate(),
					employee.status()
				);
			}
			csvPrinter.flush();
		}

		File fileEntity = File.createCsvFile(fileName, "csv", Files.size(filePath), filePath);
		fileRepository.save(fileEntity);

		return fileEntity;

	}

	@Override
	public File createImgFile(MultipartFile file) throws IOException {

		File entityFile = File.createImgFile(file.getName(), file.getContentType(), file.getSize());
		Path filePath = entityFile.getFilePath();

		file.transferTo(filePath.toFile());

		fileRepository.save(entityFile);

		return entityFile;
	}

	@Override
	public boolean deleteFile(File file) {
		if (fileRepository.existsById(file.getId())) {
			fileRepository.delete(file);
			return true;
		}
		return false;
	}
}
