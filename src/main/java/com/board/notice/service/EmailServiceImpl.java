package com.board.notice.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.board.notice.entity.EmailToken;
import com.board.notice.entity.User;
import com.board.notice.repository.EmailRepository;
import com.board.notice.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
	private final EmailRepository emailRepository;
	private final EmailSenderServiceImpl emailSenderService;
	private final UserRepository userRepository;

//	이메일 인증 확인
	@Override
	@Transactional
	public void confirmToken(String email, String code) {
		// DB에서 토큰 검색
		EmailToken emailToken = emailRepository.findByEmailAndTokenAndIsValidTrueAndIsDeletedFalse(email, code)
				.orElseThrow(() -> new EntityNotFoundException("해당 토큰을 찾을 수 없습니다."));

		// 이미 인증된 토큰인지 확인
		if (emailToken.isConfirmed() == true) {
			throw new IllegalStateException("이미 인증된 토큰입니다.");
		}

		// 인증 시간 만료된 토큰인지 확인
		if (emailToken.getExpiryDate().isBefore(LocalDateTime.now())) {
			throw new IllegalStateException("인증 시간이 만료된 토큰입니다.");
		}

		
		// 토큰 인증 확인 변경
		emailToken.changeConfirmed();
		// 토큰 무효화
		emailToken.invalidate();
		// 토큰 소프트 삭제
		emailToken.markAsDeleted();

		// 회원 조회
		User user = userRepository.findByEmail(email)
				.orElse(null);
		
		if(user != null) {
			user.changeEmailVerified();
		}

	}

//	이메일 인증 토큰 재전송
	@Override
	@Transactional
	public void resendVerificationEmail(String email) {
		// emailToken 검색
		EmailToken emailToken = emailRepository.findByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException("해당 토큰을 찾을 수 없습니다."));

		// 이미 인증된 토큰인지 확인
		if (emailToken.isConfirmed() == true) {
			throw new IllegalStateException("이미 인증된 회원입니다.");
		}

		// emailToken 무효화 메서드
		emailToken.invalidate();
		// 해당 토큰 소프트 삭제처리
		emailToken.markAsDeleted();
		;

		// 이메일 인증 토큰 보내기
		emailSenderService.sendVerificationEmail(email);
	}
}
