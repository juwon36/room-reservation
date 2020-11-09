package com.radiuslab.sample.reserve;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.radiuslab.sample.room.Room;

@Service
public class ReserveService {
	static final Logger LOGGER = LoggerFactory.getLogger(ReserveService.class);
	
	@Autowired
	private ReserveRepository reserveRepository;

	public List<Reserve> findByReserveDate(String reserveDate) {
		LocalDate date = LocalDate.parse(reserveDate );
		List<Reserve> reserveList = this.reserveRepository.findByReserveDate(date);
		return reserveList;
	}


	public List<Reserve> findByRoomIdAndYearMonth(String roomId, String year, String month) {
		Long longRoomId = (long) Integer.parseInt(roomId);
		List<Reserve> reserveList = this.reserveRepository.findAllByRoomIdAndYearMonth(longRoomId, year, month);
		return reserveList;
	}


	public List<Reserve> findByReserveDateAndRoomId(String reserveDate, Long roomId) {
		LocalDate date = LocalDate.parse(reserveDate);
		List<Reserve> reserveList = this.reserveRepository.findByReserveDateAndRoomId(date, roomId);
		return reserveList;
	}

}
