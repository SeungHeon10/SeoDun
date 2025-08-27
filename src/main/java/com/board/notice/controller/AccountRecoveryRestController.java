package com.board.notice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.board.notice.dto.request.IdLookupVerifyRequestDTO;
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

	// 아이디 찾기 인증코드 메일 전송
	@PostMapping(value = "/id/recovery/{email}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> requestIdLookupCode(@PathVariable("email") String email) {
		accountRecoveryService.sendIdLookupVerificationCode(email);
		return ResponseEntity.ok("입력하신 주소로 인증코드를 보냈습니다.");
	}

	// 아이디 찾기 인증코드 확인
	@PostMapping("/id/recovery/confirm")
	public ResponseEntity<String> verifyIdLookupCode(@RequestBody IdLookupVerifyRequestDTO request) {
		try {
			accountRecoveryService.verifyIdLookupCode(request.getEmail(), request.getCode());
			return ResponseEntity.ok("인증번호가 일치합니다.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// 아이디 찾기
	@PostMapping("/id/recovery")
	public ResponseEntity<String> recoverIdByNameAndEmail(@RequestBody IdRecoveryRequestDTO request) {
		try {
            String response = accountRecoveryService.findIdByNameAndEmail(request.getName(), request.getEmail());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
	}

	// 비밀번호 재설정 메일 요청
	@PostMapping(value = "/password/reset/request", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> requestPasswordReset(@RequestBody PasswordRecoveryRequestDTO request) {
		accountRecoveryService.sendPasswordResetVerificationEmail(request.getId(), request.getEmail());

		return ResponseEntity.ok("입력하신 주소로 재설정 메일을 보냈습니다.");
	}

	// 토큰 확인 후 비밀번호 변경
	@PostMapping(value = "/password/reset/confirm", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> confirmPasswordReset(@RequestBody PasswordResetConfirmRequestDTO request) {
		try {
			accountRecoveryService.confirmReset(request.getToken(), request.getNewPassword());
			return ResponseEntity.ok("비밀번호가 변경되었습니다.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
}
