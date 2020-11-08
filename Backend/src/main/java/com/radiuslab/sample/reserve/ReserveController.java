package com.radiuslab.sample.reserve;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reserve")
public class ReserveController {
	@Autowired
	private ReserveService reserveService;

	// 예약하기

	// 예약수정

	// 예약조회

	// 예약취소

	// 비밀번호 확인

}
