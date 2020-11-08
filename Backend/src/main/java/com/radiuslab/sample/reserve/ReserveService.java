package com.radiuslab.sample.reserve;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReserveService {
	@Autowired
	private ReserveRepository reserveRepository;
}
