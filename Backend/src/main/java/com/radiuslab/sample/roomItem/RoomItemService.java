package com.radiuslab.sample.roomItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RoomItemService {
	
	@Autowired
	private RoomItemRepository roomItemRepository;


}
