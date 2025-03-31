package com.team09.sb01hrbank09.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.team09.sb01hrbank09.dto.request.CursorPageRequestEmployeeDto;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseEmployeeDto;
import com.team09.sb01hrbank09.entity.Department;
import com.team09.sb01hrbank09.entity.Employee;
import com.team09.sb01hrbank09.entity.Enum.EmployeeStatus;
import com.team09.sb01hrbank09.mapper.EmployeeMapper;
import com.team09.sb01hrbank09.mapper.EmployeeMapperImpl;
import com.team09.sb01hrbank09.repository.EmployeeRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

  @Mock
  private EmployeeRepository employeeRepository;
  @Mock
  private DepartmentServiceInterface departmentServiceInterface;
  @Mock
  private FileServiceInterface fileServiceInterface;
  @Mock
  private ChangeLogServiceInterface changeLogServiceInterface;

  private final EmployeeMapper employeeMapper = new EmployeeMapperImpl();

  //  @InjectMocks
  private EmployeeServiceImpl employeeService;

  @BeforeEach
  void setUp() {
    employeeService = new EmployeeServiceImpl(
        employeeRepository,
        departmentServiceInterface,
        fileServiceInterface,
        changeLogServiceInterface,
        employeeMapper
    );
  }

  @Nested
  @DisplayName("검색어와 필터 기능 테스트")
  class SearchAndFilterTest {

    @Test
    void 직원_이름_오름차순으로_직원_이름을_검색어로_직원을_정상적으로_검색한다_중복_시_순서는_고려하지_않는다() throws Exception {

      //given
      Department dept = Department.createDepartments("Back-end", "This is back-end team",
          LocalDate.parse("2025-03-01"));

      Employee emp1 = Employee.createEmployee("Name01", "e1f@email.com", "EMP-2025-xyz0000",
          "Junior", LocalDate.parse("2025-03-19"), EmployeeStatus.ACTIVE, null, dept);
      Employee emp2 = Employee.createEmployee("Name01", "e1s@email.com", "EMP-2025-xyz0001",
          "Senior", LocalDate.parse("2025-03-19"), EmployeeStatus.ACTIVE, null, dept);

      ReflectionTestUtils.setField(emp1, "id", 1L);
      ReflectionTestUtils.setField(emp2, "id", 2L);

      List<Employee> employees = List.of(emp1, emp2);

      given(employeeRepository.findEmployeesWithNameAsc(
          anyString(), anyString(), anyString(), any(), any(), any(), any()
      )).willReturn(employees);

      given(employeeRepository.getCount(
          anyString(), anyString(), anyString(), any()
      )).willReturn((long) employees.size());

//      List<EmployeeDto> employeeDtos = List.of(
//          new EmployeeDto(1L, "Name01", "e1f@email.com", "EMP-2025-xyz0000", 1L, "Back-end",
//              "Junior", LocalDate.parse("2025-03-19"), EmployeeStatus.ACTIVE.toString(), null),
//          new EmployeeDto(2L, "Name01", "e1s@email.com", "EMP-2025-xyz0001", 1L, "Back-end",
//              "Junior", LocalDate.parse("2025-03-19"), EmployeeStatus.ACTIVE.toString(), null)
//      );
//
//      IntStream.range(0, 2)
//          .forEach(i -> given(employeeMapper.employeeToDto(employees.get(i))).willReturn(
//              employeeDtos.get(i)));

      //when
      CursorPageRequestEmployeeDto request = new CursorPageRequestEmployeeDto(
          "name0",
          "",
          "",
          null,
          null,
          null,
          30,
          "name",
          "asc"
      );

      CursorPageResponseEmployeeDto response = employeeService.findEmployeeList(request);

      //then
      assertThat(response.content())
          .isNotEmpty()
          .allMatch(
              employeeDto -> employeeDto.name().toUpperCase().contains("name0".toUpperCase()));
      assertThat(response.nextCursor()).isEqualTo("Name01");
      assertThat(response.nextIdAfter()).isEqualTo(2L);
      assertThat(response.totalElements()).isEqualTo(2L);
      assertThat(response.hasNext()).isFalse();
    }

    @Test
    void 직원_이름_오름차순으로_이메일을_검색어로_직원을_정상적으로_검색한다() throws Exception {

      //given
      Department dept = Department.createDepartments("Back-end", "This is back-end team",
          LocalDate.parse("2025-03-01"));

      Employee emp1 = Employee.createEmployee("Name01", "e1f@email.com", "EMP-2025-xyz0000",
          "Junior", LocalDate.parse("2025-03-19"), EmployeeStatus.ACTIVE, null, dept);
      Employee emp2 = Employee.createEmployee("Name01", "e1s@email.com", "EMP-2025-xyz0001",
          "Senior", LocalDate.parse("2025-03-19"), EmployeeStatus.ACTIVE, null, dept);

      ReflectionTestUtils.setField(emp1, "id", 1L);
      ReflectionTestUtils.setField(emp2, "id", 2L);

      List<Employee> employees = List.of(emp1, emp2);

      given(employeeRepository.findEmployeesWithNameAsc(
          anyString(), anyString(), anyString(), any(), any(), any(), any()
      )).willReturn(employees);

      given(employeeRepository.getCount(
          anyString(), anyString(), anyString(), any()
      )).willReturn((long) employees.size());

      //when
      CursorPageRequestEmployeeDto request = new CursorPageRequestEmployeeDto(
          "e1",
          "",
          "",
          null,
          null,
          null,
          30,
          "name",
          "asc"
      );

      CursorPageResponseEmployeeDto response = employeeService.findEmployeeList(request);

      //then
      assertThat(response.content())
          .isNotEmpty()
          .allMatch(
              employeeDto -> employeeDto.email().toUpperCase().contains("e1".toUpperCase()));
      assertThat(response.nextCursor()).isEqualTo("Name01");
      assertThat(response.nextIdAfter()).isEqualTo(2L);
      assertThat(response.totalElements()).isEqualTo(2L);
      assertThat(response.hasNext()).isFalse();
    }

    @Test
    void 직원_이름_오름차순으로_부서명을_검색어로_직원을_정상적으로_검색한다() throws Exception {

      //given
      Department dept = Department.createDepartments("Back-end", "This is back-end team",
          LocalDate.parse("2025-03-01"));

      Employee emp1 = Employee.createEmployee("Name01", "e1f@email.com", "EMP-2025-xyz0000",
          "Junior", LocalDate.parse("2025-03-19"), EmployeeStatus.ACTIVE, null, dept);
      Employee emp2 = Employee.createEmployee("Name01", "e1s@email.com", "EMP-2025-xyz0001",
          "Senior", LocalDate.parse("2025-03-19"), EmployeeStatus.ACTIVE, null, dept);

      ReflectionTestUtils.setField(emp1, "id", 1L);
      ReflectionTestUtils.setField(emp2, "id", 2L);

      List<Employee> employees = List.of(emp1, emp2);

      given(employeeRepository.findEmployeesWithNameAsc(
          anyString(), anyString(), anyString(), any(), any(), any(), any()
      )).willReturn(employees);

      given(employeeRepository.getCount(
          anyString(), anyString(), anyString(), any()
      )).willReturn((long) employees.size());

      //when
      CursorPageRequestEmployeeDto request = new CursorPageRequestEmployeeDto(
          "",
          "back",
          "",
          null,
          null,
          null,
          30,
          "name",
          "asc"
      );

      CursorPageResponseEmployeeDto response = employeeService.findEmployeeList(request);

      //then
      assertThat(response.content())
          .isNotEmpty()
          .allMatch(
              employeeDto -> employeeDto.departmentName().toUpperCase()
                  .contains("back".toUpperCase()));
      assertThat(response.nextCursor()).isEqualTo("Name01");
      assertThat(response.nextIdAfter()).isEqualTo(2L);
      assertThat(response.totalElements()).isEqualTo(2L);
      assertThat(response.hasNext()).isFalse();

    }

    @Test
    void 직원_이름_오름차순으로_직함을_검색어로_직원을_정상적으로_검색한다_중복_시_순서는_고려하지_않는다() throws Exception {

      //given
      Department dept = Department.createDepartments("Back-end", "This is back-end team",
          LocalDate.parse("2025-03-01"));

      Employee emp1 = Employee.createEmployee("Name01", "e1f@email.com", "EMP-2025-xyz0000",
          "Junior", LocalDate.parse("2025-03-19"), EmployeeStatus.ACTIVE, null, dept);
      Employee emp2 = Employee.createEmployee("Name01", "e1s@email.com", "EMP-2025-xyz0001",
          "Senior", LocalDate.parse("2025-03-19"), EmployeeStatus.ACTIVE, null, dept);

      ReflectionTestUtils.setField(emp1, "id", 1L);
      ReflectionTestUtils.setField(emp2, "id", 2L);

      given(employeeRepository.findEmployeesWithNameAsc(
          anyString(), anyString(), anyString(), any(), any(), any(), any()
      )).willReturn(List.of(emp1));

      given(employeeRepository.getCount(
          anyString(), anyString(), anyString(), any()
      )).willReturn(1L);

      //when
      CursorPageRequestEmployeeDto request = new CursorPageRequestEmployeeDto(
          "",
          "",
          "juni",
          null,
          null,
          null,
          30,
          "name",
          "asc"
      );

      CursorPageResponseEmployeeDto response = employeeService.findEmployeeList(request);

      //then
      assertThat(response.content().get(0).position())
          .containsIgnoringCase("juni");
      assertThat(response.nextCursor()).isEqualTo("Name01");
      assertThat(response.nextIdAfter()).isEqualTo(1L);
      assertThat(response.totalElements()).isEqualTo(1L);
      assertThat(response.hasNext()).isFalse();

    }

    @Test
    void 직원_이름_오름차순으로_고용상태를_필터로_직원을_정상적으로_검색한다() throws Exception {

      //given
      Department dept = Department.createDepartments("Back-end", "This is back-end team",
          LocalDate.parse("2025-03-01"));

      Employee emp1 = Employee.createEmployee("Name01", "e1f@email.com", "EMP-2025-xyz0000",
          "Junior", LocalDate.parse("2025-03-19"), EmployeeStatus.ACTIVE, null, dept);
      Employee emp2 = Employee.createEmployee("Name01", "e1s@email.com", "EMP-2025-xyz0001",
          "Senior", LocalDate.parse("2025-03-19"), EmployeeStatus.ACTIVE, null, dept);

      ReflectionTestUtils.setField(emp1, "id", 1L);
      ReflectionTestUtils.setField(emp2, "id", 2L);

      List<Employee> employees = List.of(emp1, emp2);

      given(employeeRepository.findEmployeesWithNameAsc(
          anyString(), anyString(), anyString(), any(), any(), any(), any()
      )).willReturn(employees);

      given(employeeRepository.getCount(
          anyString(), anyString(), anyString(), any()
      )).willReturn((long) employees.size());

      //when
      CursorPageRequestEmployeeDto request = new CursorPageRequestEmployeeDto(
          "",
          "",
          "",
          EmployeeStatus.ACTIVE,
          null,
          null,
          30,
          "name",
          "asc"
      );

      CursorPageResponseEmployeeDto response = employeeService.findEmployeeList(request);

      //then
      assertThat(response.content())
          .isNotEmpty()
          .allMatch(
              employeeDto -> EmployeeStatus.valueOf(employeeDto.status()) == EmployeeStatus.ACTIVE);
      assertThat(response.nextCursor()).isEqualTo("Name01");
      assertThat(response.nextIdAfter()).isEqualTo(2L);
      assertThat(response.totalElements()).isEqualTo(2L);
      assertThat(response.hasNext()).isFalse();

    }

  }

  @Nested
  @DisplayName("페이지네이션 기능 테스트")
  class PaginationTest {

    private static final List<Employee> employees = new ArrayList<>();

    @BeforeAll
    static void setUp() {
      Department dept = Department.createDepartments("Back-end", "This is back-end team",
          LocalDate.parse("2025-03-01"));

      LocalDate startDate = LocalDate.parse("2025-03-19");
      for (long i = 1; i <= 91; i++) {
        String name = "Name" + i;
        String email = "e" + i + "@email.com";
        String employeeNumber = "EMP-2025-xyz" + String.format("%04d", i);
        String position = (i % 2 == 0) ? "Senior" : "Junior";
        LocalDate hireDate = startDate.plusDays(i - 1);
        EmployeeStatus status = EmployeeStatus.ACTIVE;

        Employee employee = Employee.createEmployee(name, email, employeeNumber, position, hireDate,
            status, null, dept);

        ReflectionTestUtils.setField(employee, "id", i);

        employees.add(employee);
      }
    }

    @Test
    void 직원_이름_오름차순으로_직원_첫_페이지를_정상적으로_탐색한다() throws Exception {

      //given
      given(employeeRepository.findEmployeesWithNameAsc(
          anyString(), anyString(), anyString(), any(), any(), any(), any()
      )).willReturn(employees.subList(0, 31));

      given(employeeRepository.getCount(
          anyString(), anyString(), anyString(), any()
      )).willReturn((long) employees.size());

      //when
      CursorPageRequestEmployeeDto request = new CursorPageRequestEmployeeDto(
          "",
          "",
          "",
          null,
          null,
          null,
          30,
          "name",
          "asc"
      );

      CursorPageResponseEmployeeDto response = employeeService.findEmployeeList(request);

      //then
      assertThat(response.content().size()).isEqualTo(30L);
      assertThat(response.nextCursor()).isEqualTo("Name30");
      assertThat(response.nextIdAfter()).isEqualTo(30L);
      assertThat(response.totalElements()).isEqualTo(91L);
      assertThat(response.hasNext()).isTrue();

    }

    @Test
    void 직원_이름_오름차순으로_직원_중간_페이지를_정상적으로_탐색한다() throws Exception {

      //given
      given(employeeRepository.findEmployeesWithNameAsc(
          anyString(), anyString(), anyString(), any(), any(), any(), any()
      )).willReturn(employees.subList(30, 61));

      given(employeeRepository.getCount(
          anyString(), anyString(), anyString(), any()
      )).willReturn((long) employees.size());

      //when
      CursorPageRequestEmployeeDto request = new CursorPageRequestEmployeeDto(
          "",
          "",
          "",
          null,
          null,
          null,
          30,
          "name",
          "asc"
      );

      CursorPageResponseEmployeeDto response = employeeService.findEmployeeList(request);

      //then
      assertThat(response.content().size()).isEqualTo(30L);
      assertThat(response.nextCursor()).isEqualTo("Name60");
      assertThat(response.nextIdAfter()).isEqualTo(60L);
      assertThat(response.totalElements()).isEqualTo(91L);
      assertThat(response.hasNext()).isTrue();

    }

    @Test
    void 직원_이름_오름차순으로_직원_마지막_페이지를_정상적으로_탐색한다() throws Exception {

      //given
      given(employeeRepository.findEmployeesWithNameAsc(
          anyString(), anyString(), anyString(), any(), any(), any(), any()
      )).willReturn(employees.subList(90, 91));

      given(employeeRepository.getCount(
          anyString(), anyString(), anyString(), any()
      )).willReturn((long) employees.size());

      //when
      CursorPageRequestEmployeeDto request = new CursorPageRequestEmployeeDto(
          "",
          "",
          "",
          null,
          null,
          null,
          30,
          "name",
          "asc"
      );

      CursorPageResponseEmployeeDto response = employeeService.findEmployeeList(request);

      //then
      assertThat(response.content().size()).isEqualTo(1L);
      assertThat(response.nextCursor()).isEqualTo("Name91");
      assertThat(response.nextIdAfter()).isEqualTo(91L);
      assertThat(response.totalElements()).isEqualTo(91L);
      assertThat(response.hasNext()).isFalse();

    }

  }
}