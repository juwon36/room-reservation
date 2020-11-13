package com.radiuslab.sample.room;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.radiuslab.sample.roomItem.RoomItem;
import com.radiuslab.sample.roomItem.RoomItemRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = { "spring.datasource.url=jdbc:postgresql://localhost:5432/test" }) // @SpringBootTest에서 다른
																								// db를 사용하겠다
@AutoConfigureMockMvc
public class Room_controller_테스트 {
	protected static final Logger LOGGER = LoggerFactory.getLogger(Room_controller_테스트.class);

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext ctx;

	@Autowired
	private ObjectMapper objectMapper;

	
}
