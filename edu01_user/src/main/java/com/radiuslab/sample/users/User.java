package com.radiuslab.sample.users;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User {
	@Id
	@GeneratedValue
	private Long id;

	private String name;
	private String email;
	private String password;
	private LocalDate birthday;
	private String gender;
	// private Integer age;
	// private boolean isAdult;

	// public void update() {
	// if (age > 19) {
	// this.isAdult = true;
	// } else {
	// this.isAdult = false;
	// }
	// }
}
