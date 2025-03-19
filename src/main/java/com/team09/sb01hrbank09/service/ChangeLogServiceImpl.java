package com.team09.sb01hrbank09.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team09.sb01hrbank09.dto.entityDto.ChangeLogDto;
import com.team09.sb01hrbank09.dto.entityDto.DiffDto;
import com.team09.sb01hrbank09.dto.request.CursorPageRequestChangeLog;
import com.team09.sb01hrbank09.dto.response.CursorPageResponseChangeLogDto;
import com.team09.sb01hrbank09.entity.ChangeLog;
import com.team09.sb01hrbank09.mapper.ChangeLogMapper;
import com.team09.sb01hrbank09.repository.ChangeLogRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChangeLogServiceImpl implements ChangeLogServiceInterface {

	private final ChangeLogRepository changeLogRepository;
	private final ChangeLogMapper changeLogMapper;

	@Override
	@Transactional(readOnly = true)
	public CursorPageResponseChangeLogDto findChangeLogList(CursorPageRequestChangeLog request) {
		log.info("findChangeLogList() 호출, request: {}", request);

		// 정렬 필드 검증 (허용된 필드만 사용)
		// 허용된 필드("at", "id", "ipAddress") 중 하나인지 확인하고, 아니라면 기본값 "at"으로 설정
		String sortField = validateSortField(request.sortField());
		log.info("정렬 필드 검증 완료, 사용된 정렬 필드: {}", sortField);

		// 정렬 방식 설정
		// 요청된 정렬 방향이 "desc"면 내림차순 정렬, 그 외에는 오름차순 정렬 적용
		String sortDirection = validateSortDirection(request.sortDirection());
		log.info("정렬 방향: {}", sortDirection);

		// 조건에 맞는 ChangeLog 목록 조회
		List<ChangeLog> changeLogs = getChangeLogs(request, sortDirection);
		log.info("조회된 데이터 수: {}", changeLogs.size());

		// ChangeLog 엔티티를 ChangeLogDto로 변환
		List<ChangeLogDto> dtos = changeLogs.stream()
			.map(log -> new ChangeLogDto(
				log.getId(), log.getType(), log.getEmployeeNumber(), log.getMemo(), log.getIpAddress(), log.getAt()
			))
			.toList();

		// 커서 페이지 응답 생성
		return toCursorPageResponse(dtos, request);
	}

	/**
	 * 정렬 필드 검증 메서드
	 * 요청된 정렬 필드가 허용된 필드 목록에 포함되어 있는지 확인하고,
	 * 포함되지 않은 경우 기본 정렬 필드("at")를 반환
	 */
	private String validateSortField(String sortField) {
		List<String> allowedFields = List.of("at", "ipAddress");  // 허용된 정렬 필드 목록

		// 필드가 허용된 목록에 포함되지 않으면 예외를 던짐
		if (!allowedFields.contains(sortField)) {
			throw new IllegalArgumentException(
				"Invalid sort field: " + sortField + ". Allowed fields are: " + allowedFields);
		}

		return sortField;
	}

	// 정렬 방향 검증 메서드
	private String validateSortDirection(String sortDirection) {
		// "asc" 또는 "desc"만 허용
		if (!"asc".equalsIgnoreCase(sortDirection) && !"desc".equalsIgnoreCase(sortDirection)) {
			throw new IllegalArgumentException(
				"Invalid sort direction: " + sortDirection + ". Allowed values are: 'asc' or 'desc'.");
		}

		return sortDirection;
	}

	private List<ChangeLog> getChangeLogs(CursorPageRequestChangeLog request, String sortDirection) {
		if ("asc".equalsIgnoreCase(sortDirection)) {
			return changeLogRepository.findChangeLogsAsc(
				request.employeeNumber(),
				request.memo(),
				request.ipAddress(),
				request.type(),
				request.atFrom(),
				request.atTo(),
				request.idAfter(),
				request.sortField()
			);
		} else {
			return changeLogRepository.findChangeLogsDesc(
				request.employeeNumber(),
				request.memo(),
				request.ipAddress(),
				request.type(),
				request.atFrom(),
				request.atTo(),
				request.idAfter(),
				request.sortField()
			);
		}
	}

	// responseMapper 내부 구현
	// 커서 페이지 응답 생성
	private CursorPageResponseChangeLogDto toCursorPageResponse(List<ChangeLogDto> dtos,
		CursorPageRequestChangeLog request) {
		Long nextIdAfter = null;
		String nextCursor = null;
		boolean hasNext = false;

		// dtos가 비어있지 않으면, 마지막 항목의 id를 다음 커서로 설정
		if (!dtos.isEmpty()) {
			nextIdAfter = dtos.get(dtos.size() - 1).id();
			nextCursor = String.valueOf(nextIdAfter);

			// 다음 페이지의 데이터가 있는지 확인
			List<ChangeLog> nextChangeLogs = getChangeLogs(
				CursorPageRequestChangeLog.copy(request, nextIdAfter, nextCursor), request.sortDirection());
			hasNext = !nextChangeLogs.isEmpty();
		}

		// 전체 데이터 수 계산
		Long totalCount = changeLogRepository.countChangeLogs(request.employeeNumber(), request.memo(),
			request.ipAddress(), request.type(), request.atFrom(), request.atTo());

		// CursorPageResponseChangeLogDto 생성
		return new CursorPageResponseChangeLogDto(
			dtos,
			nextCursor,
			nextIdAfter,
			request.size(),  // 요청받은 페이지 크기
			totalCount,  // 전체 항목 수
			hasNext  // 다음 페이지 존재 여부
		);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DiffDto> findChangeLogById(Long id) {
		log.info("findChangeLogById() 호출, id: {}", id);
		// 이력 조회
		ChangeLog changeLog = changeLogRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("ChangeLog not found for id: " + id));

		// before, after JSON 비교하여 변경된 필드들 반환
		List<DiffDto> diffs = compareDiffs(changeLog.getBefore(), changeLog.getAfter());

		// 필드의 순서를 정해주기 위한 기준 정의 (정확한 필드 순서)
		List<String> fieldOrder = List.of(
			"hireDate", "name", "position", "department", "email", "employeeNumber", "status"
		);

		// propertyName을 기준으로 필터링하여 순서대로 정렬
		diffs.sort(Comparator.comparingInt(diff -> fieldOrder.indexOf(diff.propertyName())));
		log.info("변경된 필드 수: {}", diffs.size());
		return diffs;
	}

	@Override
	@Transactional(readOnly = true)
	public Long countChangeLog(Instant fromDate, Instant toDate) {
		log.info("countChangeLog() 호출, fromDate: {}, toDate: {}", fromDate, toDate);
		if (fromDate == null) {
			fromDate = Instant.now().minus(7, ChronoUnit.DAYS); // 기본값: 7일 전
			log.info("fromDate가 null, 기본값 설정됨: {}", fromDate);
		}
		if (toDate == null) {
			toDate = Instant.now(); // 기본값: 현재 시간
			log.info("toDate가 null, 기본값 설정됨: {}", toDate);
		}

		long count = changeLogRepository.countByAtBetween(fromDate, toDate);
		log.info("조회된 ChangeLog 개수: {}", count);
		return count;
	}

	private List<DiffDto> compareDiffs(String beforeJson, String afterJson) {
		List<DiffDto> diffs = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			// beforeJson 또는 afterJson이 빈 객체로 저장되었으므로, null일 경우 빈 객체로 취급
			Map<String, Object> beforeMap = (beforeJson != null && !beforeJson.isEmpty())
				? objectMapper.readValue(beforeJson, Map.class) : new HashMap<>();
			Map<String, Object> afterMap = (afterJson != null && !afterJson.isEmpty())
				? objectMapper.readValue(afterJson, Map.class) : new HashMap<>();

			// 모든 키를 비교
			Set<String> allKeys = new HashSet<>();
			allKeys.addAll(beforeMap.keySet());
			allKeys.addAll(afterMap.keySet());

			for (String key : allKeys) {
				Object beforeValue = beforeMap.get(key);
				Object afterValue = afterMap.get(key);

				// before와 after가 다르면 DiffDto에 추가
				if (!Objects.equals(beforeValue, afterValue)) {
					diffs.add(new DiffDto(key, String.valueOf(beforeValue), String.valueOf(afterValue)));
					log.info("변경된 필드: {}, before: {}, after: {}", key, beforeValue, afterValue);
				}
			}
		} catch (JsonProcessingException e) {
			throw new RuntimeException("JSON parsing error", e);
		}

		//변경된 부분이 없다면 빈 객체 반환 []
		if (diffs.isEmpty()) {
			log.info("변경된 부분이 없습니다.");
		}

		return diffs;
	}

}
