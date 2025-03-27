package com.team09.sb01hrbank09.dto.request;

import com.team09.sb01hrbank09.entity.Enum.EmployeeStatus;

public record CursorPageRequestEmployeeDto(
    String nameOrEmail,
    String departmentName,
    String position,
    EmployeeStatus status,
    Long idAfter,
    String cursor,
    int size,
    String sortField,
    String sortDirection
) {

}
