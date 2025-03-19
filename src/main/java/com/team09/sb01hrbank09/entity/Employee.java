package com.team09.sb01hrbank09.entity;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;

import java.time.Instant;

import com.team09.sb01hrbank09.entity.Enum.EmployeeStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employees")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Employee {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(nullable = false, length = 20)
	private String name;

	@Column(nullable = false, unique = true, length = 20)
	private String email;

	@Column(name = "employee_number", nullable = false, length = 50)
	private String employeeNumber;

	@Column(nullable = false, length = 20)
	private String position;

	@Column(nullable = false)
	private Instant hireDateFrom;

	@Enumerated(STRING)
	@Column(nullable = false, length = 20)
	private EmployeeStatus status;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "file_id")
	private File file;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "dept_id")
	private Department department;


	private Employee(String name, String email, String employeeNumber, String position
		, Instant hireDateFrom, EmployeeStatus status, File file, Department department) {
		this.name = name;
		this.email = email;
		this.employeeNumber = employeeNumber;
		this.position = position;
		this.hireDateFrom = hireDateFrom;
		this.status = status;
		this.file = file;
		this.department = department;
	}

	public static Employee createEmployee(String name, String email, String employeeNumber, String position,
		Instant hireDateFrom, EmployeeStatus status, File file, Department department) {
		return new Employee(name, email, employeeNumber, position, hireDateFrom, status, file, department);
	}

	public void updatePosition(String position) {
		this.position = position;
	}

	public void updateEmail(String email) {
		this.email = email;
	}

	public void updateName(String name) {
		this.name = name;
	}

	public void updateHireDateFrom(Instant hireDateFrom) {
		this.hireDateFrom = hireDateFrom;
	}

	public void updateStatus(EmployeeStatus status) {
		this.status = status;
	}

	public void updateFile(File file) {
		this.file = file;
	}

	public void updateDepartment(Department department) {
		this.department = department;
	}

}
