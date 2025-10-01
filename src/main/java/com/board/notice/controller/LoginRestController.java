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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.board.notice.dto.request.LoginRequestDTO;
import com.board.notice.dto.response.TokenResponseDTO;
import com.board.notice.enums.Role;
import com.board.notice.security.CustomUserDetail;
import com.board.notice.security.jwt.JwtUtil;
import com.board.notice.service.RefreshTokenService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

			ResponseCookie cookie = null;

			if (request.isRememberMe() == true) {
				String refreshToken = jwtUtil.createRefreshToken(user.getUsername(),
						jwtUtil.extractRole(user.getAuthorities()));

				cookie = ResponseCookie.from("refreshToken", refreshToken).httpOnly(true).secure(true) // 배포시
																										// true로
																										// 변경
						.path("/").maxAge(Duration.ofDays(7)).build();
			}

			ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok();
			if (cookie != null) {
				responseBuilder.header(HttpHeaders.SET_COOKIE, cookie.toString());
			}

			return responseBuilder.body(new TokenResponseDTO(token));
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

		try {
			if (refreshToken != null) {
			      try { refreshTokenService.deleteByToken(refreshToken); } catch (Exception ignore) {}
			}
			
			if (refreshToken != null && accessTokenHeader != null && accessTokenHeader.startsWith("Bearer ")) {
				String accessToken = accessTokenHeader.substring(7);

				try {
					String id = jwtUtil.getId(accessToken);
					refreshTokenService.deleteByUserId(id); // 존재 안 해도 예외 던지지 않게 처리
				} catch (Exception e) {
					// 로그만 남기고 계속 진행
					log.error("Logout: unexpected error while clearing tokens", e);
				}
			}
		} finally {
			ResponseCookie delete = ResponseCookie.from("refreshToken", "").httpOnly(true).secure(true) // HTTPS면 true
					.path("/").maxAge(0).build();
			response.addHeader(HttpHeaders.SET_COOKIE, delete.toString());
			SecurityContextHolder.clearContext();
		}
		
		return ResponseEntity.noContent().build();
	}

//	사용자 인증 확인
	@GetMapping("/auth/check")
	public ResponseEntity<?> checkAuthentication(@AuthenticationPrincipal CustomUserDetail userDetails) {
		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
		}

		return ResponseEntity.ok().build(); // 인증된 사용자면 200 OK
	}

}
