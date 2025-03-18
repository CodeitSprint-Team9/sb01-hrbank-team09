package com.team09.sb01hrbank09.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.team09.sb01hrbank09.entity.ChangeLog;
import com.team09.sb01hrbank09.repository.ChangeLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeEventListener {

	private final ChangeLogRepository changeLogRepository;

	@Async //비동기 처리
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleEmployeeEvent(EmployeeEvent event) {
		try {
			ChangeLog changeLog = ChangeLog.createChangeLog(
				event.getType(), event.getEmployeeNumber(), event.getIpAddress(), event.getMemo(),
				event.getBeforeEmployee(), event.getAfterEmployee()
			);
			changeLogRepository.save(changeLog);
		} catch (Exception e) {
			log.error("ChangeLog 저장 중 예외 발생: {}", e.getMessage(), e);
			// 예외 발생 시 다른 처리 (ex: 재시도 로직 추가 가능)
		}
	}
}
