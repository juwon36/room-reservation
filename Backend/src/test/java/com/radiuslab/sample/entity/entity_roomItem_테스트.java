package com.radiuslab.sample.entity;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.radiuslab.sample.room.Room;
import com.radiuslab.sample.room.RoomRepository;
import com.radiuslab.sample.roomItem.RoomItem;
import com.radiuslab.sample.roomItem.RoomItemRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = { "spring.datasource.url=jdbc:postgresql://localhost:5432/radius_test" })
public class entity_roomItem_테스트 {
	protected static final Logger LOGGER = LoggerFactory.getLogger(entity_roomItem_테스트.class);

	@Autowired
	private RoomRepository roomRepository;
	
	@Autowired
	private RoomItemRepository roomItemRepository;

	@Test
	public void roomItem_저장_테스트() {
		// Room 생성
		Room room1 = new Room();
		room1.setRoomName("1회의실");
		roomRepository.save(room1);
		
		// RoomItem 생성 ... 어떻게 묶어서 반복시킬 수 없을까,,,
		List <RoomItem> roomItems = new ArrayList<>();
		
		RoomItem roomItem1 = RoomItem.builder().itemName("TV").itemNum(1).room(room1).build();
		room1.getItems().add(roomItem1);
		roomItemRepository.save(roomItem1);

		RoomItem roomItem2 = RoomItem.builder().itemName("의자").itemNum(6).room(room1).build();
		room1.getItems().add(roomItem2);
		roomItemRepository.save(roomItem2);

		RoomItem roomItem3 = RoomItem.builder().itemName("테이블").itemNum(1).room(room1).build();
		room1.getItems().add(roomItem3);
		roomItemRepository.save(roomItem3);

		
	}
}
