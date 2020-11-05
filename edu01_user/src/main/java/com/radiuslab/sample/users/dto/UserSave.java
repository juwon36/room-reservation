package com.radiuslab.sample.users.dto;

import java.time.LocalDate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
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
public class UserSave {
	@NotEmpty(message = "이름을 입력해주세요.")
	@Size(max = 20, message = "이름은 20자 이하여야 합니다.")
	private String name;

	@NotEmpty(message = "이메일을 입력해주세요.")
	@Email(message = "이메일 형식이 올바르지 않습니다.")
	private String email;

	@NotEmpty(message = "비밀번호를 입력해주세요.(영문자/숫자/특수문자)")
	@Pattern(regexp = "((?=.*\\d)(?=.*[A-Za-z])(?=.*[@#$%*!^]).{8,32})", message = "비밀번호가 올바르지 않습니다.")
	private String password;

	@Past(message = "생년월일이 올바르지 않습니다.")
	private LocalDate birthday;

	@NotEmpty(message = "성별을 입력해주세요.")
	@Pattern(regexp = "male|female|none", message = "성별이 올바르지 않습니다.")
	private String gender;
}
