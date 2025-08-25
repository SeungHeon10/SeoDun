package com.board.notice.service;

public interface AccountRecoveryService {
	// 아이디 찾기
	public void sendUsernameToEmailIfMatched(String name, String email);
	
	// 비밀번호 재설정 메일 전송
	public void issueResetEmailIfMatched(String id, String email);
	
	// 토큰 확인 후 비밀번호 변경
	public void confirmReset(String token, String newPassword);
}
