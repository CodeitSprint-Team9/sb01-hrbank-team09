package com.team09.sb01hrbank09.entity;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "files")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true, nullable = false, length = 20)
	private String name;
	@Column(nullable = false, length = 20)
	private String type;
	@Column(nullable = false)
	private Long size;
	@Column(nullable = false, length = 100)
	private String path;

	private File(String name, String type, Long size, String typePath) {
		this.path=typePath;
		this.type = type;
		this.size = size;
		this.name = name;
	}

	@PostPersist
	private void setFileNameAfterPersist() {
		if (this.id != null && !this.name.startsWith("id_")) {
			this.name = "id_" + this.id + "_" + this.name;
		}
	}

	public static File createImgFile(String name, String type, Long size) {
		String pathImg = Paths.get(System.getProperty("user.dir"), "files", "img",name).toString();
		return new File(name, type, size, pathImg);
	}

	public static File createCsvFile(String filename,String type, Long size, Path path) {
		String convertPath=path.toString();
		return new File(filename, type, size, convertPath);
	}

	public void updateFileName(String updateName) {
		this.name = "id_" + this.id + "_" + updateName;
	}

	public Path getFilePath() {
		return Paths.get(this.path);
	}

}
