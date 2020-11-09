package com.radiuslab.sample.reserve;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Assertions;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.http.MediaType;
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

	private Room room1, room2;

	@BeforeEach
	public void 데이터_셋업() throws Exception {
		room1 = new Room();
		room1.setRoomName("1회의실");
		roomRepository.save(room1);

		room2 = new Room();
		room2.setRoomName("2회의실");
		roomRepository.save(room2);
	}

	@BeforeEach
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).addFilters(new CharacterEncodingFilter("UTF-8", true)) // 한글 깨짐 방지 필터 추가
				.alwaysDo(print()) // 항상 내용 출력
				.build();

		// 회의실 초기화
		for (int i = 1; i < 5; i++) {
			Room room = Room.builder().roomName(i + "회의실").capacity(i * 3).build();
			this.roomRepository.save(room);
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
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com").userPassword("0306").userNum(5).title("스터디 회의")//
				.reserveDate(LocalDate.of(2020, 11, 19)).startTime(LocalDateTime.of(2020, 11, 19, 15, 00)).endTime(LocalDateTime.of(2020, 11, 19, 16, 00))//
				.build();

		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(dto)))//
				.andExpect(status().isCreated())//
				.andExpect(jsonPath("reserveId").exists());
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
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(10)).userName("정주원").userEmail("juwon@gmail.com").userPassword("0306").userNum(5).title("스터디 회의")//
				.reserveDate(LocalDate.of(2020, 11, 19)).startTime(LocalDateTime.of(2020, 11, 19, 15, 00)).endTime(LocalDateTime.of(2020, 11, 19, 16, 00))//
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
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(10)).userName("정주원").userEmail(email).userPassword("0306").userNum(5).title("스터디 회의")//
				.reserveDate(LocalDate.of(2020, 11, 19)).startTime(LocalDateTime.of(2020, 11, 19, 15, 00)).endTime(LocalDateTime.of(2020, 11, 19, 16, 00))//
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
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com").userPassword("as1").userNum(5).title("스터디 회의")//
				.reserveDate(LocalDate.of(2020, 11, 19)).startTime(LocalDateTime.of(2020, 11, 19, 15, 00)).endTime(LocalDateTime.of(2020, 11, 19, 16, 00))//
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
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com").userPassword("0306").userNum(-1).title("스터디 회의")//
				.reserveDate(LocalDate.of(2020, 11, 19)).startTime(LocalDateTime.of(2020, 11, 19, 15, 00)).endTime(LocalDateTime.of(2020, 11, 19, 16, 00))//
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
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com").userPassword("0306").userNum(5).title("스터디 회의")//
				.reserveDate(reserveDate).startTime(LocalDateTime.of(2020, 11, 19, 15, 00)).endTime(LocalDateTime.of(2020, 11, 19, 16, 00))//
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
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com").userPassword("0306").userNum(5).title("스터디 회의")//
				.reserveDate(LocalDate.of(2020, 11, 19)).startTime(startTime).endTime(LocalDateTime.of(2020, 11, 19, 16, 00))//
				.build();

		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(dto)))//
				.andExpect(status().isConflict());
	}

	// 3. 같은 회의실의 다른 예약과 겹칠 경우 → 예약 불가
	@ParameterizedTest
	@DisplayName("예약 불가 - 다른 예약과 겹칠 경우")
	@ValueSource(strings = { "2020-11-19T12:00:00", "2020-11-19T13:30:00", "2020-11-19T15:00:00" }) // 종료시간 겹침, 포함, 시작시간 겹침
	public void save_예약불가_예약_시간_겹침_테스트(LocalDateTime startTime) throws Exception {
		// 비교할 예약 2020-11-19 1시부터 4시까지
		ReserveDto reserve = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com").userPassword("0306").userNum(5).title("스터디 회의")//
				.reserveDate(LocalDate.of(2020, 11, 19)).startTime(LocalDateTime.of(2020, 11, 19, 13, 00)).endTime(LocalDateTime.of(2020, 11, 19, 16, 00))//
				.build();
		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(reserve)))//
				.andExpect(status().isCreated());

		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com").userPassword("0306").userNum(5).title("스터디 회의")//
				.reserveDate(LocalDate.of(2020, 11, 19)).startTime(startTime).endTime(startTime.plusHours(2))//
				.build();

		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(dto)))//
				.andExpect(status().isConflict());
	}

	// 4. endTime이 startTime보다 빠른 시간일 경우 → 예약 불가
	@ParameterizedTest
	@DisplayName("예약 불가 - endTime이 startTime보다 빠른 시간일 경우")
	@ValueSource(strings = { "2020-11-19T12:00:00", "2020-11-19T13:30:00", "2020-11-19T15:00:00" })
	public void save_예약불가_예약_시간_start_end_테스트(LocalDateTime startTime) throws Exception {
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com").userPassword("0306").userNum(5).title("스터디 회의")//
				.reserveDate(LocalDate.of(2020, 11, 19)).startTime(startTime).endTime(startTime.plusHours(2))//
				.build();

		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(dto)))//
				.andExpect(status().isConflict());
	}

	// 5. endTime이 오후 8시이후 → 오후 8시로 변경
	@Test
	@DisplayName("예약하기 성공 - endTime 자동변경")
	public void save_endTime_변경_테스트() throws Exception {
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com").userPassword("0306").userNum(5).title("스터디 회의")//
				.reserveDate(LocalDate.of(2020, 11, 19)).startTime(LocalDateTime.of(2020, 11, 19, 15, 00)).endTime(LocalDateTime.of(2020, 11, 19, 21, 00))//
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
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com").userPassword("0306").userNum(5).title("스터디 회의")//
				.reserveDate(LocalDate.of(2020, 11, 19)).startTime(LocalDateTime.of(2020, 11, 19, 07, 00)).endTime(LocalDateTime.of(2020, 11, 19, 16, 00))//
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
	public void save_bad_request_예약_시간_날짜_다름_테스트() throws Exception {
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com").userPassword("0306").userNum(5).title("스터디 회의")//
				.reserveDate(LocalDate.of(2020, 11, 19)).startTime(LocalDateTime.of(2020, 11, 19, 15, 00)).endTime(LocalDateTime.of(2020, 11, 20, 16, 00))//
				.build();

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
