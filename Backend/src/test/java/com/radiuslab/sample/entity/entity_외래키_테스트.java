package com.radiuslab.sample.entity;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.radiuslab.sample.reserve.Reserve;
import com.radiuslab.sample.reserve.ReserveRepository;
import com.radiuslab.sample.room.Room;
import com.radiuslab.sample.room.RoomRepository;
import com.radiuslab.sample.roomItem.RoomItem;
import com.radiuslab.sample.roomItem.RoomItemRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = { "spring.datasource.url=jdbc:postgresql://localhost:5432/radius_test" })
public class entity_외래키_테스트 {
	protected static final Logger LOGGER = LoggerFactory.getLogger(entity_외래키_테스트.class);

	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private RoomItemRepository roomItemRepository;

	@Autowired
	private ReserveRepository reserveRepository;

	@Test
	public void room_roomItem_join_저장_테스트() {
		// 회의실과 아이템을 세트로 넣으면 회의실의 id가 아이템 갯수만큼 떠버린다? 어째서??
		// 그래서 회의실을 먼저 DB에 다 저장을 하고 아이템을 저장해야 한다?? 왜???
		// @GeneratedValue(strategy = GenerationType.IDENTITY) 이걸 붙이니까 원하는 값이 나오긴 하는데 이해 필요

		Room room = new Room();
		room.setRoomName("1회의실");
		roomRepository.save(room);

		RoomItem item1 = new RoomItem();
		item1.setItemName("TV");
		item1.setItemNum(1);
		RoomItem item2 = new RoomItem();
		item2.setItemName("보드");
		item2.setItemNum(1);

		room.getItems().add(item1);
		room.getItems().add(item2);
		item1.setRoom(room);
		item2.setRoom(room);
		roomItemRepository.save(item1);
		roomItemRepository.save(item2);

		Room room2 = new Room();
		room2.setRoomName("2회의실");
		roomRepository.save(room2);

		RoomItem item2_1 = new RoomItem();
		item2_1.setItemName("TV");
		item2_1.setItemNum(2);
		RoomItem item2_2 = new RoomItem();
		item2_2.setItemName("보드");
		item2_2.setItemNum(2);

		room2.getItems().add(item2_1);
		room2.getItems().add(item2_2);
		item2_1.setRoom(room2);
		item2_2.setRoom(room2);

		roomItemRepository.save(item2_1);
		roomItemRepository.save(item2_2);
	}

	@Test
	@Transactional
	public void room_roomItem_join_조회_테스트() {
		List<Room> roomList = roomRepository.findAll();
		for (Room r : roomList) {
			LOGGER.info("Room");
			LOGGER.info("Room ID: " + r.getRoomId());
			for (RoomItem i : r.getItems()) {
				LOGGER.info("?!!");
				LOGGER.info("item id : " + i.getItemId());
				LOGGER.info("item name : " + i.getItemName());
				LOGGER.info("item num : " + i.getItemNum());
			}
		}
	}

	@Test
	public void room_reserve_join_저장_테스트() {
		Room room = new Room();
		room.setRoomName("3회의실");
		roomRepository.save(room);

		Reserve reserve = new Reserve();
		reserve.setTitle("2팀 회의");
		reserve.setRoom(room);
		reserveRepository.save(reserve);

		List<Room> roomList = roomRepository.findAll();
		for (Room r : roomList) {
			LOGGER.info("Room");
			LOGGER.info("Room ID: " + r.getRoomId());
			LOGGER.info("Room ID: " + r.getRoomName());
		}

		List<Reserve> reserveList = reserveRepository.findAll();
		for (Reserve r : reserveList) {
			LOGGER.info("Reserve");
			LOGGER.info("Reserve ID: " + r.getReserveId());
			LOGGER.info("Reserve ID: " + r.getTitle());
			LOGGER.info("Room ID: " + r.getRoom().getRoomId());
		}
	}

	// @Test
	// public void room_reserve_join_조회_테스트() {
	// List<Reserve> roomList = reserveRepository.findAll();
	// for (Reserve r : roomList) {
	// LOGGER.info("Room");
	// LOGGER.info("Room ID: " + r.getReserveId());
	// LOGGER.info("Room ID: " + r.getTitle());
	// }
	// }
}
