package com.radiuslab.sample.room;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
	// roomId만 가져옴
	public RoomId findByRoomId(Long roomId);
}
