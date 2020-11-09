package com.radiuslab.sample.reserve;

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
}
