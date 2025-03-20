package com.team09.sb01hrbank09.service;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.team09.sb01hrbank09.dto.entityDto.EmployeeDto;
import com.team09.sb01hrbank09.entity.File;
import com.team09.sb01hrbank09.repository.FileRepository;

@Service
public class FileServiceImpl implements FileServiceInterface {

	private final FileRepository fileRepository;
	private final EmployeeServiceInterface employeeServiceInterface;

	public FileServiceImpl(
		@Lazy FileRepository fileRepository,
		@Lazy EmployeeServiceInterface employeeServiceInterface
	) {
		this.fileRepository = fileRepository;
		this.employeeServiceInterface = employeeServiceInterface;
	}

	@Override
	@Transactional(readOnly = true)
	public String downloadFile(Long id) throws IOException {
		File file = fileRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("file with id " + id + " not found"));
		Path getFile = file.getFilePath();
		if (Files.exists(getFile)) {
			byte[] fileContent = Files.readAllBytes(getFile);
			if (file.getType().equalsIgnoreCase("txt") || file.getType().equalsIgnoreCase("csv") || file.getType().equalsIgnoreCase("log")) {
				return new String(fileContent, StandardCharsets.UTF_8);
			} else {
				return Base64.getEncoder().encodeToString(fileContent);
			}
		} else {
			throw new FileNotFoundException("File not found at path: " + getFile);
		}

	}

	@Override
	@Transactional
	public File createCsvBackupFile() throws IOException {
		List<EmployeeDto> data = employeeServiceInterface.getEmployeeAllList();

		String directoryPath = System.getProperty("user.dir") + "/files/csv";
		String fileName = "employee_backup_" + Instant.now().toEpochMilli() + ".csv";
		Path filePath = Paths.get(directoryPath, fileName);

		Files.createDirectories(filePath);

		try (BufferedWriter writer = Files.newBufferedWriter(filePath);
			 CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
				 .withHeader("ID", "EmployeeNumber", "Name", "Email", "DepartmentName",
					 "Position", "HireDate", "Status"))) {

			int batchSize = 10000;//5000?
			int count = 0;

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

				count++;

				if (count % batchSize == 0) {
					csvPrinter.flush();
				}
			}

			csvPrinter.flush();
			File fileEntity = File.createCsvFile(fileName, Files.size(filePath), filePath);
			fileRepository.save(fileEntity);

			return fileEntity;
		} catch (IOException e) {
			Path errorPath = logError(filePath, e);
			java.io.File errorFileObj = errorPath.toFile();
			long errorFileSize = Files.size(errorPath);

			File errorFile = File.createErrorFile(errorFileObj.getName(), errorFileSize, errorPath);
			fileRepository.save(errorFile);
			return errorFile;
		}

	}

	@Override
	@Transactional
	public File createImgFile(MultipartFile file) throws IOException {

		File entityFile = File.createImgFile(file.getOriginalFilename(),file.getSize());
		Path filePath = entityFile.getFilePath();

		file.transferTo(filePath.toFile());

		fileRepository.save(entityFile);

		return entityFile;
	}

	@Override
	@Transactional
	public boolean deleteFile(File file) {
		if (file == null)
			return true;
		if (fileRepository.existsById(file.getId())) {
			Path path = file.getFilePath();
			try {
				if (Files.exists(path)) {
					Files.delete(path);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			fileRepository.delete(file);
			return true;
		}
		return false;
	}

	@Override
	public File findById(Long Id) {
		return fileRepository.findById(Id).
			orElseThrow(() -> new NoSuchElementException("file with id " + Id + " not found"));
	}

	private Path logError(Path filePath, IOException e) {
		String logFileName = "backup_error_" + Instant.now().toEpochMilli() + ".log";
		Path logFilePath = Paths.get(filePath.getParent().toString(), logFileName);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath.toFile()))) {
			writer.write("Error during CSV backup: " + e.getMessage());
		} catch (IOException logEx) {
			logEx.printStackTrace();
		}
		return logFilePath;
	}
}
