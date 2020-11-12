package com.radiuslab.sample.roomItem;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.radiuslab.sample.room.Room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RoomItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long itemId;

	@ManyToOne
	@JoinColumn(name = "roomId")
	private Room room;

	private String itemName;
	private int itemNum;
}
