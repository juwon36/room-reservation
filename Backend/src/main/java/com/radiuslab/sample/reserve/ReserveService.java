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

	public Reserve findByReserveId(Long reserveId) throws  CException{
		Optional<Reserve> reserve = this.reserveRepository.findById(reserveId);
		if (!reserve.isPresent()) { // Optional의 null체크
			throw new CException( ""+reserveId);
		}
		return reserve.get();
	}

	public Reserve isReserveId(Reserve reserve, String userPassword) throws CException{
		Reserve res = this.findByReserveId(reserve.getReserveId());
		
		if (res.getUserPassword().equals(userPassword)) {			
			return res;
		}
		else {
			throw new CException( "비밀번호가 맞지 않는 예약번호 : "+reserve.getReserveId());
		}

	}

	public void delete(Reserve res) {
		this.reserveRepository.delete(res);
	}
}

/* Custom Exception 생성 */
class CException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CException( String msg) {
		super( msg);
	}
}
