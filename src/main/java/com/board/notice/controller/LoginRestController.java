package com.board.notice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import com.board.notice.dto.request.LoginRequestDTO;
import com.board.notice.dto.response.TokenResponseDTO;
import com.board.notice.security.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
public class LoginRestController {
	private final JwtUtil jwtUtil;
	private final AuthenticationManager authenticationManager;
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
		try {
			// Security로 인증 
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getId(), request.getPassword()));
			// 인증 성공 시 사용자 정보 가져오기
			UserDetails user = (UserDetails) authentication.getPrincipal();
			// 토큰 생성(JWT)
			String token = jwtUtil.createToken(user.getUsername(), jwtUtil.extractRole(user.getAuthorities()));
			
			return ResponseEntity.ok(new TokenResponseDTO(token));
		} catch(AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 정보가 올바르지 않습니다. 다시 시도해주세요.");
		}
	}
	
}
