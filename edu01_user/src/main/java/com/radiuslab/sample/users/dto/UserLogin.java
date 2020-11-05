package com.radiuslab.sample.users.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

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
public class UserLogin {
	@NotEmpty(message = "이메일을 입력해주세요.")
	@Email(message = "이메일 형식이 올바르지 않습니다.")
	private String email;

	@NotEmpty(message = "비밀번호를 입력해주세요.(영문자/숫자/특수문자)")
	private String password;
}
