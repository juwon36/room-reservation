package com.radiuslab.sample.room;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = { "spring.datasource.url=jdbc:postgresql://localhost:5432/test" }) // @SpringBootTest에서 다른 db를 사용하겠다
@AutoConfigureMockMvc
public class Room_controller_테스트 {
	// 회의실 조회 테스트
}
