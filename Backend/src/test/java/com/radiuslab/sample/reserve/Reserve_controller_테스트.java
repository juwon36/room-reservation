package com.radiuslab.sample.reserve;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.util.NestedServletException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.radiuslab.sample.room.Room;
import com.radiuslab.sample.room.RoomRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = { "spring.datasource.url=jdbc:postgresql://localhost:5432/test" }) // @SpringBootTest에서 다른
																								// db를 사용하겠다
@AutoConfigureMockMvc
public class Reserve_controller_테스트 {
	protected static final Logger LOGGER = LoggerFactory.getLogger(Reserve_controller_테스트.class);

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext ctx;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private ReserveRepository reserveRepository;

	private Room room1, room2;

	@BeforeEach
	public void init() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).addFilters(new CharacterEncodingFilter("UTF-8", true))
				.alwaysDo(print()).build();
	}

	@BeforeEach
	public void 데이터_셋업() throws Exception {
		room1 = new Room();
		room1.setRoomName("1회의실");
		roomRepository.save(room1);

		room2 = new Room();
		room2.setRoomName("2회의실");
		roomRepository.save(room2);
	}
	
	@AfterEach
	public void 데이터_삭제() throws Exception{
		reserveRepository.deleteAll();
	}
	// juwon
	// 예약하기 테스트

	// 예약수정 테스트

	// ------------------------------

	// gyuwoon

	// 에약 전체 조회
	@Test
	public void 전체_예약_조회_성공() {
		String strDate = "2020-11-08";
		LocalDate date = LocalDate.parse(strDate);
		String strDate2 = "2020-11-10";
		LocalDate date2 = LocalDate.parse(strDate2);
		String strDate3 = "2020-10-30";
		LocalDate date3 = LocalDate.parse(strDate3);

		Reserve reserve = Reserve.builder().userName("정겨운").reserveDate(date).build();
		Reserve reserve2 = Reserve.builder().userName("정주원").reserveDate(date2).build();
		Reserve reserve3 = Reserve.builder().userName("봉하선").reserveDate(date3).build();

		reserveRepository.save(reserve);
		reserveRepository.save(reserve2);
		reserveRepository.save(reserve3);

		List<Reserve> reserveList = reserveRepository.findAll();
		for (Reserve r : reserveList) {
			LOGGER.info("Reserve");
			LOGGER.info("Reserve ID: " + r.getReserveId());
			LOGGER.info("Reserve userName: " + r.getUserName());
			LOGGER.info("Reserve reserveDate: " + r.getReserveDate());
		}
	}

	@Test
	public void 메인_조회_성공() throws Exception {
		// 당일날짜를 받으면 해당일의 회의실 전체 예약 리스트를 보낸다
		String strDate = "2020-11-10";
		LocalDate date = LocalDate.parse(strDate);
		String strDate2 = "2020-11-10";
		LocalDate date2 = LocalDate.parse(strDate2);

		Reserve reserve = Reserve.builder().userName("정겨운").reserveDate(date).room(room1).build();
		Reserve reserve2 = Reserve.builder().userName("정주원").reserveDate(date2).room(room2).build();

		reserveRepository.save(reserve);
		reserveRepository.save(reserve2);

		mockMvc.perform(get("/api/reserve/2020-11-10/all")).andDo(print()).andExpect(status().isOk());
	}

	// 실패 : 날짜를 안보낼때 -> 404에러
	@Test
	@DisplayName(value = "메인 조회 실패 - 날짜 미입력")
	public void 메인_조회_실패() throws Exception {

			Reserve reserve = Reserve.builder().userName("정겨운").reserveDate(LocalDate.of(2020, 11, 10)).room(room1)
					.build();

			reserveRepository.save(reserve);

			// 안하면 컴파일 오류,,, 뭐죠
			//Assertions.assertThrows(NestedServletException.class, () -> {
			mockMvc.perform(get("/api/reserve")).andDo(print()).andExpect(status().isBadRequest());
		//});
	}

	// 날짜에 해당하는 예약리스트가 없을때 -> 200 (빈 리스트를 보낸다)
	@DisplayName(value = "메인 조회 성공 - 빈 리스트 리턴")
	@ParameterizedTest
	@ValueSource(strings = {"2019-11-10", "2020-02-10", "2020-11-01"})
	public void 메인_조회_성공_예약없음(String inputDate)throws Exception{
		Reserve reserve = Reserve.builder().userName("정겨운").reserveDate(LocalDate.parse(inputDate)).room(room1).build();
		
		reserveRepository.save(reserve);
		
		mockMvc.perform(get("/api/reserve/2020-11-10/all")).andDo(print()).andExpect(status().isOk()).andExpect(content().string("[]"));
	}

	@Test
	public void 월간_조회_성공() throws Exception {
		// 월간보기(캘린더)로 조회시 해당년월과 회의실id에 맞는 예약현황을 리턴한다.
		String strDate = "2020-11-08";
		LocalDate date = LocalDate.parse(strDate);
		String strDate2 = "2020-11-10";
		LocalDate date2 = LocalDate.parse(strDate2);
		String strDate3 = "2020-10-30";
		LocalDate date3 = LocalDate.parse(strDate3);

		Reserve reserve = Reserve.builder().userName("정겨운").reserveDate(date).room(room1).build();
		Reserve reserve2 = Reserve.builder().userName("정주원").reserveDate(date2).room(room2).build();
		Reserve reserve3 = Reserve.builder().userName("봉하선").reserveDate(date3).room(room1).build();

		reserveRepository.save(reserve);
		reserveRepository.save(reserve2);
		reserveRepository.save(reserve3);

		mockMvc.perform(get("/api/reserve/").param("roomId", "1").param("year", "2020").param("month", "11"))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void 당일_조회_성공() throws Exception {
		// 예약시 상세 조회(당일, 회의실id를 보내면 예약 현황 리턴한다)
		LocalDate date = LocalDate.parse("2020-11-08");// 예약날짜
		LocalDateTime startTime = LocalDateTime.parse("2020-11-08T10:00:00");
		LocalDateTime endTime = LocalDateTime.parse("2020-11-08T11:30:00");
		LocalDateTime startTime2 = LocalDateTime.parse("2020-11-08T15:00:00");
		LocalDateTime endTime2 = LocalDateTime.parse("2020-11-08T18:00:00");

		Reserve reserve = Reserve.builder().userName("정겨운").reserveDate(date).startTime(startTime).endTime(endTime)
				.room(room1).build();
		Reserve reserve2 = Reserve.builder().userName("정주원").reserveDate(date).startTime(startTime2).endTime(endTime2)
				.room(room1).build();

		reserveRepository.save(reserve);
		reserveRepository.save(reserve2);

		mockMvc.perform(get("/api/reserve/" + "2020-11-08/" + "1")).andDo(print()).andExpect(status().isOk());
	}
	
	// 일간조회 성공 - 아무예약이 없으면 빈 리스트 리턴
	@ParameterizedTest
	@ValueSource(strings = {"2019-11-10", "2020-02-10", "2020-11-01"})
	@DisplayName(value = "상세 조회 성공 - 빈 리스트 리턴")
	public void 당일_조회_성공_빈리스트(String input_date) throws Exception {
		LocalDate date = LocalDate.parse(input_date);// 예약날짜
		LocalDateTime startTime = LocalDateTime.parse("2020-11-08T10:00:00");
		LocalDateTime endTime = LocalDateTime.parse("2020-11-08T11:30:00");
		
		Reserve reserve = Reserve.builder().userName("정겨운").reserveDate(date).startTime(startTime).endTime(endTime)
				.room(room1).build();
	
		reserveRepository.save(reserve);

		mockMvc.perform(get("/api/reserve/" + "2020-11-08/" + "1")).andDo(print()).andExpect(status().isOk()).andExpect(content().string("[]"));
	}
	
	// 일간조회 실패 - 없는 회의실 id를 호출한 경우
	@ParameterizedTest
	@ValueSource(strings = {"5", "-1"})
	public void 당일_조회_실패_roomId(String input) throws Exception{
		LocalDate date = LocalDate.parse("2020-11-08");// 예약날짜
		LocalDateTime startTime = LocalDateTime.parse("2020-11-08T10:00:00");
		LocalDateTime endTime = LocalDateTime.parse("2020-11-08T11:30:00");
		LocalDateTime startTime2 = LocalDateTime.parse("2020-11-08T15:00:00");
		LocalDateTime endTime2 = LocalDateTime.parse("2020-11-08T18:00:00");

		Reserve reserve = Reserve.builder().userName("정겨운").reserveDate(date).startTime(startTime).endTime(endTime)
				.room(room1).build();
		Reserve reserve2 = Reserve.builder().userName("정주원").reserveDate(date).startTime(startTime2).endTime(endTime2)
				.room(room1).build();

		reserveRepository.save(reserve);
		reserveRepository.save(reserve2);

		mockMvc.perform(get("/api/reserve/" + "2020-11-08/"+input)).andDo(print()).andExpect(status().isNotFound());
	}

	// 예약취소 테스트

	// 비밀번호 확인

}
