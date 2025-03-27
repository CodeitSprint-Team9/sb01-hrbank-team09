package com.team09.sb01hrbank09.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.team09.sb01hrbank09.entity.Employee;
import com.team09.sb01hrbank09.entity.Enum.EmployeeStatus;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepositoryTest {

  @Autowired
  private EmployeeRepository employeeRepository;

//  @Autowired
//  private TestEntityManager em;

  @Nested
  @DisplayName("검색어와 필터 기능 테스트")
  @Sql(scripts = {"/employee/search_and_filter_data.sql"})
  class SearchAndFilterTest {

    @Test
    void 직원_이름_오름차순으로_직원_이름을_검색어로_직원을_정상적으로_검색한다_중복_시_순서는_고려하지_않는다() throws Exception {

      //given
      String searchText = "name01";

      //when
      Pageable pageable = PageRequest.of(0, 30, Sort.by(Direction.ASC, "name"));
      List<Employee> employees = employeeRepository.findEmployeesWithNameAsc(
          searchText, "", "", null, null, null, pageable
      );

      //then
      assertThat(employees)
          .allMatch(
              employee -> employee.getName().toUpperCase().contains(searchText.toUpperCase()));
    }

    @Test
    void 직원_이름_오름차순으로_이메일을_검색어로_직원을_정상적으로_검색한다() throws Exception {

      //given
      String searchText = "e2@email";

      //when
      Pageable pageable = PageRequest.of(0, 30, Sort.by(Direction.ASC, "name"));
      List<Employee> employees = employeeRepository.findEmployeesWithNameAsc(
          searchText, "", "", null, null, null, pageable
      );

      //then
      assertThat(employees)
          .hasSize(1)
          .allMatch(
              employee -> employee.getEmail().toUpperCase().contains(searchText.toUpperCase()));
    }

    @Test
    void 직원_이름_오름차순으로_부서명을_검색어로_직원을_정상적으로_검색한다() throws Exception {

      //given
      String searchText = "back";

      //when
      Pageable pageable = PageRequest.of(0, 30, Sort.by(Direction.ASC, "name"));
      List<Employee> employees = employeeRepository.findEmployeesWithNameAsc(
          "", searchText, "", null, null, null, pageable
      );

      //then
      assertThat(employees)
          .allMatch(employee -> employee.getDepartment().getName().toUpperCase()
              .contains(searchText.toUpperCase()));
    }

    @Test
    void 직원_이름_오름차순으로_직함을_검색어로_직원을_정상적으로_검색한다_중복_시_순서는_고려하지_않는다() throws Exception {

      //given
      String searchText = "juni";

      //when
      Pageable pageable = PageRequest.of(0, 30, Sort.by(Direction.ASC, "name"));
      List<Employee> employees = employeeRepository.findEmployeesWithNameAsc(
          "", "", searchText, null, null, null, pageable
      );

      //then
      assertThat(employees)
          .allMatch(
              employee -> employee.getPosition().toUpperCase().contains(searchText.toUpperCase()));
    }

    @Test
    void 직원_이름_오름차순으로_고용상태를_필터로_직원을_정상적으로_검색한다() throws Exception {

      //given
      EmployeeStatus filterStatus = EmployeeStatus.ACTIVE;

      //when
      Pageable pageable = PageRequest.of(0, 30, Sort.by(Direction.ASC, "name"));
      List<Employee> employees = employeeRepository.findEmployeesWithNameAsc(
          "", "", "", filterStatus, null, null, pageable
      );

      //then
      assertThat(employees)
          .allMatch(employee -> employee.getStatus() == EmployeeStatus.ACTIVE);
    }
  }

  @Nested
  @DisplayName("페이지네이션 기능 테스트")
  @Sql(scripts = {"/employee/pagination_data.sql"})
  class PaginationTest {

    @Test
    void 직원_이름_오름차순으로_직원_첫_페이지를_정상적으로_탐색한다() throws Exception {
      //given
      String cursorName = null;
      Long idAfter = null;
      int pageSize = 30;

      //when
      Pageable pageable = PageRequest.of(0, pageSize, Sort.by(Direction.ASC, "name"));
      List<Employee> employees = employeeRepository.findEmployeesWithNameAsc(
          "", "", "", null, cursorName, idAfter, pageable
      );
      Long totalCount = employeeRepository.getCount("", "", "", null);

      //then
      assertThat(employees.size()).isEqualTo(pageSize);
      assertThat(totalCount).isEqualTo(91);

    }

    @Test
    void 직원_이름_오름차순으로_직원_중간_페이지를_정상적으로_탐색한다() throws Exception {
      //given
      String cursorName = "Name30";
      Long idAfter = 30L;
      int pageSize = 30;

      //when
      Pageable pageable = PageRequest.of(0, pageSize, Sort.by(Direction.ASC, "name"));
      List<Employee> employees = employeeRepository.findEmployeesWithNameAsc(
          "", "", "", null, cursorName, idAfter, pageable
      );
      Long totalCount = employeeRepository.getCount("", "", "", null);

      //then
      assertThat(employees.get(0).getName()).isGreaterThan(cursorName);
      assertThat(employees.get(0).getId()).isGreaterThan(idAfter);
      assertThat(employees.size()).isEqualTo(pageSize);
      assertThat(totalCount).isEqualTo(91);

    }

    @Test
    void 직원_이름_오름차순으로_직원_마지막_페이지를_정상적으로_탐색한다() throws Exception {
      //given
      String cursorName = "Name90";
      Long idAfter = 90L;
      int pageSize = 30;

      //when
      Pageable pageable = PageRequest.of(0, pageSize, Sort.by(Direction.ASC, "name"));
      List<Employee> employees = employeeRepository.findEmployeesWithNameAsc(
          "", "", "", null, cursorName, idAfter, pageable
      );
      Long totalCount = employeeRepository.getCount("", "", "", null);

      //then
      assertThat(employees.get(0).getName()).isEqualTo("Name91");
      assertThat(employees.get(0).getId()).isEqualTo(91L);
      assertThat(totalCount).isEqualTo(91);

    }

  }

}