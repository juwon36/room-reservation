package com.radiuslab.sample.reserve;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reserve")
public class ReserveController {
	@Autowired
	private ReserveService reserveService;

	@Autowired
	private ReserveTimeValidator reserveTimeValidator;

	@Autowired
	private ReserveValidator reserveValidator;

	// 예약하기
	@PostMapping
	public ResponseEntity<Object> save(@Valid @RequestBody ReserveDto dto, Errors errors) {
		if (errors.hasErrors()) {
			return ResponseEntity.badRequest().body(errors);
		}

		dto.update();
		this.reserveTimeValidator.validate(dto, errors);
		if (errors.hasErrors()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
		}

		this.reserveValidator.validate(dto, errors);
		if (errors.hasErrors()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
		}

		Reserve res = this.reserveService.save(dto);

		URI uri = linkTo(ReserveController.class).slash(res.getReserveId()).toUri();
		return ResponseEntity.created(uri).body(res);
	}

	// 예약수정

	// 예약조회
	@GetMapping("{reserveDate}")
	public ResponseEntity<List<Reserve>> findByReserveDate(@PathVariable String reserveDate) {
		List<Reserve> reserveList = this.reserveService.findByReserveDate(reserveDate);
		return new ResponseEntity<List<Reserve>>(reserveList, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<List<Reserve>> checkMonthlyReserve(@RequestParam String roomId, @RequestParam String year,
			@RequestParam String month) {
		List<Reserve> reserveList = this.reserveService.findByRoomIdAndYearMonth(roomId, year, month);
		return new ResponseEntity<List<Reserve>>(reserveList, HttpStatus.OK);
	}

	@GetMapping("{reserveDate}/{roomId}")
	public ResponseEntity<?> findByReserveDateAndRoomId(@PathVariable String reserveDate, @PathVariable Long roomId) {
		if (roomId <= 0 || roomId > 4) {
			return ResponseEntity.badRequest().body("존재하지 않는 회의실id입니다");
		}
		List<Reserve> reserveList = this.reserveService.findByReserveDateAndRoomId(reserveDate, roomId);
		return new ResponseEntity<List<Reserve>>(reserveList, HttpStatus.OK);
	}

	// 예약취소
	@DeleteMapping("{reserveId}")
	public ResponseEntity delete(@RequestParam(name = "reserveId") Long reserveId, @RequestParam String userPassword) {
		Reserve res = reserveService.findByReserveId(reserveId);
		if (res == null) {
			return ResponseEntity.badRequest().body("삭제하려는 예약이 존재하지 않습니다.");
		}
		Reserve checkedReserve = reserveService.isReserveId(res, userPassword);
		this.reserveService.delete(checkedReserve);
		URI uri = linkTo(ReserveController.class).slash(res.getReserveId()).toUri();
		return ResponseEntity.created(uri).body(res);
	}

	// 비밀번호 확인

}
