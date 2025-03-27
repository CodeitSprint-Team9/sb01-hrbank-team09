package com.team09.sb01hrbank09.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.team09.sb01hrbank09.dto.entityDto.EmployeeDto;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseEmployeeDto;
import com.team09.sb01hrbank09.entity.Enum.EmployeeStatus;
import com.team09.sb01hrbank09.service.EmployeeServiceInterface;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(value = EmployeeController.class)
class EmployeeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private EmployeeServiceInterface employeeService;

  @Nested
  @DisplayName("검색어와 필터 기능 테스트")
  class SearchAndFilterTest {

    @Test
    void 직원_이름_오름차순으로_직원_이름을_검색어로_직원을_정상적으로_검색한다_중복_시_순서는_고려하지_않는다() throws Exception {

      //given
      EmployeeDto empDto1 = new EmployeeDto(1L, "Name01", "e1f@email.com", "EMP-2025-xyz0000", 1L,
          "Back-end",
          "Junior", LocalDate.parse("2025-03-19"), EmployeeStatus.ACTIVE.toString(), null);
      EmployeeDto empDto2 = new EmployeeDto(2L, "Name01", "e1s@email.com", "EMP-2025-xyz0001", 1L,
          "Back-end",
          "Senior", LocalDate.parse("2025-03-19"), EmployeeStatus.ACTIVE.toString(), null);

      List<EmployeeDto> employeeDtos = List.of(empDto1, empDto2);

      given(employeeService.findEmployeeList(any())).willReturn(
          new CursorPageResponseEmployeeDto(
              employeeDtos,
              "Name01",
              2L,
              30,
              2L,
              false
          )
      );

      //when
      ResultActions action = mockMvc.perform(
          MockMvcRequestBuilders.get("/api/employees")
              .param("nameOrEmail", "Name01")
              .param("size", "30")
              .param("sortField", "name")
              .param("sortDirection", "asc")
      );

      //then
      action.andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().json(
              """
                  {
                    content: [
                      {
                        "id": 1,
                        "name": "Name01",
                        "email": "e1f@email.com",
                        "employeeNumber": "EMP-2025-xyz0000",
                        "departmentId": 1,
                        "departmentName": "Back-end",
                        "position": "Junior",
                        "hireDate": "2025-03-19",
                        "status": "ACTIVE",
                        "profileImageId": null
                      },
                      {
                        "id": 2,
                        "name": "Name01",
                        "email": "e1s@email.com",
                        "employeeNumber": "EMP-2025-xyz0001",
                        "departmentId": 1,
                        "departmentName": "Back-end",
                        "position": "Senior",
                        "hireDate": "2025-03-19",
                        "status": "ACTIVE",
                        "profileImageId": null
                      }
                    ],
                    "nextCursor": "Name01",
                    "nextIdAfter": 2,
                    "size": 30,
                    "totalElements": 2,
                    "hasNext": false
                  }
                  """
          ));
    }
  }

}