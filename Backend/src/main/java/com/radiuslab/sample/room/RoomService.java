package com.radiuslab.sample.room;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class RoomService {
	@Autowired
	private RoomRepository roomRepository;

	// value: 저장시 키 값, key : 키 생성시 추가로 덧붙일 파라미터 정보
	@Cacheable("Room")
	public List<Room> findAll() {
		List<Room> roomList = this.roomRepository.findAll();
		return roomList;
	}
}
