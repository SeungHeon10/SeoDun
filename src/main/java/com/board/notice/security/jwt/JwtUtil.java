package com.board.notice.security.jwt;

import java.util.Date;

import com.board.notice.enums.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtUtil {
	private final long accessTokenTime = 1000 * 60 * 30;
	private final long refreshTokenTime = 1000 * 60 * 60 * 24 * 7;

	public String createToken(String id, Role role) {

		return Jwts.builder()
				.setSubject(id)
				.claim("Role", role)
				.setExpiration(new Date(System.currentTimeMillis() + accessTokenTime))
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();
	}
	
	public String createRefreshToken(String id, Role role) {
		
		return Jwts.builder()
				.setSubject(id)
				.setExpiration(new Date(System.currentTimeMillis() + refreshTokenTime))
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();
	}
	
	public Claims getClaims(String token) {
		
		return Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
	
	public boolean isTokenValid(String token) {
		try {
			getClaims(token);
			return true;
		} catch(Exception e) {
			log.warn("토큰이 유효하지 않습니다.");
			return false;
		}
	}
}
