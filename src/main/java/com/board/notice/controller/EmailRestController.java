package com.board.notice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.board.notice.service.EmailService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/emails")
public class EmailRestController {
	private final EmailService emailService;
	
//	이메일 인증 토큰 보내기
	@PostMapping
	public ResponseEntity<String> sendEmail(@RequestParam String email) {
		emailService.sendVerificationEmail(email);
		return ResponseEntity.ok("인증 메일이 전송되었습니다.");
	}
	
//	이메일 인증 확인
	@GetMapping("/verify/{token}")
	public ResponseEntity<String> verifyToken(@PathVariable("token") String token) {
		emailService.confirmToken(token);
		return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
	}
	
//	이메일 인증 토큰 재전송
	@PostMapping("/resend")
	public ResponseEntity<String> resendEmail(@RequestParam String email) {
		emailService.resendVerificationEmail(email);
		return ResponseEntity.ok("인증 메일이 재전송 되었습니다.");
	}
	
}
