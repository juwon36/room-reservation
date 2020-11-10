package com.radiuslab.sample.reserve;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReserveRepository extends JpaRepository<Reserve, Long> {

	// 메인페이지 - 일간 전체회의실 조회
	public List<Reserve> findByReserveDate(LocalDate reserveDate);

	// 회의실id와 해당년도, 월에 해당하는 예약현황을 리턴한다
	@Query(value = "select rs from Reserve rs, Room rm where rm.roomId = ?1 and TO_CHAR(rs.reserveDate, 'YYYY') = ?2 and TO_CHAR(rs.reserveDate, 'MM') = ?3 ")
	public List<Reserve> findAllByRoomIdAndYearMonth(Long roomId, String year, String month);

	// 회의실 id와 해당날짜에 해당하는 예약
	@Query("select rs from Reserve rs, Room rm where rm.roomId = ?2 and rs.reserveDate = ?1")
	public List<Reserve> findByReserveDateAndRoomId(LocalDate date, Long roomId);

}
