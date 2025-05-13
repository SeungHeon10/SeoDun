package com.board.notice.service;

import jakarta.mail.MessagingException;

public interface EmailService {
//	이메일 인증 토큰 보내기
	void sendVerificationEmail(String email) throws MessagingException;
//	이메일 인증 확인
	void confirmToken(String token);
//	토큰 만료 확인
	boolean isTokenExpired(String token);
}
