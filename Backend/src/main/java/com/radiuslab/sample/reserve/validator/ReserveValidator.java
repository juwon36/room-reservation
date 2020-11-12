package com.radiuslab.sample.reserve.validator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.radiuslab.sample.reserve.Reserve;
import com.radiuslab.sample.reserve.ReserveDto;
import com.radiuslab.sample.reserve.ReserveRepository;
import com.radiuslab.sample.room.RoomRepository;

@Component
public class ReserveValidator {
	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private ReserveRepository reserveRepository;

	public void validate(ReserveDto dto, Errors errors) {
		if (this.roomRepository.findByRoomId(dto.getRoomId()) == null) {
			errors.rejectValue("roomId", "WrongRoomId", "예약하려는 회의실이 없는 회의실입니다.");
		}

		// TODO 나중에 고쳐라
		if (dto.getReserveId() != null) {
			Optional<Reserve> res = this.reserveRepository.findById(dto.getReserveId());
			if (res.isPresent()) {
				Reserve reserve = res.get();
				LocalDateTime current = LocalDateTime.now();

				LocalDate reserveDate = reserve.getReserveDate();
				if (reserveDate.isBefore(current.toLocalDate())) {
					errors.rejectValue("reserveDate", "PastDate", "지난 날짜는 수정할 수 없습니다.");
				}

				LocalDateTime startTime = reserve.getStartTime();
				if (startTime.isBefore(current)) {
					errors.rejectValue("startTime", "PastTime", "지난 시간은 수정할 수 없습니다.");
				}
			} else {
				errors.rejectValue("reserveId", "WrongReserveId", "수정하려는 예약이 없는 예약입니다.");
			}
		}

		List<Reserve> list = this.reserveRepository.findByReserveDateAndRoomId(dto.getReserveDate(), dto.getRoomId());
		for (Reserve r : list) {
			if (dto.getReserveId() != null && dto.getReserveId() == r.getReserveId()) {
				continue;
			}
			if (!dto.getStartTime().isBefore(r.getStartTime()) && !dto.getStartTime().isAfter(r.getEndTime())) {
				errors.rejectValue("startTime", "OverlapTime", "다른 예약과 겹치는 시간 입니다.");
			}
			if (!dto.getEndTime().isBefore(r.getStartTime()) && !dto.getEndTime().isAfter(r.getEndTime())) {
				errors.rejectValue("endTime", "OverlapTime", "다른 예약과 겹치는 시간 입니다.");
			}
			if (errors.hasErrors()) {
				break;
			}
		}
	}
}
