package com.radiuslab.sample.reserve;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.radiuslab.sample.room.Room;
import com.radiuslab.sample.room.RoomRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = { "spring.datasource.url=jdbc:postgresql://localhost:5432/radius_test" })
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
public class Reserve_controller_테스트 {
	protected static final Logger LOGGER = LoggerFactory.getLogger(Reserve_controller_테스트.class);

	private String API_URL = "/api/reserve";

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext ctx;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private ReserveRepository reserveRepository;

	@Autowired
	private ReserveService reserveService;

	private Room room1, room2;

	@BeforeAll
	public void 데이터_셋업() throws Exception {
		List<Room> list = new ArrayList<>();
		for (int i = 1; i < 5; i++) {
			Room room = Room.builder().roomName(i + "회의실").capacity(i * 3).build();
			list.add(room);
		}
		this.roomRepository.saveAll(list);
		this.room1 = list.get(0);
		this.room2 = list.get(1);
	}

	@BeforeEach
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).addFilters(new CharacterEncodingFilter("UTF-8", true)) // 한글
																														// 깨짐
																														// 방지
																														// 필터
																														// 추가
				.alwaysDo(print()) // 항상 내용 출력
				.build();

		for (Room r : this.roomRepository.findAll()) {
			LOGGER.info(r.getRoomId().toString() + " : " + r.getRoomName());
		}
	}

	@AfterEach
	public void delete() {
		this.reserveRepository.deleteAll();
		// this.roomRepository.deleteAll();
	}

	// juwon
	// 예약하기 테스트
	// - 예약 성공
	@Test
	@DisplayName("예약하기 성공")
	public void save_테스트() throws Exception {
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com")
				.userPassword("0306").userNum(5).title("스터디 회의").reserveDate(LocalDate.of(2020, 11, 19))
				.startTime(LocalDateTime.of(2020, 11, 19, 15, 00)).endTime(LocalDateTime.of(2020, 11, 19, 16, 00))
				.build();

		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(dto)))//
				.andExpect(status().isCreated())//
				.andExpect(jsonPath("reserveId").exists());

		List<Reserve> list = this.reserveRepository.findAll();
		assertThat(list).isNotNull().hasSize(1);
		Reserve res = list.get(0);
		// reserveId, room, endTime 외에 다른 필드는 전부 같은 값이여야 한다
		assertThat(res).isNotNull().isEqualToIgnoringGivenFields(dto, "reserveId", "room", "endTime");
		assertThat(res.getRoom().getRoomId()).isEqualTo(dto.getRoomId());
	}

	// - 데이터가 하나라도 누락(null이나 "")될 경우 → 400에러
	@Test
	@DisplayName("예약하기 Bad Request - 데이터 누락(null)")
	public void save_bad_request_input_null_테스트() throws Exception {
		ReserveDto dto = ReserveDto.builder().build();

		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(dto)))//
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("예약하기 Bad Request - 데이터 누락(empty)")
	public void save_bad_request_input_empty_테스트() throws Exception {
		ReserveDto dto = ReserveDto.builder().userName("").userEmail("").userPassword("").title("").build();

		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(dto)))//
				.andExpect(status().isBadRequest());
	}

	// - roomId
	// 1. 회의실테이블에 해당값이 없을 경우 → 예약 불가
	@Test
	@DisplayName("예약 불가 - roomId가 회의실테이블에 없을 경우")
	public void save_예약불가_roomId_테스트() throws Exception {
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(10)).userName("정주원").userEmail("juwon@gmail.com")
				.userPassword("0306").userNum(5).title("스터디 회의").reserveDate(LocalDate.of(2020, 11, 19))
				.startTime(LocalDateTime.of(2020, 11, 19, 15, 00)).endTime(LocalDateTime.of(2020, 11, 19, 16, 00))
				.build();

		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(dto)))//
				.andExpect(status().isConflict());
	}

	// - userEmail
	// 1. 이메일 형식이 아닐 경우 → 400에러
	@ParameterizedTest
	@DisplayName("예약하기 Bad Request - email")
	@ValueSource(strings = { "juwon12radiuslabcom", "juwonradiuslab.com" })
	public void save_bad_request_input_email_테스트(String email) throws Exception {
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(10)).userName("정주원").userEmail(email)
				.userPassword("0306").userNum(5).title("스터디 회의").reserveDate(LocalDate.of(2020, 11, 19))
				.startTime(LocalDateTime.of(2020, 11, 19, 15, 00)).endTime(LocalDateTime.of(2020, 11, 19, 16, 00))
				.build();

		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(dto)))//
				.andExpect(status().isBadRequest());
	}

	// - userPassword
	// 1. 4글자 보다 작은 경우 → 400에러
	@Test
	@DisplayName("예약하기 Bad Request - password")
	public void save_bad_request_input_password_테스트() throws Exception {
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com")
				.userPassword("as1").userNum(5).title("스터디 회의").reserveDate(LocalDate.of(2020, 11, 19))
				.startTime(LocalDateTime.of(2020, 11, 19, 15, 00)).endTime(LocalDateTime.of(2020, 11, 19, 16, 00))
				.build();

		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(dto)))//
				.andExpect(status().isBadRequest());
	}

	// - userNum
	// 1. 0보다 작은 값이 들어왔을 경우 → 400에러
	@Test
	@DisplayName("예약하기 Bad Request - userNum")
	public void save_bad_request_input_userNum_테스트() throws Exception {
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com")
				.userPassword("0306").userNum(-1).title("스터디 회의")//
				.reserveDate(LocalDate.of(2020, 11, 19)).startTime(LocalDateTime.of(2020, 11, 19, 15, 00))
				.endTime(LocalDateTime.of(2020, 11, 19, 16, 00))//
				.build();

		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(dto)))//
				.andExpect(status().isBadRequest());
	}

	// - reserveDate
	// 1. 포멧(yyyy-MM-dd)이 맞지 않을 경우 → 400에러 -> 일단 패스

	// 2. 현재 날짜 이전인 경우 → 예약 불가
	@ParameterizedTest
	@DisplayName("예약 불가 - 현재 날짜 이전")
	@ValueSource(strings = { "2020-11-01", "2020-10-30" })
	public void save_예약불가_reserveDate_테스트(LocalDate reserveDate) throws Exception {
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com")
				.userPassword("0306").userNum(5).title("스터디 회의").reserveDate(reserveDate)
				.startTime(LocalDateTime.of(2020, 11, 19, 15, 00)).endTime(LocalDateTime.of(2020, 11, 19, 16, 00))
				.build();

		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(dto)))//
				.andExpect(status().isConflict());
	}

	// - startTime, endTime
	// 1. 포멧(yyyy-MM-dd HH:mm:ss)이 맞지 않을 경우 → 400에러 -> 일단 패스

	// 2. 현재 시간 이전일 경우 → 예약 불가
	@ParameterizedTest
	@DisplayName("예약 불가 - 현재 시간 이전")
	@ValueSource(strings = { "2020-11-09T10:30:00", "2020-11-09T11:30:00" })
	public void save_예약불가_startTime_테스트(LocalDateTime startTime) throws Exception {
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com")
				.userPassword("0306").userNum(5).title("스터디 회의").reserveDate(LocalDate.of(2020, 11, 19))
				.startTime(startTime).endTime(LocalDateTime.of(2020, 11, 19, 16, 00)).build();

		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(dto)))//
				.andExpect(status().isConflict());
	}

	// 3. 같은 회의실의 다른 예약과 겹칠 경우 → 예약 불가
	@ParameterizedTest
	@DisplayName("예약 불가 - 다른 예약과 겹칠 경우")
	@ValueSource(strings = { "2020-11-19T12:00:00", "2020-11-19T13:30:00", "2020-11-19T15:00:00" }) // 종료시간 겹침, 포함, 시작시간
																									// 겹침
	public void save_예약불가_예약_시간_겹침_테스트(LocalDateTime startTime) throws Exception {
		// 비교할 예약 2020-11-19 1시부터 4시까지
		Reserve reserve = Reserve.builder().room(room1).userName("정주원").userEmail("juwon@gmail.com")
				.userPassword("0306").userNum(5).title("스터디 회의").reserveDate(LocalDate.of(2020, 11, 19))
				.startTime(LocalDateTime.of(2020, 11, 19, 13, 00)).endTime(LocalDateTime.of(2020, 11, 19, 16, 00))
				.build();
		this.reserveRepository.save(reserve);

		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com")
				.userPassword("0306").userNum(5).title("스터디 회의").reserveDate(LocalDate.of(2020, 11, 19))
				.startTime(startTime).endTime(startTime.plusHours(2)).build();

		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(dto)))//
				.andExpect(status().isConflict());
	}

	// 3-1. 성공인 경우
	@ParameterizedTest
	@DisplayName("예약 성공 - 경계 시간")
	@ValueSource(strings = { "2020-11-19T11:00:00", "2020-11-19T16:00:00" }) // 종료시간 겹침, 포함, 시작시간 겹침
	public void save_예약_시간_겹침_테스트(LocalDateTime startTime) throws Exception {
		// 비교할 예약 2020-11-19 1시부터 4시까지
		Reserve reserve = Reserve.builder().room(room1).userName("정주원").userEmail("juwon@gmail.com")
				.userPassword("0306").userNum(5).title("스터디 회의").reserveDate(LocalDate.of(2020, 11, 19))
				.startTime(LocalDateTime.of(2020, 11, 19, 13, 00)).endTime(LocalDateTime.of(2020, 11, 19, 16, 00))
				.build();
		this.reserveRepository.save(reserve);

		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com")
				.userPassword("0306").userNum(5).title("스터디 회의").reserveDate(LocalDate.of(2020, 11, 19))
				.startTime(startTime).endTime(startTime.plusHours(2)).build();

		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(dto)))//
				.andExpect(status().isCreated());
	}

	// 4. endTime이 startTime보다 빠른 시간일 경우 → 예약 불가
	@ParameterizedTest
	@DisplayName("예약 불가 - endTime이 startTime보다 빠른 시간일 경우")
	@ValueSource(strings = { "2020-11-19T12:00:00", "2020-11-19T13:30:00", "2020-11-19T15:00:00" })
	public void save_예약불가_예약_시간_start_end_테스트(LocalDateTime startTime) throws Exception {
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com")
				.userPassword("0306").userNum(5).title("스터디 회의").reserveDate(LocalDate.of(2020, 11, 19))
				.startTime(startTime).endTime(startTime.minusHours(2)).build();

		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(dto)))//
				.andExpect(status().isConflict());
	}

	// 4-1. endTime이 startTime과 같을 경우 → 예약 불가
	@ParameterizedTest
	@DisplayName("예약 불가 - endTime이 startTime과 같을 경우")
	@ValueSource(strings = { "2020-11-19T12:00:00", "2020-11-19T13:30:00", "2020-11-19T15:00:00" })
	public void save_예약불가_예약_시간_start_end__테스트(LocalDateTime startTime) throws Exception {
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com")
				.userPassword("0306").userNum(5).title("스터디 회의").reserveDate(LocalDate.of(2020, 11, 19))
				.startTime(startTime).endTime(startTime).build();

		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(dto)))//
				.andExpect(status().isConflict());
	}

	// 5. endTime이 오후 8시이후 → 오후 8시로 변경
	@Test
	@DisplayName("예약하기 성공 - endTime 자동변경")
	public void save_endTime_변경_테스트() throws Exception {
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com")
				.userPassword("0306").userNum(5).title("스터디 회의").reserveDate(LocalDate.of(2020, 11, 19))
				.startTime(LocalDateTime.of(2020, 11, 19, 15, 00)).endTime(LocalDateTime.of(2020, 11, 19, 21, 00))
				.build();

		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(dto)))//
				.andExpect(status().isCreated())//
				.andExpect(jsonPath("endTime").value(Matchers.is("2020-11-19T19:59:59")))//
				.andExpect(jsonPath("reserveId").exists());
	}

	// 6. startTime이 오전 8시이전 → 오전 8시로 변경
	@Test
	@DisplayName("예약하기 성공 - startTime 자동변경")
	public void save_startTime_변경_테스트() throws Exception {
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com")
				.userPassword("0306").userNum(5).title("스터디 회의").reserveDate(LocalDate.of(2020, 11, 19))
				.startTime(LocalDateTime.of(2020, 11, 19, 07, 00)).endTime(LocalDateTime.of(2020, 11, 19, 16, 00))
				.build();

		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(dto)))//
				.andExpect(status().isCreated())//
				.andExpect(jsonPath("startTime").value(Matchers.is("2020-11-19T08:00:00")))//
				.andExpect(jsonPath("reserveId").exists());
	}

	// 7. startTime과 endTime의 날짜가 다를 경우
	@Test
	@DisplayName("예약 불가 - startTime과 endTime의 날짜가 다를 경우")
	public void save_예약불가_예약_시간_날짜_다름_테스트() throws Exception {
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com")
				.userPassword("0306").userNum(5).title("스터디 회의").reserveDate(LocalDate.of(2020, 11, 19))
				.startTime(LocalDateTime.of(2020, 11, 19, 15, 00)).endTime(LocalDateTime.of(2020, 11, 20, 16, 00))
				.build();

		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(dto)))//
				.andExpect(status().isConflict());
	}

	// 8. 시간이 30분 단위로 들어오지 않았을 경우
	@ParameterizedTest
	@DisplayName("예약 불가 - 시간이 30분 단위로 들어오지 않았을 경우")
	@ValueSource(strings = { "2020-11-19T12:42:53", "2020-11-19T13:01:50", "2020-11-19T15:54:45" })
	public void save_예약불가_시간_블록_테스트(LocalDateTime startTime) throws Exception {
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com")
				.userPassword("0306").userNum(5).title("스터디 회의").reserveDate(LocalDate.of(2020, 11, 19))
				.startTime(startTime).endTime(startTime.plusHours(1).minusMinutes(30)).build();

		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(dto)))//
				.andExpect(status().isConflict());
	}

	// 예약수정 테스트
	// - 예약수정 성공

	// - 데이터가 하나라도 누락(null이나 "")될 경우 → 400에러

	// - roomId
	// 1. 회의실테이블에 해당값이 없을 경우 → 수정 불가

	// - userEmail
	// 1. 이메일 형식이 아닐 경우 → 400에러

	// - userPassword
	// 1. 4글자 보다 작은 경우 → 400에러

	// - userNum
	// 1. 0보다 작은 값이 들어왔을 경우 → 400에러

	// - reserveDate
	// 1. 포멧(yyyy-MM-dd)이 맞지 않을 경우 → 400에러

	// 2. 현재 날짜 이전인 경우 → 수정 불가

	// - startTime, endTime
	// 1. 포멧(yyyy-MM-dd HH:mm:ss)이 맞지 않을 경우 → 400에러

	// 2. 현재 시간 이전일 경우 →수정 불가

	// 3. 같은 회의실의 다른 예약과 겹칠 경우 → 수정 불가

	// 4. endTime이 startTime보다 빠른 시간일 경우 → 수정 불가

	// 5. startTime과 endTime이 12시간 이상 차이날경우 → 가능한 마지막 시간으로 변경

	// - 현재 시간이 수정 하려는 예약의 startTime보다 지났을 경우 → 수정 불가

	// // ------------------------------

	// gyuwoon

	/* 예약 조회 테스트 */
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

		MvcResult result = mockMvc.perform(//
				get(this.API_URL + "/2020-11-10"))//
				.andDo(MockMvcResultHandlers.print())//
				.andExpect(status().isOk())//
				.andReturn();//
	}

	// 실패 : 날짜를 안보낼때 -> 404에러
	@Test
	@DisplayName(value = "메인 조회 실패 - 날짜 미입력")
	public void 메인_조회_실패() throws Exception {

		Reserve reserve = Reserve.builder().userName("정겨운").reserveDate(LocalDate.of(2020, 11, 10)).room(room1).build();

		reserveRepository.save(reserve);

		// 안하면 컴파일 오류,,, 뭐죠
		// Assertions.assertThrows(NestedServletException.class, () -> {
		mockMvc.perform(get(this.API_URL))//
				.andDo(print())//
				.andExpect(status().isBadRequest());//
		// });
	}

	// 날짜에 해당하는 예약리스트가 없을때 -> 200 (빈 리스트를 보낸다)
	@DisplayName(value = "메인 조회 성공 - 빈 리스트 리턴")
	@ParameterizedTest
	@ValueSource(strings = { "2019-11-10", "2020-02-10", "2020-11-01" })
	public void 메인_조회_성공_예약없음(String inputDate) throws Exception {
		Reserve reserve = Reserve.builder().userName("정겨운").reserveDate(LocalDate.parse(inputDate)).room(room1).build();

		reserveRepository.save(reserve);

		mockMvc.perform(get(this.API_URL + "/2020-11-10")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string("[]"));
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

		mockMvc.perform(get(this.API_URL).param("roomId", "1").param("year", "2020").param("month", "11"))
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

		mockMvc.perform(get(this.API_URL + "/2020-11-08/1")).andDo(print()).andExpect(status().isOk())//
				.andExpect(jsonPath("$[0].reserveDate").value(Matchers.is("2020-11-08")));
	}

	// 일간조회 성공 - 아무예약이 없으면 빈 리스트 리턴
	@ParameterizedTest
	@ValueSource(strings = { "2019-11-10", "2020-02-10", "2020-11-01" })
	@DisplayName(value = "상세 조회 성공 - 빈 리스트 리턴")
	public void 당일_조회_성공_빈리스트(String input_date) throws Exception {
		LocalDate date = LocalDate.parse(input_date);// 예약날짜
		LocalDateTime startTime = LocalDateTime.parse("2020-11-08T10:00:00");
		LocalDateTime endTime = LocalDateTime.parse("2020-11-08T11:30:00");

		Reserve reserve = Reserve.builder().userName("정겨운").reserveDate(date).startTime(startTime).endTime(endTime)
				.room(room1).build();

		reserveRepository.save(reserve);

		mockMvc.perform(get(this.API_URL + "/2020-11-08/1")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string("[]"));
	}

	// 일간조회 실패 - 없는 회의실 id를 호출한 경우
	@ParameterizedTest
	@ValueSource(strings = { "5", "-1" })
	public void 당일_조회_실패_roomId(String input) throws Exception {
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

		mockMvc.perform(get(this.API_URL + "/2020-11-08/" + input)).andDo(print()).andExpect(status().isBadRequest());
	}

	/* 예약취소 테스트 */
	// 예약 취소 성공
	@Test
	public void 예약_취소_성공() throws Exception {
		// 예약 생성
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정겨운").userEmail("gyu@email.com")
				.userPassword("0000").userNum(2).title("회의")//
				.reserveDate(LocalDate.of(2020, 12, 10)).startTime(LocalDateTime.of(2020, 12, 10, 16, 00))
				.endTime(LocalDateTime.of(2020, 12, 10, 17, 30))//
				.build();
		Reserve res = reserveService.save(dto);

		this.mockMvc.perform(MockMvcRequestBuilders.delete(this.API_URL + "/" + res.getReserveId())
				.param("reserveId", String.valueOf(res.getReserveId())).param("userPassword", res.getUserPassword())
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(res))//
		).andDo(print())//
				.andExpect(status().isCreated());
		List<Reserve> reserve = reserveRepository.findAll();
		assertEquals(0, reserve.size());
	}

	// 파라미터로 비밀번호 또는 예약테이블의 id가 넘어가지 않은 경우 ->null일때 400, empty일때 컴파일에러
	@Test
	public void 예약_취소_실패_데이터누락_reserveId() throws Exception {
		// 예약 생성
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정겨운").userEmail("gyu@email.com")
				.userPassword("0000").userNum(2).title("회의")//
				.reserveDate(LocalDate.of(2020, 12, 10))//
				.startTime(LocalDateTime.of(2020, 12, 10, 16, 00))//
				.endTime(LocalDateTime.of(2020, 12, 10, 17, 30))//
				.build();
		Reserve res = reserveService.save(dto);

		this.mockMvc.perform(MockMvcRequestBuilders.delete(this.API_URL + "/" + res.getReserveId())
				.param("userPassword", res.getUserPassword()).contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(res))//
		).andDo(print())//
				.andExpect(status().isBadRequest());
	}

	@Test
	public void 예약_취소_실패_데이터누락_password() throws Exception {
		// 예약 생성
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정겨운").userEmail("gyu@email.com")
				.userPassword("0000").userNum(2).title("회의")//
				.reserveDate(LocalDate.of(2020, 12, 10))//
				.startTime(LocalDateTime.of(2020, 12, 10, 16, 00))//
				.endTime(LocalDateTime.of(2020, 12, 10, 17, 30))//
				.build();
		Reserve res = reserveService.save(dto);

		this.mockMvc.perform(MockMvcRequestBuilders.delete(this.API_URL + "/" + res.getReserveId())
				.param("reserveId", String.valueOf(res.getReserveId())).contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(res))//
		).andDo(print())//
				.andExpect(status().isBadRequest());

	}

	@Disabled // java.lang.IllegalArgumentException: Entity must not be null!
	// 에러
	// 발생... 처리는 어떻게 하죠..
	@Test
	public void 예약_취소_실패_데이터누락_empty_password() throws Exception {
		// 예약 생성
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정겨운").userEmail("gyu@email.com")
				.userPassword("0000").userNum(2).title("회의")//
				.reserveDate(LocalDate.of(2020, 12, 10))//
				.startTime(LocalDateTime.of(2020, 12, 10, 16, 00))//
				.endTime(LocalDateTime.of(2020, 12, 10, 17, 30))//
				.build();
		Reserve res = reserveService.save(dto);

		this.mockMvc.perform(MockMvcRequestBuilders.delete(this.API_URL + "/" + res.getReserveId())
				.param("reserveId", String.valueOf(res.getReserveId())).param("userPassword", "")
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(res))//
		).andDo(print())//
				.andExpect(status().isBadRequest());
	}

	// url로 예약테이블 id가 넘어가지 않은 경우 -> 405 해당 자원이 지원하지 않는 메소드일 때,,
	@Test
	public void 예약_취소_실패_path() throws Exception {
		// 예약 생성
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정겨운").userEmail("gyu@email.com")
				.userPassword("0000").userNum(2).title("회의")//
				.reserveDate(LocalDate.of(2020, 12, 10))//
				.startTime(LocalDateTime.of(2020, 12, 10, 16, 00))//
				.endTime(LocalDateTime.of(2020, 12, 10, 17, 30))//
				.build();
		Reserve res = reserveService.save(dto);

		this.mockMvc.perform(MockMvcRequestBuilders.delete(this.API_URL) // "{reserveId}"
																			// 누락
				.param("reserveId", String.valueOf(res.getReserveId())).contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(res))//
		).andDo(print())//
				.andExpect(status().isMethodNotAllowed());

	}

	// 예약테이블id가 유효하지 않는 경우(없는 예약테이블id) -> 400에러
	@ParameterizedTest
	@ValueSource(strings = { "-1", "5", "9999", "2" })
	public void 예약_취소_실패_없는예약(String input) throws Exception {
		// 예약 생성
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정겨운").userEmail("gyu@email.com")
				.userPassword("0000").userNum(2).title("회의")//
				.reserveDate(LocalDate.of(2020, 12, 10))//
				.startTime(LocalDateTime.of(2020, 12, 10, 16, 00))//
				.endTime(LocalDateTime.of(2020, 12, 10, 17, 30))//
				.build();
		Reserve res = reserveService.save(dto);

		this.mockMvc.perform(MockMvcRequestBuilders.delete(this.API_URL + "/" + input) //
				.param("reserveId", input).param("userPassword", res.getUserPassword())//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(res))//
		).andDo(print())//
				.andExpect(status().isBadRequest());
	}

	/* 비밀번호 확인 */
	// 비밀번호가 db에 저장된 비밀번호와 일치하는 경우 -> 201

	// 비밀번호가 db에 저장된 비밀번호와 일치하지 않는 경우

	// 비밀번호가 4자리 미만인 경우

	// 비밀번호값이 null 또는 empty

}
