package com.radiuslab.sample.reserve;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
public class Reserve {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reserveId;

	@ManyToOne
	@JoinColumn(name = "roomId")
	private Room room;

	private String userName;
	private String userEmail;
	private String userPassword;
	private int userNum;
	private String title;
	private LocalDate reserveDate;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
}
