package com.radiuslab.sample.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.radiuslab.sample.users.UserService;
import com.radiuslab.sample.users.dto.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = { "spring.datasource.url=jdbc:postgresql://localhost:5432/bucheon_bus" }) // @SpringBootTest에서 다른 db를 사용하겠다
@AutoConfigureMockMvc
public class UserController_테스트 {
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext ctx;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserService userService;

	@BeforeEach // Junit4의 @Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).addFilters(new CharacterEncodingFilter("UTF-8", true)) // 한글 깨짐 방지 필터 추가
				.alwaysDo(print()) // 항상 내용 출력
				.build();

		for (int i = 0; i < 10; i++) {
			UserSave userDto = UserSave.builder().name("user" + i).email("user" + i + "@gmail.com").password("abcd1234*" + i).gender("none").build();
			this.userService.save(userDto);
		}
	}

	@AfterEach
	public void delete() {
		this.userService.deleteAll();
	}

	// save
	@Test
	@DisplayName("User 생성 테스트")
	public void user_생성_테스트() throws Exception {
		UserSave userDto = UserSave.builder().name("정주원").email("jjw0306@gmail.com").password("abcd1234*").gender("female").build();

		this.mockMvc.perform(post("/api/users")//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(userDto)))//
				.andExpect(status().isCreated())//
				.andExpect(jsonPath("id").exists());
	}

	@Test
	@DisplayName("User 생성 Bad Request - null")
	public void user_생성_bad_request_input_null_테스트() throws Exception {
		UserSave userDto = UserSave.builder().build();

		this.mockMvc.perform(post("/api/users")//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(userDto)))//
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("User 생성 Bad Request - empty")
	public void user_생성_bad_request_input_empty_테스트() throws Exception {
		UserSave userDto = UserSave.builder().name("").email("").password("").gender("").build();

		this.mockMvc.perform(post("/api/users")//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(userDto)))//
				.andExpect(status().isBadRequest());
	}

	@ParameterizedTest
	@DisplayName("User 생성 Bad Request - name")
	@ValueSource(strings = { "j12345678901234567890", "juwonradiuslabcomjuwonradiuslabcom", "으아아아아ㅏ아아아아으아아아아ㅏ아아아아ㅏ" })
	public void user_생성_bad_request_input_name_테스트(String name) throws Exception {
		UserSave userDto = UserSave.builder().name(name).email("jjw0306@gmail.com").password("abcd1234*").gender("female").build();

		this.mockMvc.perform(post("/api/users")//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(userDto)))//
				.andExpect(status().isBadRequest());
	}

	@ParameterizedTest
	@DisplayName("User 생성 Bad Request - email")
	@ValueSource(strings = { "juwon12radiuslabcom", "juwonradiuslab.com" })
	public void user_생성_bad_request_input_email_테스트(String email) throws Exception {
		UserSave userDto = UserSave.builder().name("정주원").email(email).password("abcd1234*").gender("female").build();

		this.mockMvc.perform(post("/api/users")//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(userDto)))//
				.andExpect(status().isBadRequest());
	}

	@ParameterizedTest
	@DisplayName("User 생성 Bad Request - password")
	@ValueSource(strings = { "abcdefgh", "12345678", "#*#*#*#*", "abcd1234", "abcd@#$%", "1234%*!^", "abc123@", "abcdefghijklmnop1234567890@#$%*!^" })
	public void user_생성_bad_request_input_password_테스트(String password) throws Exception {
		UserSave userDto = UserSave.builder().name("정주원").email("jjw0306@gmail.com").password(password).gender("female").build();

		this.mockMvc.perform(post("/api/users")//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(userDto)))//
				.andExpect(status().isBadRequest());
	}

	@ParameterizedTest
	@DisplayName("User 생성 Bad Request - birthday")
	@ValueSource(strings = { "2999-01-01", "2030-11-02" })
	public void user_생성_bad_request_input_birthday_테스트(LocalDate birthday) throws Exception {
		UserSave userDto = UserSave.builder().name("정주원").email("jjw0306@gmail.com").password("abcd1234*").birthday(birthday).gender("female").build();

		this.mockMvc.perform(post("/api/users")//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(userDto)))//
				.andExpect(status().isBadRequest());
	}

	@ParameterizedTest
	@DisplayName("User 생성 Bad Request - gender")
	@ValueSource(strings = { "male", "female", "none" })
	public void user_생성_input_gender_테스트(String gender) throws Exception {
		UserSave userDto = UserSave.builder().name("정주원").email("jjw0306@gmail.com").password("abcd1234*").gender(gender).build();

		this.mockMvc.perform(post("/api/users")//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(userDto)))//
				.andExpect(status().isCreated());
	}

	@ParameterizedTest
	@DisplayName("User 생성 Bad Request - gender")
	@ValueSource(strings = { " male", "fem ale", "n one" })
	public void user_생성_bad_request_input_gender_테스트(String gender) throws Exception {
		UserSave userDto = UserSave.builder().name("정주원").email("jjw0306@gmail.com").password("abcd1234*").gender(gender).build();

		this.mockMvc.perform(post("/api/users")//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(userDto)))//
				.andExpect(status().isBadRequest());
	}

	// login
	@ParameterizedTest
	@ValueSource(ints = { 0, 2, 4, 6, 8 })
	@DisplayName("로그인 테스트")
	public void 로그인_테스트(int i) throws Exception {
		UserLogin userDto = UserLogin.builder().email("user" + i + "@gmail.com").password("abcd1234*" + i).build();

		this.mockMvc.perform(post("/api/users/login")//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(userDto)))//
				.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("로그인 Bad Request - null")
	public void 로그인_bad_request_input_null_테스트() throws Exception {
		UserLogin userDto = UserLogin.builder().build();

		this.mockMvc.perform(post("/api/users/login")//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(userDto)))//
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("로그인 Bad Request - empty")
	public void 로그인_bad_request_input_empty_테스트() throws Exception {
		UserLogin userDto = UserLogin.builder().email("").password("").build();

		this.mockMvc.perform(post("/api/users/login")//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(userDto)))//
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("로그인 Bad Request - email")
	public void 로그인_bad_request_input_email_테스트() throws Exception {
		UserLogin userDto = UserLogin.builder().email("user@gmail.com").password("abcd1234*").build();

		this.mockMvc.perform(post("/api/users/login")//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(userDto)))//
				.andExpect(status().isBadRequest());
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 2, 4, 6, 8 })
	@DisplayName("로그인 Bad Request - password")
	public void 로그인_bad_request_input_password_테스트(int i) throws Exception {
		UserLogin userDto = UserLogin.builder().email("user" + i + "@gmail.com").password("abcd1234*").build();

		this.mockMvc.perform(post("/api/users/login")//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(userDto)))//
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("회원정보 수정 테스트")
	public void 회원정보_수정_테스트() throws Exception {
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("login", this.userService.login(UserLogin.builder().email("user1@gmail.com").password("abcd1234*1").build()));
		UserUpdate userDto = UserUpdate.builder().name("updateUser").password("updateUser12*").gender("male").build();

		this.mockMvc.perform(put("/api/users")//
				.session(session)//
				.contentType(MediaType.APPLICATION_JSON)//
				.content(objectMapper.writeValueAsString(userDto)))//
				.andExpect(status().isCreated())//
				.andExpect(jsonPath("name").value(Matchers.is("updateUser")))//
				.andExpect(jsonPath("gender").value(Matchers.is("male")));
	}
}
