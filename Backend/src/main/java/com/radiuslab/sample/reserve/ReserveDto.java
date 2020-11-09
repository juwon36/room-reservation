package com.radiuslab.sample.reserve;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
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
}
