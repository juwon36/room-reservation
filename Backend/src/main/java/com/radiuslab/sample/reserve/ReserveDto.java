package com.radiuslab.sample.reserve;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReserveDto {
	private Long reserveId;

	// private Room room;
	private Long roomId;

	private String userName;
	private String userEmail;
	private String userPassword;
	private int userNum;
	private String title;
	private LocalDate reserveDate;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
}
