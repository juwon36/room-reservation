package com.radiuslab.sample.reserve;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.radiuslab.sample.reserve.validator.ReserveTimeValidator;
import com.radiuslab.sample.reserve.validator.ReserveValidator;

@RestController
@RequestMapping("/api/reserve")
public class ReserveController {
	Logger LOGGER = LoggerFactory.getLogger(ReserveController.class);
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
	@PutMapping
	public ResponseEntity update(@Valid @RequestBody ReserveDto dto, Errors errors) {
		return null;
	}

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
	public ResponseEntity delete(@Valid @RequestBody PassCheckDto passCheckDto, Errors error) {
		LOGGER.info("reserveId = " + passCheckDto.getReserveId() + ", password = " + passCheckDto.getUserPassword());
		if (error.hasErrors()) {
			return ResponseEntity.badRequest().body("삭제하려는 예약번호나 비밀번호가 존재하지 않습니다.");
		}
		// TODO 수정 필요 -> 예외처리,,
		Reserve res;
		try {
			res = reserveService.findByReserveId(passCheckDto.getReserveId());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("없는 예약번호 입니다.");
		}

		Reserve checkedReserve = reserveService.isReserveId(res, passCheckDto.getUserPassword());
		this.reserveService.delete(checkedReserve);
		URI uri = linkTo(ReserveController.class).slash(res.getReserveId()).toUri();
		return ResponseEntity.created(uri).body(res);
	}

	// 비밀번호 확인
	@PostMapping("/checkpw")
	public ResponseEntity checkPassword(@Valid @RequestBody PassCheckDto passCheckDto, Errors error) {
		if (error.hasErrors()) {
			return ResponseEntity.badRequest().body("예약번호 또는 비밀번호를 확인하세요");
		}

		Reserve res = reserveService.findByReserveId(passCheckDto.getReserveId());
		if (res == null)
			return ResponseEntity.badRequest().body("존재하지 않는 예약번호 입니다.");

		Reserve checkedReserve = reserveService.isReserveId(res, passCheckDto.getUserPassword());
		if (checkedReserve == null) {
			return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
		}
		
		URI uri = linkTo(ReserveController.class).slash(res.getReserveId()).toUri();
		return ResponseEntity.created(uri).body(res);
	}
}
