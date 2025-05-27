package com.board.notice.security.jwt;

import java.util.Collection;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.board.notice.enums.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {
	private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
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
	
	public String getId(String token) {
		return getClaims(token).getSubject();
	}
	
	public Role getRole(String token) {
		return Role.valueOf(getClaims(token).get("Role", String.class));
	}
	
	public Role extractRole(Collection<? extends GrantedAuthority> authorities) {
		for(GrantedAuthority authority : authorities) {
			String rolename = authority.getAuthority();
			return Role.valueOf(rolename);
		}
		throw new RuntimeException("권한이 존재하지 않습니다.");
	}
}
