package com.radiuslab.sample.roomItem;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
public class RoomItemService {
	
	@Autowired
	private RoomItemRepository roomItemRepository;


}
