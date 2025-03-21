# Team9 - HR Bank 프로젝트

## 팀원 구성
- **민기**   [GitHub](https://github.com/GiMin0123)
- **김상호** [GitHub](https://github.com/ghtkdrla)
- **김승찬** [GitHub](https://github.com/tmdcks801)
- **김도일**
- **백승헌** [GitHub](https://github.com/FrogBaek)

## 프로젝트 소개
**프로젝트명**: 프로그래밍 교육 사이트의 Spring 백엔드 시스템 구축  
**프로젝트 기간**: 2025.03.14  ~ 2025.03.23

## 기술 스택
- **Backend**: Spring Boot, Spring Data JPA
- **Database**: PostgreSql
- **공통 Tool**: Git & Github, Discord, ERD CLOUD

## 팀원별 구현 기능 상세
각자 맡은 도메인의 컨트롤러, 서비스 레포지토리까지 작업했습니다.
- **민기**: 서비스 배포, Change log 도메인 작업![이력.png](ReadMdImg/%EC%9D%B4%EB%A0%A5.png)
- **김상호**: Department 도메인 작업![부서.png](ReadMdImg/%EB%B6%80%EC%84%9C.png)
- **김승찬**: Employee, file 도메인 작업![직원.png](ReadMdImg/%EC%A7%81%EC%9B%90.png)
- **김도일**: 테스트 작업
- **백승헌**: Backup 도메인 작업![백업.png](ReadMdImg/%EB%B0%B1%EC%97%85.png)

## 파일 구조

```plaintext
checkStyle
files
 ┣ img
 ┗ csv
src
 ┣ main
 ┃ ┣ java
 ┃ ┃ ┣ com.team09.sb01hrbank09
 ┃ ┃ ┃ ┣ controller
 ┃ ┃ ┃ ┃ ┣ BackupController.java
 ┃ ┃ ┃ ┃ ┣ ChangeLogController.java
 ┃ ┃ ┃ ┃ ┣ FileController.java
 ┃ ┃ ┃ ┃ ┣ EmployeeController.java
 ┃ ┃ ┃ ┃ ┗ DepartmentController.java
 ┃ ┃ ┃ ┣ dto
 ┃ ┃ ┃ ┃ ┣entityDto
 ┃ ┃ ┃ ┃ ┃ ┣ BackupDto.java
 ┃ ┃ ┃ ┃ ┃ ┣ ChangeLogDto.java
 ┃ ┃ ┃ ┃ ┃ ┣ DepartmentDto.java
 ┃ ┃ ┃ ┃ ┃ ┣ DiffDto.java
 ┃ ┃ ┃ ┃ ┃ ┣ EmployeeDistributionDto.java
 ┃ ┃ ┃ ┃ ┃ ┣ EmployeeDto.java
 ┃ ┃ ┃ ┃ ┃ ┣ EmployeeTrendDto.java
 ┃ ┃ ┃ ┃ ┃ ┗FileDto.java
 ┃ ┃ ┃ ┃ ┣request
 ┃ ┃ ┃ ┃ ┃ ┣ CursorPageRequestBackupDto.java
 ┃ ┃ ┃ ┃ ┃ ┣ CursorPageRequestChangeLog.java
 ┃ ┃ ┃ ┃ ┃ ┣ CursorPageRequestDepartment.java
 ┃ ┃ ┃ ┃ ┃ ┣ DepartmentCreateRequest.java
 ┃ ┃ ┃ ┃ ┃ ┣ DepartmentUpdateRequest.java
 ┃ ┃ ┃ ┃ ┃ ┣ EmployeeCreateRequest.java
 ┃ ┃ ┃ ┃ ┃ ┗ EmployeeUpdateRequest.java
 ┃ ┃ ┃ ┃ ┗response
 ┃ ┃ ┃ ┃ ┃ ┣ CursorPageResponseBackupDto.java
 ┃ ┃ ┃ ┃ ┃ ┣ CursorPageResponseChangeLogDto.java
 ┃ ┃ ┃ ┃ ┃ ┣ CursorPageResponseDepartmentDto.java
 ┃ ┃ ┃ ┃ ┃ ┗ CursorPageResponseEmployeeDto.java
 ┃ ┃ ┃ ┃ ┗ ErrorResponse.java
 ┃ ┃ ┃ ┣ repository
 ┃ ┃ ┃ ┃ ┣ BackupRepository.java
 ┃ ┃ ┃ ┃ ┣ ChangeLogRepository.java
 ┃ ┃ ┃ ┃ ┣ FileRepository.java
 ┃ ┃ ┃ ┃ ┣ EmployeeRepository.java
 ┃ ┃ ┃ ┃ ┗ DepartmentRepository.java
 ┃ ┃ ┃ ┣ service
 ┃ ┃ ┃ ┃ ┣ BackupService.java
 ┃ ┃ ┃ ┃ ┣ ChangeLogService.java
 ┃ ┃ ┃ ┃ ┣ FileService.java
 ┃ ┃ ┃ ┃ ┣ EmployeeService.java
 ┃ ┃ ┃ ┃ ┗ DepartmentService.java
 ┃ ┃ ┃ ┣ entity
 ┃ ┃ ┃ ┃ ┣ Backup.java
 ┃ ┃ ┃ ┃ ┣ Change.java
 ┃ ┃ ┃ ┃ ┣ File.java
 ┃ ┃ ┃ ┃ ┣ Employee.java
 ┃ ┃ ┃ ┃ ┣ Department.java
 ┃ ┃ ┃ ┃ ┗Enum
 ┃ ┃ ┃ ┃   ┣BackupStatus.java
 ┃ ┃ ┃ ┃   ┣ChangeLogType.java
 ┃ ┃ ┃ ┃   ┗EmployeeStatus.java
 ┃ ┃ ┃ ┣ mapper
 ┃ ┃ ┃ ┃ ┣ BackupMapper.java
 ┃ ┃ ┃ ┃ ┣ ChangeLogMapper.java
 ┃ ┃ ┃ ┃ ┣ FileMapper.java
 ┃ ┃ ┃ ┃ ┣ EmployeeMapper.java
 ┃ ┃ ┃ ┃ ┗ DepartmentMapper.java
 ┃ ┃ ┃ ┣ exception
 ┃ ┃ ┃ resources
 ┃ ┃ ┃ ┣ application.properties
 ┃ ┃ ┃ ┗ static
 ┃ ┃ ┃ ┃ ┣ css
 ┃ ┃ ┃ ┃ ┃ ┗ style.css
 ┃ ┃ ┃ ┃ ┣ js
 ┃ ┃ ┃ ┃ ┃ ┗ script.js
 ┃ ┃ ┃ webapp
 ┃ ┃ ┃ ┣ WEB-INF
 ┃ ┃ ┃ ┃ ┗ web.xml
 ┃ ┃ ┃ test
 ┃ ┃ ┃ ┣ java
 ┃ ┃ ┃ ┃ ┣ com
 ┃ ┃ ┃ ┃ ┃ ┣ example
 ┃ ┃ ┃ ┃ ┃ ┗ ApplicationTests.java
 ┃ ┃ ┃ resources
 ┃ ┃ ┃ ┣ application.properties
 ┃ ┃ ┃ ┗ static
 ┃ ┃ ┃ ┃ ┣ css
 ┃ ┃ ┃ ┃ ┃ ┗ style.css
 ┃ ┃ ┃ ┃ ┣ js
 ┃ ┃ ┃ ┃ ┃ ┗ script.js
 ┣ pom.xml
 ┣ Application.java
 ┣ application.properties
 ┣ .gitignore
 ┗ README.md
```
----------------
## 구현 홈페이지
https://sb01-hrbank-team09-production.up.railway.app/
--------------
## 프로젝트 회고록
노션???