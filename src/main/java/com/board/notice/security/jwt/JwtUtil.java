package com.board.notice.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.board.notice.enums.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {
	private final SecretKey secretKey = Keys
			.hmacShaKeyFor("yourVerySecureKeyThatIsAtLeast32BytesLong!!!".getBytes(StandardCharsets.UTF_8));
	private final long accessTokenTime = 1000 * 60 * 30;
	private final long refreshTokenTime = 1000 * 60 * 60 * 24 * 7;

//	토큰 생성
	public String createToken(String id, Role role) {

		return Jwts.builder().setSubject(id).claim("Role", role).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + accessTokenTime))
				.signWith(SignatureAlgorithm.HS256, secretKey).compact();
	}

//	ReFresh 토큰 생성
	public String createRefreshToken(String id, Role role) {

		return Jwts.builder().setSubject(id).claim("Role", role).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + refreshTokenTime))
				.signWith(SignatureAlgorithm.HS256, secretKey).compact();
	}

//	클라임 가져오기
	public Claims getClaims(String token) {

		return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
	}

//	토큰 유효한지 여부 확인
	public boolean isTokenValid(String token) {
		try {
			getClaims(token);
			return true;
		} catch (ExpiredJwtException e) {
			log.warn("토큰이 만료되었습니다.");
			return false;
		} catch (UnsupportedJwtException e) {
			log.warn("토큰 형식이 지원되지 않습니다.");
			return false;
		} catch (MalformedJwtException e) {
			log.warn("토큰 구조가 잘못되었습니다.");
			return false;
		} catch (SignatureException e) {
			log.warn("시그니처가 불일치합니다.");
			return false;
		}
	}

//	아이디 가져오기
	public String getId(String token) {
		return getClaims(token).getSubject();
	}

//	롤(권한) 가져오기
	public Role getRole(String token) {
		return Role.valueOf(getClaims(token).get("Role", String.class));
	}

//	권한 추출하기
	public Role extractRole(Collection<? extends GrantedAuthority> authorities) {
		for (GrantedAuthority authority : authorities) {
			String rolename = authority.getAuthority();
			return Role.valueOf(rolename);
		}
		throw new RuntimeException("권한이 존재하지 않습니다.");
	}
}
