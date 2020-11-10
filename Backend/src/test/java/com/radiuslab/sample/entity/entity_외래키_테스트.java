package com.radiuslab.sample.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.radiuslab.sample.reserve.Reserve;
import com.radiuslab.sample.reserve.ReserveDto;
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

	@Test
	public void modelMapper_테스트() throws Exception {
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com").userPassword("0306").userNum(5).title("스터디 회의")//
				.reserveDate(LocalDate.of(2020, 11, 19)).startTime(LocalDateTime.of(2020, 11, 19, 15, 00)).endTime(LocalDateTime.of(2020, 11, 19, 16, 00))//
				.build();
		Reserve entity = new ModelMapper().map(dto, Reserve.class);
		LOGGER.info(entity.getRoom().toString());
		LOGGER.info(entity.getRoom().getRoomId().toString());
	}

	@Test
	public void test() throws Exception {
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com").userPassword("0306").userNum(5).title("스터디 회의")//
				.reserveDate(LocalDate.of(2020, 11, 19)).startTime(LocalDateTime.of(2020, 11, 19, 7, 5, 5)).endTime(LocalDateTime.of(2020, 11, 19, 21, 45, 53))//
				.build();
		LOGGER.info(dto.toString());
		dto.update();
		LOGGER.info(dto.toString());
		
		ReserveDto dto2 = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com").userPassword("0306").userNum(5).title("스터디 회의")//
				.reserveDate(LocalDate.of(2020, 11, 20)).startTime(LocalDateTime.of(2020, 11, 20, 10, 5, 5)).endTime(LocalDateTime.of(2020, 11, 20, 11, 45, 53))//
				.build();
		LOGGER.info(dto2.toString());
		dto2.update();
		LOGGER.info(dto2.toString());
	}
}
