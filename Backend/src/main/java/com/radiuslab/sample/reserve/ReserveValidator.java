package com.radiuslab.sample.reserve;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

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

		List<Reserve> list = this.reserveRepository.findByReserveDateAndRoomId(dto.getReserveDate(), dto.getRoomId());
		for (Reserve r : list) {
			if (dto.getStartTime().isAfter(r.getStartTime()) && dto.getStartTime().isBefore(r.getEndTime())) {
				errors.rejectValue("startTime", "OverlapTime", "다른 예약과 겹치는 시간 입니다.");
			}
			if (dto.getEndTime().isAfter(r.getStartTime()) && dto.getEndTime().isBefore(r.getEndTime())) {
				errors.rejectValue("endTime", "OverlapTime", "다른 예약과 겹치는 시간 입니다.");
			}
			if (errors.hasErrors()) {
			}
		}
	}
}
