package com.radiuslab.sample.reserve;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.radiuslab.sample.room.Room;
import com.radiuslab.sample.room.RoomRepository;

@Service
public class ReserveService {
	@Autowired
	private ReserveRepository reserveRepository;

	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private ModelMapper modelMapper;

	public Reserve save(ReserveDto dto) {
		Reserve reserve = this.modelMapper.map(dto, Reserve.class);
//		// Optional<Room> room = this.roomRepository.findById(dto.getRoomId());
//		Room room = new Room();
//		room.setRoomId(dto.getRoomId());
//		// reserve.setRoom(room.get());
//
//		reserve.setRoom(room);
		Reserve res = this.reserveRepository.save(reserve);
		return res;
	}
  
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
