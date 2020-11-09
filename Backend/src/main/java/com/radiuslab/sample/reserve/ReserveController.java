package com.radiuslab.sample.reserve;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/api/reserve")
public class ReserveController {

	@Autowired
	private ReserveService reserveService;

	@GetMapping("{reserveDate}/all")
	public ResponseEntity<List<Reserve>> findByReserveDate(@PathVariable String reserveDate) {
		List<Reserve> reserveList = this.reserveService.findByReserveDate(reserveDate);
		return new ResponseEntity<List<Reserve>>(reserveList, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<List<Reserve>> checkMonthlyReserve(@RequestParam String roomId, @RequestParam String year, @RequestParam String month) {
		List<Reserve> reserveList = this.reserveService.findByRoomIdAndYearMonth(roomId, year, month);
		return new ResponseEntity<List<Reserve>>(reserveList, HttpStatus.OK);
	}
	
	@GetMapping("{reserveDate}/{roomId}")
	public ResponseEntity<List<Reserve>> findByReserveDateAndRoomId(@PathVariable String reserveDate, @PathVariable Long roomId) {
//		if(roomId<0 || roomId > 4) {
//			return ResponseEntity.badRequest().body(error);
//		}
		List<Reserve> reserveList = this.reserveService.findByReserveDateAndRoomId(reserveDate, roomId);
		return new ResponseEntity<List<Reserve>>(reserveList, HttpStatus.OK);
	}

}
