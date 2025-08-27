package com.board.notice.service;

import java.util.concurrent.CompletableFuture;

public interface EmailSenderService {
	// 이메일 인증 메일 전송
	public CompletableFuture<Boolean> sendVerificationEmail(String email);

	// 비밀번호 재설정 메일 전송
	public CompletableFuture<Boolean> sendPasswordResetEmail(String email, String body);
	
	// 아이디 찾기 인증코드 메일 전송
	public CompletableFuture<Boolean> sendIdLookupCodeEmail(String email, String body);
}
