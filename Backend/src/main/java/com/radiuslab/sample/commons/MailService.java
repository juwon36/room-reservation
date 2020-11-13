package com.radiuslab.sample.commons;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.radiuslab.sample.reserve.Reserve;

@Component
public class MailService {
	@Autowired
	private JavaMailSender mailSender;
	private static final String FROM_ADDRESS = "springprojecttestemail@gmail.com";

	public void mailSend(Reserve reserve) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(reserve.getUserEmail());
		message.setFrom(MailService.FROM_ADDRESS);
		message.setSubject("Radius Lab 회의실 예약 안내");

		StringBuilder sb = new StringBuilder();
		sb.append("예약자: " + reserve.getUserName() + "\n");
		// sb.append("이메일: " + reserve.getUserEmail() + "\n");
		sb.append("비밀번호(분실시 예약 수정/취소 불가): " + reserve.getUserPassword() + "\n");
		sb.append("인원: " + reserve.getUserNum() + "\n");
		sb.append("예약일: " + reserve.getReserveDate() + "\n");
		sb.append("예약시간: " + reserve.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("a hh:mm")) + " ~ "
				+ reserve.getEndTime().toLocalTime().plusMinutes(1).format(DateTimeFormatter.ofPattern("a hh:mm"))
				+ "\n");
		message.setText(sb.toString());

		mailSender.send(message);
	}
}
