package com.radiuslab.sample.reserve;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.radiuslab.sample.room.Room;
import com.radiuslab.sample.room.RoomRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = { "spring.datasource.url=jdbc:postgresql://localhost:5432/radius_test" })
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
public class ReserveController_gyuwoon_테스트 {
	protected static final Logger LOGGER = LoggerFactory.getLogger(ReserveController_gyuwoon_테스트.class);

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
		ReserveDto cancelDto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정겨운").userEmail("gyu@email.com")
				.userPassword("0000").userNum(2).title("회의")//
				.reserveDate(LocalDate.of(2020, 12, 10)).startTime(LocalDateTime.of(2020, 12, 10, 16, 00))
				.endTime(LocalDateTime.of(2020, 12, 10, 17, 30))//
				.build();
		Reserve res = reserveService.save(cancelDto);

		// 예약 취소를 시도 -> 팝업창에 비밀번호 입력 -> 비밀번호와 예약번호 받아서 넘긴다(PassCheckDto)
		PassCheckDto pcd = PassCheckDto.builder().userPassword("0000").reserveId(res.getReserveId()).build();

		this.mockMvc.perform(MockMvcRequestBuilders.delete(this.API_URL + "/" + res.getReserveId())
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(pcd))//
		).andDo(print())//
				.andExpect(status().isCreated());
		List<Reserve> reserve = reserveRepository.findAll();
		assertEquals(0, reserve.size());
	}

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

		// 예약 취소를 시도 -> 팝업창에 비밀번호 입력 -> 비밀번호와 예약번호 받아서 넘긴다(PassCheckDto)
		PassCheckDto pcd = PassCheckDto.builder().userPassword("0000").build();

		this.mockMvc
				.perform(MockMvcRequestBuilders.delete(this.API_URL + "/" + res.getReserveId())
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(pcd)))
				.andDo(print()).andExpect(status().isBadRequest());
		// 에러 발생시 처리법 : i.e. "IllegalArgumentException에러"
		// .andExpect(result ->
		// assertTrue(result.getResolvedException().getClass().isAssignableFrom(IllegalArgumentException.class)));

	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "   ", "\t", "\n" })
	public void 예약_취소_실패_데이터누락_empty_password(String input) throws Exception {
		// 예약 생성
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정겨운").userEmail("gyu@email.com")
				.userPassword("0000").userNum(2).title("회의")//
				.reserveDate(LocalDate.of(2020, 12, 10))//
				.startTime(LocalDateTime.of(2020, 12, 10, 16, 00))//
				.endTime(LocalDateTime.of(2020, 12, 10, 17, 30))//
				.build();
		Reserve res = reserveService.save(dto);

		// 예약 취소를 시도 -> 팝업창에 비밀번호 입력 -> 비밀번호와 예약번호 받아서 넘긴다(PassCheckDto)
		PassCheckDto pcd = PassCheckDto.builder().reserveId(res.getReserveId()).userPassword(input).build();

		this.mockMvc
				.perform(MockMvcRequestBuilders.delete(this.API_URL + "/" + res.getReserveId())
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(pcd)))
				.andDo(print()).andExpect(status().isBadRequest());
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

		// 예약 취소를 시도 -> 팝업창에 비밀번호 입력 -> 비밀번호와 예약번호 받아서 넘긴다(PassCheckDto)
		PassCheckDto pcd = PassCheckDto.builder().userPassword("0000").reserveId(res.getReserveId()).build();

		this.mockMvc
				.perform(MockMvcRequestBuilders.delete(this.API_URL).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(pcd)))
				.andDo(print()).andExpect(status().isMethodNotAllowed());

	}

	// 예약테이블id가 유효하지 않는 경우(없는 예약테이블id) -> 컴파일 에러.. ava.lang.NullPointerException: 아예
	// delete 매핑이 안되는건가,,
	//@Disabled
	@ParameterizedTest
	@ValueSource(strings = { "5", "9999", "2", "-1" })
	public void 예약_취소_실패_없는예약(String input) throws Exception {
		// 예약 생성
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정겨운").userEmail("gyu@email.com")
				.userPassword("0000").userNum(2).title("회의")//
				.reserveDate(LocalDate.of(2020, 12, 10))//
				.startTime(LocalDateTime.of(2020, 12, 10, 16, 00))//
				.endTime(LocalDateTime.of(2020, 12, 10, 17, 30))//
				.build();
		Reserve res = reserveService.save(dto);

		System.out.println("######################");
		System.out.println( res.toString());
		// 예약 취소를 시도 -> 팝업창에 비밀번호 입력 -> 비밀번호와 예약번호 받아서 넘긴다(PassCheckDto)
		PassCheckDto pcd = PassCheckDto.builder().reserveId(Long.parseLong(input)).userPassword("0000").build();

		System.out.println( res.getReserveId());
		
		String temp = this.API_URL + "/" + res.getReserveId();
		
		System.out.println(temp);
		
		this.mockMvc
		.perform(
				MockMvcRequestBuilders.delete(temp)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(pcd)))
		.andExpect(status().isBadRequest())
		.andExpect( (result) -> {
			System.out.println("@@@@@@@@@@@@");
			System.out.println(result.getResolvedException());
		});
		
			
		
