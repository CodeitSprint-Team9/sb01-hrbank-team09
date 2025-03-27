package com.team09.sb01hrbank09.integrationtest;

import static org.assertj.core.api.Assertions.assertThat;

import com.team09.sb01hrbank09.dto.response.CursorPageResponseEmployeeDto;
import com.team09.sb01hrbank09.entity.Department;
import com.team09.sb01hrbank09.entity.Employee;
import com.team09.sb01hrbank09.entity.Enum.EmployeeStatus;
import com.team09.sb01hrbank09.repository.DepartmentRepository;
import com.team09.sb01hrbank09.repository.EmployeeRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class EmployeeServiceIntegrationTest {

  @LocalServerPort
  private int port;

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Test
  void 직원_이름_오름차순으로_직원_이름을_검색어로_직원을_정상적으로_검색한다_중복_시_순서는_고려하지_않는다() {

    //given
    Department dept = Department.createDepartments("Back-end", "This is back-end",
        LocalDate.parse("2025-03-01"));

    departmentRepository.save(dept);

    Employee emp1 = Employee.createEmployee("Name01", "e1f@email.com", "EMP-2025-xyz0000",
        "Junior", LocalDate.parse("2025-03-19"), EmployeeStatus.ACTIVE, null, dept);
    Employee emp2 = Employee.createEmployee("Name01", "e1s@email.com", "EMP-2025-xyz0001",
        "Senior", LocalDate.parse("2025-03-19"), EmployeeStatus.ACTIVE, null, dept);

    employeeRepository.saveAll(List.of(emp1, emp2));

    String url = "http://localhost:" + port + "/api/employees?nameOrEmail=name01";

    //when
    ResponseEntity<CursorPageResponseEmployeeDto> responseEntity = testRestTemplate.getForEntity(
        url, CursorPageResponseEmployeeDto.class);

    //then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseEntity.getBody()).isNotNull();
    assertThat(responseEntity.getBody().content().size()).isEqualTo(2);
  }
}
