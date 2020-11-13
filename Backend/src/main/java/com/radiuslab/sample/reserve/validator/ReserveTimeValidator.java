package com.radiuslab.sample.reserve.validator;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.radiuslab.sample.reserve.ReserveDto;

@Component
public class ReserveTimeValidator {
	public void validate(ReserveDto dto, Errors errors) {
		LocalDateTime current = LocalDateTime.now();

		LocalDate reserveDate = dto.getReserveDate();
		if (reserveDate.isBefore(current.toLocalDate())) {
			errors.rejectValue("reserveDate", "PastDate", "지난 날짜는 예약할 수 없습니다.");
		}

		LocalDateTime startTime = dto.getStartTime();
		LocalDateTime endTime = dto.getEndTime();
		if (startTime.isBefore(current)) {
			errors.rejectValue("startTime", "PastTime", "지난 시간은 예약할 수 없습니다.");
		}
		if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
			errors.rejectValue("endTime", "WrongTime", "종료 시간이 시작 시간보다 빠르거나 같을 수 없습니다.");
		}

		if (!startTime.toLocalDate().isEqual(endTime.toLocalDate())) {
			errors.rejectValue("startTime", "WrongDate", "시작 시간과 종료 시간의 날짜가 다를 수 없습니다.");
			errors.rejectValue("endTime", "WrongDate", "시작 시간과 종료 시간의 날짜가 다를 수 없습니다.");
		}

		if (startTime.toLocalTime().getMinute() % 30 != 0) {
			errors.rejectValue("startTime", "WrongTime", "예약은 30분 단위로만 가능합니다.");
		}
		if (endTime.toLocalTime().getMinute() % 30 != 0) {
			errors.rejectValue("endTime", "WrongTime", "예약은 30분 단위로만 가능합니다.");
		}
	}
}
