package com.board.notice.service;

import java.time.Duration;

public interface RefreshTokenService {
//	RefreshToken 저장
	void saveRefreshToken(String id, String refreshToken, Duration duration);
//	유효한 토큰인지 확인
    boolean isValid(String id, String refreshToken);
//  RefreshToken 삭제
    void delete(String id);
}
