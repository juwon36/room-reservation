package com.radiuslab.sample.room;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.radiuslab.sample.roomItem.RoomItem;
import com.radiuslab.sample.roomItem.RoomItemRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = { "spring.datasource.url=jdbc:postgresql://localhost:5432/test" })
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
public class Room_controller_테스트 {
	protected static final Logger LOGGER = LoggerFactory.getLogger(Room_controller_테스트.class);

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext ctx;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private RoomItemRepository roomItemRepository;
	
	@Autowired
	private RoomService roomService;

	private long startTime; // 캐시 테스트용

	private long endTime;

	@Transactional
	@BeforeAll
	public void 회의실과_비품_데이터_셋업() {
		String[] roomName = { "오픈형 회의실", "1회의실", "2회의실", "대회의실" };
		int[] roomNum = { 11, 6, 6, 23 };
		List<Room> list = new ArrayList<>();
		for (int i = 0; i < roomName.length; i++) {
			Room room = Room.builder().roomName(roomName[i]).capacity(roomNum[i]).build();
			list.add(room);
		}
		roomRepository.saveAll(list);

		String[] itemName = { "테이블", "좌석", "TV", "HDMI선", "콘센트(구)", "USB 포트", "보드", "랜선" };
		int[][] itemNum = { { 2, 7, 0, 0, 4, 0, 1, 0 }, { 1, 6, 1, 0, 3, 0, 1, 0 }, { 1, 6, 1, 0, 4, 1, 1, 1 },
				{ 2, 13, 1, 1, 8, 1, 1, 1 } };
		List<RoomItem> items = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			for (int j = 0; j < itemName.length; j++) {
				RoomItem item = new RoomItem();
				item.setItemName(itemName[j]);
				item.setItemNum(itemNum[i][j]);
				list.get(i).getItems().add(item);
				item.setRoom(list.get(i));
				items.add(item);
			}
		}
		roomItemRepository.saveAll(items);

//		LOGGER.info("==================== roomRepository.findAll");
//		for (Room r : this.roomRepository.findAll()) {
//			LOGGER.info("Room[id: " + r.getRoomId() + ", name: " + r.getRoomName() + ", capacity: " + r.getCapacity()
//					+ "]");
//			for (RoomItem i : r.getItems()) {
//				LOGGER.info("RoomItem[id: " + i.getItemId() + ", name: " + i.getItemName() + ", num: " + i.getItemNum()
//						+ ", room id: " + i.getRoom().getRoomId() + "]");
//			}
//			LOGGER.info("=============================\n");
//		}
	}

	@BeforeEach
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).addFilters(new CharacterEncodingFilter("UTF-8", true))
//				.alwaysDo(print())
				.build();

		// 시간체크
		startTime = System.currentTimeMillis();
	}

	@AfterEach
	public void 캐시테스트용_시간체크() {
		endTime = System.currentTimeMillis();
		LOGGER.info("소요시간: "+ (endTime - startTime) + "ms");
	}
	
	@Test
	public void 회의실_조회_service_테스트() {
		List<Room> room = roomService.findAll();
		System.out.printf("roomSize: %d \n", room.size());
		assertEquals(4, room.size());
		// 실행하면 출력은 세 개가 나오지만 캐싱이 적용되었기 때문에 select문은 출력되지 않는다.
		System.out.println(roomService.findAll());
		System.out.println(roomService.findAll());
		System.out.println(roomService.findAll());
	}

	
	@RepeatedTest(5)
	public void 회의실_조회_테스트() throws Exception {

		mockMvc.perform(//
				get("/api/room"))//
//				.andDo(MockMvcResultHandlers.print())//
				.andExpect(status().isOk());//
	}

}
