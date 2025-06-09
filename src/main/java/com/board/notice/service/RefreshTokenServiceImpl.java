package com.board.notice.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService{
	private final RedisTemplate<String, String> redisTemplate;
	
	@Override
	public void saveRefreshToken(String id, String refreshToken, Duration duration) {
		redisTemplate.opsForValue().set("refresh:" + id, refreshToken, duration);
	}

	@Override
	public boolean isValid(String id, String refreshToken) {
		String stored = redisTemplate.opsForValue().get("refresh:" + id);
		return refreshToken.equals(stored);
	}

	@Override
	public void delete(String id) {
		redisTemplate.delete("refresh" + id);
	}

}
