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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
import jakarta.persistence.criteria.Predicate;
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
		Sort sort;
		if (request.sortDirection().equalsIgnoreCase("desc")) {
			sort = Sort.by(Sort.Order.desc(sortField));
			log.info("내림차순 정렬 적용");
		} else {
			sort = Sort.by(Sort.Order.asc(sortField));
			log.info("오름차순 정렬 적용");
		}

		// 페이지네이션 설정
		// 기본적으로 첫 번째 페이지(0번 페이지)에서 요청된 크기(request.size())만큼 데이터를 조회
		Pageable pageable = PageRequest.of(0, request.size(), sort);
		log.info("페이지네이션 설정 완료, size: {}, page: 0", request.size());

		// 검색 조건 설정
		Specification<ChangeLog> spec = (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			// 직원 번호(employeeNumber) 검색 (부분 일치)
			if (request.employeeNumber() != null) {
				predicates.add(criteriaBuilder.like(root.get("employeeNumber"), "%" + request.employeeNumber() + "%"));
				log.info("employeeNumber 필드로 검색, 값: {}", request.employeeNumber());
			}
			// 메모(memo) 검색 (부분 일치)
			if (request.memo() != null) {
				predicates.add(criteriaBuilder.like(root.get("memo"), "%" + request.memo() + "%"));
				log.info("memo 필드로 검색, 값: {}", request.memo());
			}
			// IP 주소(ipAddress) 검색 (부분 일치)
			if (request.ipAddress() != null) {
				predicates.add(criteriaBuilder.like(root.get("ipAddress"), "%" + request.ipAddress() + "%"));
				log.info("ipAddress 필드로 검색, 값: {}", request.ipAddress());
			}
			// 변경 유형(type) 검색 (완전 일치)
			if (request.type() != null) {
				predicates.add(criteriaBuilder.equal(root.get("type"), request.type()));
				log.info("type 필드로 검색, 값: {}", request.type());
			}
			// 변경 시간(at) 검색 (범위 조건)
			if (request.atFrom() != null && request.atTo() != null) {
				predicates.add(criteriaBuilder.between(root.get("at"), request.atFrom(), request.atTo()));
				log.info("at 필드로 범위 검색, from: {}, to: {}", request.atFrom(), request.atTo());
			} else if (request.atFrom() != null) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("at"), request.atFrom()));
				log.info("at 필드로 from 이후 검색, from: {}", request.atFrom());
			} else if (request.atTo() != null) {
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("at"), request.atTo()));
				log.info("at 필드로 to 이전 검색, to: {}", request.atTo());
			}

			// idAfter 기반 페이지네이션 적용
			if (request.idAfter() != null) {
				predicates.add(criteriaBuilder.greaterThan(root.get("id"), request.idAfter()));
				log.info("idAfter 조건 적용, idAfter: {}", request.idAfter());
			}

			// 모든 조건을 AND 연산자로 결합하여 최종 Predicate 반환
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

		// 커서 기반 페이지네이션 적용
		Page<ChangeLog> page;
		if (request.cursor() != null) {
			// cursor 값(이전 페이지의 마지막 at 값)을 Instant 타입으로 변환
			Instant cursorAt = Instant.parse(request.cursor());

			// 정렬 방향에 따라 "at" 필드 기준으로 커서 조건 추가
			// desc 정렬 시 cursor 보다 작은 데이터 조회
			// asc 정렬 시 cursor 보다 큰 데이터 조회
			spec = spec.and((root, query, criteriaBuilder) ->
				request.sortDirection().equalsIgnoreCase("desc")
					? criteriaBuilder.lessThan(root.get("at"), cursorAt)
					: criteriaBuilder.greaterThan(root.get("at"), cursorAt)
			);
			log.info("커서 기반 페이지네이션 적용, cursor: {}", request.cursor());
		}

		// 최종적으로 조건과 페이지네이션을 적용하여 데이터 조회
		page = changeLogRepository.findAll(spec, pageable);
		log.info("조회된 데이터 수: {}", page.getTotalElements());

		// 조회된 ChangeLog 엔티티 리스트를 ChangeLogDto 리스트로 변환
		// 여기에서 before,after는 필요가 없으므로 제외
		List<ChangeLogDto> dtos = page.getContent().stream()
			.map(log -> new ChangeLogDto(
				log.getId(), log.getType(), log.getEmployeeNumber(), log.getMemo(), log.getIpAddress(), log.getAt()
			))
			.toList();

		return toCursorPageResponse(page, dtos);
	}

	/**
	 * 정렬 필드 검증 메서드
	 * 요청된 정렬 필드가 허용된 필드 목록에 포함되어 있는지 확인하고,
	 * 포함되지 않은 경우 기본 정렬 필드("at")를 반환
	 */
	private String validateSortField(String sortField) {
		List<String> allowedFields = List.of("at", "ipAddress");
		if (!allowedFields.contains(sortField)) {
			log.warn("허용되지 않은 정렬 필드, 기본값 'at'로 설정됨");
			return "at";
		}
		return sortField;
	}

	// responseMapper 내부 구현
	private CursorPageResponseChangeLogDto toCursorPageResponse(Page<ChangeLog> page, List<ChangeLogDto> dtos) {
		String nextCursor =
			page.hasNext() ? String.valueOf(page.getContent().get(page.getContent().size() - 1).getId()) : null;
		Long nextIdAfter = page.hasNext() ? page.getContent().get(page.getContent().size() - 1).getId() : null;

		return new CursorPageResponseChangeLogDto(
			dtos,
			nextCursor,
			nextIdAfter,
			page.getSize(),
			page.getTotalElements(),
			page.hasNext()
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
