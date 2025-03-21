package com.team09.sb01hrbank09.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "departments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Department {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true, nullable = false, length = 20)
	private String name;
	@Column(nullable = false, length = 20)
	private String description;
	@Column(name = "created_at", nullable = false)
	private LocalDate establishedDate;
	@Column(name = "employee_count", nullable = false)
	private int employeeCount = 0;

	private Department(String name, String description, LocalDate establishedDate) {
		this.name = name;
		this.description = description;
		this.establishedDate = establishedDate;
	}

	public static Department createDepartments(String name, String description, LocalDate establishedDate) {
		return new Department(name, description, establishedDate);
	}

	public void updateDepartmentsName(String updateName) {
		this.name = updateName;
	}

	public void updateDepartmentDescription(String updateDescription) {
		this.description = updateDescription;
	}

	public void updateEstablishedDate(LocalDate establishedDate) {
		this.establishedDate = establishedDate;
	}

	public void increaseCount() {
		this.employeeCount++;
	}

	public void decreaseCount() {
		this.employeeCount--;
	}

}
