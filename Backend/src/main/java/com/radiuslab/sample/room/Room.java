package com.radiuslab.sample.room;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Proxy;

import com.radiuslab.sample.roomItem.RoomItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Proxy(lazy=false) // 매핑을 통해 lazy로딩되므로 캐싱시 문제가 발생하지 않도록 proxy false를 설정한다.
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
