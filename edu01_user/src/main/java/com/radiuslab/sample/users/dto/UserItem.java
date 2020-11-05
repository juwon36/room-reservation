package com.radiuslab.sample.users.dto;

import java.time.LocalDate;

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
public class UserItem {
	private Long id;
	private String name;
	private String email;
	private LocalDate birthday;
	private String gender;
}
