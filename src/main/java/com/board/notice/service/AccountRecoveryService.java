package com.board.notice.service;

public interface AccountRecoveryService {
	// 아이디 찾기 인증코드 메일 전송
	public void sendIdLookupVerificationCode(String email);
	
	// 아이디 찾기 인증코드 확인
	public void verifyIdLookupCode(String email,String code);
	
	// 이름 + 이메일로 아이디 찾기
	public String findIdByNameAndEmail(String name,String email);
	
	// 비밀번호 재설정 메일 전송
	public void sendPasswordResetVerificationEmail(String id, String email);
	
	// 토큰 확인 후 비밀번호 변경
	public void confirmReset(String token, String newPassword);
}
