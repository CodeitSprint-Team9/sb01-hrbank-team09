package com.team09.sb01hrbank09.entity;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.GenerationType.*;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "employees")
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
	private LocalDate hireDateFrom;

	@Enumerated(STRING)
	@Column(nullable = false, length = 20)
	private EmployeeStatus status;

	protected Employee() {
	}

}
