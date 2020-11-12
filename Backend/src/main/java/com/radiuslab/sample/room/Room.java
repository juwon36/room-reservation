package com.radiuslab.sample.room;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.radiuslab.sample.roomItem.RoomItem;

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
public class Room {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long roomId;

	private String roomName;
	private int capacity;

	@Builder.Default
	@OneToMany(mappedBy = "room") // mapperBy를 연결해두지 않으면, 둘을 매핑하는 새로운 테이블이 생성된다.
	private List<RoomItem> items = new ArrayList<>();
}
