package com.board.notice.service;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
//	이메일 인증 토큰 보내기
	CompletableFuture<Boolean> sendVerificationEmail(String email);
//	이메일 인증 확인
	void confirmToken(String token);
//	이메일 인증 토큰 재전송
	void resendVerificationEmail(String email);
}
