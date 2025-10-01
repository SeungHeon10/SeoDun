package com.board.notice.service;

public interface EmailService {
//	이메일 인증 확인
	void confirmToken(String email, String code);
//	이메일 인증 토큰 재전송
	void resendVerificationEmail(String email);
}
