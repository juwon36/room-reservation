package com.radiuslab.sample.reserve;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reserve")
public class ReserveController {
	@Autowired
	private ReserveService reserveService;

	// 예약하기
	@PostMapping
	public ResponseEntity save(@Valid @RequestBody ReserveDto dto, Errors errors) {
		if (errors.hasErrors()) {
			return ResponseEntity.badRequest().body(errors);
		}
		Reserve res = this.reserveService.save(dto);

		URI uri = linkTo(ReserveController.class).slash(res.getReserveId()).toUri();
		return ResponseEntity.created(uri).body(res);
	}

	// 예약수정

	// 예약조회

	// 예약취소

	// 비밀번호 확인

}
