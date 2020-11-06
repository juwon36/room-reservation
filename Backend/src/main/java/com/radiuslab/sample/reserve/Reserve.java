package com.radiuslab.sample.reserve;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "reserve")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Reserve {
	@Id
	@GeneratedValue
	private Long id;

}