//		
//				.andDo(print())
//				.andExpect( rst -> {
//					System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@");
//					System.out.println( rst.getResolvedException());
//				})
//				.andExpect(
//						result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(CException.class)))
				//.andReturn();
	}

	/* 비밀번호 확인 */
	// 비밀번호가 db에 저장된 비밀번호와 일치하는 경우 -> 201
	@Test
	public void 비밀번호_일치_성공() throws Exception {
		// 예약 생성
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정겨운").userEmail("gyu@email.com")
				.userPassword("0000").userNum(2).title("회의")//
				.reserveDate(LocalDate.of(2020, 12, 10))//
				.startTime(LocalDateTime.of(2020, 12, 10, 16, 00))//
				.endTime(LocalDateTime.of(2020, 12, 10, 17, 30))//
				.build();
		Reserve res = reserveService.save(dto);

		PassCheckDto pcd = PassCheckDto.builder().reserveId(res.getReserveId()).userPassword(res.getUserPassword()).build();

		this.mockMvc
				.perform(post(this.API_URL + "/checkpw")
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(pcd)))
				.andDo(print()).andExpect(status().isCreated());
	}

	// 비밀번호가 db에 저장된 비밀번호와 일치하지 않는 경우
	@Test
	public void 비밀번호_일치_실패() throws Exception {
		// 예약 생성
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정겨운").userEmail("gyu@email.com")
				.userPassword("0000").userNum(2).title("회의")//
				.reserveDate(LocalDate.of(2020, 12, 10))//
				.startTime(LocalDateTime.of(2020, 12, 10, 16, 00))//
				.endTime(LocalDateTime.of(2020, 12, 10, 17, 30))//
				.build();
		Reserve res = reserveService.save(dto);

		PassCheckDto pcd = PassCheckDto.builder().reserveId(res.getReserveId()).userPassword("1111").build();

		this.mockMvc
				.perform(post(this.API_URL + "/checkpw")
						.contentType(MediaType.APPLICATION_JSON)//
						.content(objectMapper.writeValueAsString(pcd)))
				.andDo(print()).andExpect(status().isBadRequest());
	}

	// 비밀번호가 4자리 미만인 경우 (+ 5자리인 경우)
	@ParameterizedTest
	@ValueSource(strings = {"0", "00", "000","00000"})
	public void 비밀번호_자릿수_실패(String input) throws Exception {
		// 예약 생성
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정겨운").userEmail("gyu@email.com")
				.userPassword("0000").userNum(2).title("회의")//
				.reserveDate(LocalDate.of(2020, 12, 10))//
				.startTime(LocalDateTime.of(2020, 12, 10, 16, 00))//
				.endTime(LocalDateTime.of(2020, 12, 10, 17, 30))//
				.build();
		Reserve res = reserveService.save(dto);

		PassCheckDto pcd = PassCheckDto.builder().reserveId(res.getReserveId()).userPassword(input).build();

		this.mockMvc
				.perform(post(this.API_URL + "/checkpw")
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(pcd)))
				.andDo(print()).andExpect(status().isBadRequest());
	}

	// 비밀번호값이 null 또는 empty
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "   ", "\t", "\n" })
	public void 비밀번호_공백_실패(String input) throws Exception {
		// 예약 생성
		ReserveDto dto = ReserveDto.builder().roomId(Long.valueOf(1)).userName("정겨운").userEmail("gyu@email.com")
				.userPassword("0000").userNum(2).title("회의")//
				.reserveDate(LocalDate.of(2020, 12, 10))//
				.startTime(LocalDateTime.of(2020, 12, 10, 16, 00))//
				.endTime(LocalDateTime.of(2020, 12, 10, 17, 30))//
				.build();
		Reserve res = reserveService.save(dto);

		PassCheckDto pcd = PassCheckDto.builder().reserveId(res.getReserveId()).userPassword(input).build();

		this.mockMvc
				.perform(post(this.API_URL + "/checkpw")
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(pcd)))
				.andDo(print()).andExpect(status().isBadRequest());
	}

}
