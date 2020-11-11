package com.radiuslab.sample.reserve;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReserveService {
	@Autowired
	private ReserveRepository reserveRepository;

	@Autowired
	private ModelMapper modelMapper;

	public Reserve save(ReserveDto dto) {
		Reserve reserve = this.modelMapper.map(dto, Reserve.class);
		Reserve res = this.reserveRepository.save(reserve);
		return res;
	}

	public List<Reserve> findByReserveDate(String reserveDate) {
		LocalDate date = LocalDate.parse(reserveDate);
		List<Reserve> reserveList = this.reserveRepository.findByReserveDate(date);
		return reserveList;
	}

	public List<Reserve> findByRoomIdAndYearMonth(String roomId, String year, String month) {
		Long longRoomId = (long) Integer.parseInt(roomId);
		List<Reserve> reserveList = this.reserveRepository.findAllByRoomIdAndYearMonth(longRoomId, year, month);
		return reserveList;
	}

	public List<Reserve> findByReserveDateAndRoomId(String reserveDate, Long roomId) {
		LocalDate date = LocalDate.parse(reserveDate);
		List<Reserve> reserveList = this.reserveRepository.findByReserveDateAndRoomId(date, roomId);
		return reserveList;
	}

	public Reserve findByReserveId(Long reserveId) {
		Optional<Reserve> reserve = this.reserveRepository.findById(reserveId);
		// Reserve res = reserve.get();
		// if (res == null) return null;
		if (reserve.isPresent()) { // Optional의 null체크
			return reserve.get();
		}
		return null;
	}

	public Reserve isReserveId(Reserve reserve, String userPassword) {
		Reserve res = this.findByReserveId(reserve.getReserveId());
		if (res.getUserPassword().equals(userPassword))
			return res;
		// return throws notMatchPasswordException;
		return null;
	}

	public void delete(Reserve res) {
		this.reserveRepository.delete(res);
	}
}
