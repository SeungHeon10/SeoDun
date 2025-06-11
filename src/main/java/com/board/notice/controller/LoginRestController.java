package com.board.notice.controller;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.board.notice.dto.request.LoginRequestDTO;
import com.board.notice.dto.response.TokenResponseDTO;
import com.board.notice.enums.Role;
import com.board.notice.security.jwt.JwtUtil;
import com.board.notice.service.RefreshTokenService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class LoginRestController {
	private final JwtUtil jwtUtil;
	private final AuthenticationManager authenticationManager;
	private final RefreshTokenService refreshTokenService;

//	로그인 버튼 누를 시
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
		try {
			// Security로 인증
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(request.getId(), request.getPassword()));
			// 인증 성공 시 사용자 정보 가져오기
			UserDetails user = (UserDetails) authentication.getPrincipal();
			// 토큰 생성(JWT)
			String token = jwtUtil.createToken(user.getUsername(), jwtUtil.extractRole(user.getAuthorities()));
			String refreshToken = jwtUtil.createRefreshToken(user.getUsername(),
					jwtUtil.extractRole(user.getAuthorities()));

			ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken).httpOnly(true).secure(false) // 배포시
																													// true로
																													// 변경
					.path("/").maxAge(Duration.ofDays(7)).build();

			return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
					.body(new TokenResponseDTO(token));
		} catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 정보가 올바르지 않습니다. 다시 시도해주세요.");
		}
	}

//	accessToken 재생성 API
	@PostMapping("/token")
	public ResponseEntity<?> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
		if (refreshToken != null && jwtUtil.isTokenValid(refreshToken)) {
			String id = jwtUtil.getId(refreshToken);
			Role role = jwtUtil.getRole(refreshToken);
			String newAccessToken = jwtUtil.createToken(id, role);
			return ResponseEntity.ok(new TokenResponseDTO(newAccessToken));
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("해당 RefreshToken은 유효하지 않은 토큰입니다.");
		}
	}

//	로그아웃
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletResponse response,
			@CookieValue(name = "refreshToken", required = false) String refreshToken,
			@RequestHeader(name = "Authorization", required = false) String accessTokenHeader) {
		
	    if (refreshToken != null && accessTokenHeader != null && accessTokenHeader.startsWith("Bearer ")) {
	        String accessToken = accessTokenHeader.substring(7);
	        String id = jwtUtil.getId(accessToken);
	        refreshTokenService.delete(id);
	    }

	    // 쿠키 삭제
		ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "").httpOnly(true).secure(false).path("/")
				.maxAge(0) // 즉시 만료
				.build();

		response.addHeader("Set-Cookie", deleteCookie.toString());

		return ResponseEntity.ok().build();
	}

}
