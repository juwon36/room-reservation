package com.radiuslab.sample.reserve;

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
public class PassCheckDto {
	
	@NotNull
	private Long reserveId;
	
	@NotEmpty
	@Size(min = 4)
	private String userPassword;
}
