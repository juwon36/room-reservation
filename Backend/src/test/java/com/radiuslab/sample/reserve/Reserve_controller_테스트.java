package com.radiuslab.sample.reserve;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = { "spring.datasource.url=jdbc:postgresql://localhost:5432/test" }) // @SpringBootTest에서 다른 db를 사용하겠다
@AutoConfigureMockMvc
public class Reserve_controller_테스트 {
	// juwon
	// 예약하기 테스트

	// 예약수정 테스트


	// gyuwoon
	// 예약조회 테스트

	// 예약취소 테스트

}
