package com.board.notice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.board.notice.dto.request.IdRecoveryRequestDTO;
import com.board.notice.dto.request.PasswordRecoveryRequestDTO;
import com.board.notice.dto.request.PasswordResetConfirmRequestDTO;
import com.board.notice.service.AccountRecoveryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountRecoveryRestController {
	private final AccountRecoveryService accountRecoveryService;

	// 아이디 찾기
	@PostMapping(value = "/id/recovery", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> recoverId(@RequestBody IdRecoveryRequestDTO request) {
		accountRecoveryService.sendUsernameToEmailIfMatched(request.getName(), request.getEmail());
		return ResponseEntity.ok("입력하신 주소로 안내를 보냈습니다.");
	}

	// 비밀번호 재설정 메일 요청
	@PostMapping(value = "/password/reset/request", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> requestPasswordReset(@RequestBody PasswordRecoveryRequestDTO request) {
		accountRecoveryService.issueResetEmailIfMatched(request.getId(), request.getEmail());

		return ResponseEntity.ok("입력하신 주소로 재설정 메일을 보냈습니다.");
	}

	// 토큰 확인 후 비밀번호 변경
	@PostMapping(value = "/password/reset/confirm", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> confirmPasswordReset(@RequestBody PasswordResetConfirmRequestDTO request) {
		try {
			accountRecoveryService.confirmReset(request.getToken(), request.getNewPassword());
			return ResponseEntity.ok("비밀번호가 변경되었습니다.");
		} catch(IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (UsernameNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
}
