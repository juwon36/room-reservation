package com.radiuslab.sample.room;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room")
public class RoomController {

	Logger LOGGER = LoggerFactory.getLogger(RoomController.class);

	@Autowired
	private RoomService roomService;

	// 회의실 전체 조회
	@GetMapping
	public ResponseEntity<List<Room>> findAll() {
		List<Room> roomList = this.roomService.findAll();

		return new ResponseEntity<List<Room>>(roomList, HttpStatus.OK);
	}

}
