package com.radiuslab.sample.roomItem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoomItemRepository extends JpaRepository<RoomItem, Long> {

	List<RoomItem> findAll();

}
