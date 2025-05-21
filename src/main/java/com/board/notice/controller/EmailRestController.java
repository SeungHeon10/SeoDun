package com.board.notice.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.board.notice.service.EmailSenderService;
import com.board.notice.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/emails")
@Slf4j
public class EmailRestController {
	private final EmailService emailService;
	private final EmailSenderService emailSenderService;
	
//	이메일 인증 토큰 보내기
	@PostMapping
	public CompletableFuture<ResponseEntity<String>> sendEmail(@RequestParam("email") String email) {
		return emailSenderService.sendVerificationEmail(email).thenApply(success -> {
			if(success) {
				log.info("✅ 인증 메일 전송 성공 - 대상: {}", email);
				return ResponseEntity.ok("인증 메일이 전송되었습니다.");
			} else {
				log.warn("❌ 이메일 인증 전송 실패 - 대상: {}", email);
				return ResponseEntity.ok("인증 메일 전송에 실패했습니다. 다시 시도해주세요.");
			}
		});
	}
	
//	이메일 인증 확인
	@GetMapping("/verification-tokens/{token}")
	public ResponseEntity<String> verifyToken(@PathVariable("token") String token) {
		emailService.confirmToken(token);
		return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
	}
	
//	이메일 인증 토큰 재전송
	@PostMapping("/verification-tokens")
	public ResponseEntity<String> resendEmail(@RequestParam("email") String email) {
		emailService.resendVerificationEmail(email);
		return ResponseEntity.ok("인증 메일이 재전송 되었습니다.");
	}
	
}
