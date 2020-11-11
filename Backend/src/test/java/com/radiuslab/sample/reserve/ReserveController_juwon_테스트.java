package com.radiuslab.sample.reserve;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
public class ReserveController_juwon_테스트 {
	protected static final Logger LOGGER = LoggerFactory.getLogger(ReserveController_juwon_테스트.class);

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
	public void 데이터_셋업() {
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
		this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).addFilters(new CharacterEncodingFilter("UTF-8", true))
				.alwaysDo(print()) // 항상 내용 출력
				.build();

		for (Room r : this.roomRepository.findAll()) {
			LOGGER.info(r.getRoomId().toString() + " : " + r.getRoomName());
		}
	}

	@AfterEach
	public void delete() {
		this.reserveRepository.deleteAll();
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
		// list가 null이 아니고 size가 1이여야 한다
		assertThat(list).isNotNull().hasSize(1);
		// reserveId, room, endTime 외에 다른 이름이 같은 필드는 전부 같은 값이여야 한다
		assertThat(list.get(0)).isNotNull().isEqualToIgnoringGivenFields(dto, "reserveId", "room", "endTime");
		assertThat(list.get(0).getRoom().getRoomId()).isEqualTo(dto.getRoomId());
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

		List<Reserve> list = this.reserveRepository.findAll();
		assertThat(list).isNotNull().isEmpty();// null이 아니고 빈 리스트여야 한다
	}

	@Test
	@DisplayName("예약하기 Bad Request - 데이터 누락(empty)")
	public void save_bad_request_input_empty_테스트() throws Exception {
		ReserveDto dto = ReserveDto.builder().userName("").userEmail("").userPassword("").title("").build();

		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(dto)))//
				.andExpect(status().isBadRequest());

		List<Reserve> list = this.reserveRepository.findAll();
		assertThat(list).isNotNull().isEmpty();
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

		List<Reserve> list = this.reserveRepository.findAll();
		assertThat(list).isNotNull().isEmpty();
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

		List<Reserve> list = this.reserveRepository.findAll();
		assertThat(list).isNotNull().isEmpty();
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

		List<Reserve> list = this.reserveRepository.findAll();
		assertThat(list).isNotNull().isEmpty();
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

		List<Reserve> list = this.reserveRepository.findAll();
		assertThat(list).isNotNull().isEmpty();
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

		List<Reserve> list = this.reserveRepository.findAll();
		assertThat(list).isNotNull().isEmpty();
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

		List<Reserve> list = this.reserveRepository.findAll();
		assertThat(list).isNotNull().isEmpty();
	}

	// 3. 같은 회의실의 다른 예약과 겹칠 경우 → 예약 불가
	@ParameterizedTest
	@DisplayName("예약 불가 - 다른 예약과 겹칠 경우")
	// 종료시간 겹침, 포함, 시작시간 겹침
	@ValueSource(strings = { "2020-11-19T12:00:00", "2020-11-19T13:00:00", "2020-11-19T15:00:00" })
	public void save_예약불가_예약_시간_겹침_테스트(LocalDateTime startTime) throws Exception {
		// 비교할 예약 2020-11-19 1시부터 4시까지
		Reserve reserve = Reserve.builder().room(room1).userName("정주원").userEmail("juwon@gmail.com")
				.userPassword("0306").userNum(5).title("스터디 회의").reserveDate(LocalDate.of(2020, 11, 19))
				.startTime(LocalDateTime.of(2020, 11, 19, 13, 00)).endTime(LocalDateTime.of(2020, 11, 19, 16, 00))
				.build();
		this.reserveRepository.save(reserve);

		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정주원").userEmail("juwon@gmail.com")
				.userPassword("0306").userNum(5).title("스터디 회의").reserveDate(LocalDate.of(2020, 11, 19))
				.startTime(startTime).endTime(startTime.plusHours(3)).build();

		this.mockMvc.perform(post(this.API_URL)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(dto)))//
				.andExpect(status().isConflict());

		List<Reserve> list = this.reserveRepository.findAll();
		assertThat(list).isNotNull().hasSize(1);
		assertThat(list.get(0)).isEqualToIgnoringGivenFields(reserve, "room");
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

		List<Reserve> list = this.reserveRepository.findAll();
		assertThat(list).isNotNull().hasSize(2);
		assertThat(list.get(0)).isEqualToIgnoringGivenFields(reserve, "room");
		assertThat(list.get(1)).isEqualToIgnoringGivenFields(dto, "reserveId", "room", "endTime");
		assertThat(list.get(0).getRoom().getRoomId()).isEqualTo(dto.getRoomId());
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

		List<Reserve> list = this.reserveRepository.findAll();
		assertThat(list).isNotNull().isEmpty();
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

		List<Reserve> list = this.reserveRepository.findAll();
		assertThat(list).isNotNull().isEmpty();
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

		List<Reserve> list = this.reserveRepository.findAll();
		assertThat(list).isNotNull().isEmpty();
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

		List<Reserve> list = this.reserveRepository.findAll();
		assertThat(list).isNotNull().isEmpty();
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

}
