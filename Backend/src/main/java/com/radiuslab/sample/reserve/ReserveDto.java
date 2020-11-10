package com.radiuslab.sample.reserve;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ReserveDto {
	private Long reserveId;

	@NotNull
	private Long roomId;

	@NotEmpty
	private String userName;

	@NotEmpty
	@Email
	private String userEmail;

	@NotEmpty
	@Size(min = 4)
	private String userPassword;

	@NotNull
	@Min(value = 0)
	private int userNum;

	@NotEmpty
	private String title;

	@NotNull
	private LocalDate reserveDate;

	@NotNull
	private LocalDateTime startTime;

	@NotNull
	private LocalDateTime endTime;

	public void update() {
		if (this.startTime.toLocalTime().isBefore(LocalTime.of(8, 00))) {
			this.startTime = this.startTime.withHour(8).withMinute(0);
		}
		if (this.endTime.toLocalTime().isAfter(LocalTime.of(20, 00))) {
			this.endTime = this.endTime.withHour(20).withMinute(0);
		}
		this.startTime = this.startTime.withSecond(0);
		this.endTime = this.endTime.withSecond(0).minusSeconds(1);
	}
}
