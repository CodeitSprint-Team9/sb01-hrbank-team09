INSERT INTO departments (id, name, description, created_at, employee_count) VALUES (1, 'Back-end', 'This is backend team', '2025-03-01', 3);

INSERT INTO employees (id, name, email, employee_number, position, hire_date, status, file_id, dept_id)
VALUES (1, 'Name01', 'e1f@email.com', 'EMP-2025-qwertyuiopqwer', 'Junior', '2025-03-19', 'ACTIVE', null, 1),
       (2, 'Name01', 'e1s@email.com', 'EMP-2025-wertyuiopqwert', 'Junior', '2025-03-19', 'ACTIVE', null, 1),
       (3, 'Name02', 'e2@email.com', 'EMP-2025-ertyuiopqwerty', 'Senior', '2025-03-19', 'ACTIVE', null, 1);