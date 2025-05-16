package com.board.notice.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.board.notice.entity.EmailToken;
import com.board.notice.entity.User;
import com.board.notice.repository.EmailRepository;
import com.board.notice.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
	private final EmailRepository emailRepository;
	private final UserRepository userRepository;
	private final JavaMailSender javaMailSender;

//	이메일 인증 토큰 보내기
	@Override
	@Transactional
	@Async
	public void sendVerificationEmail(String email) {
		try {
			// 토큰 생성 후 DB 반영
			String token = UUID.randomUUID().toString();
			EmailToken emailToken = EmailToken.builder().token(token).email(email)
					.expiryDate(LocalDateTime.now().plusMinutes(10)).build();
			emailRepository.save(emailToken);

			String content = String.format(
					"""
							<div
							style="max-width:600px; margin:0 auto; padding:20px; font-family:Arial, sans-serif; background-color:#ffffff; border:1px solid #e0e0e0;">
								<h2 style="color:#333333;">[SeoDun] 이메일 인증 요청</h2>

								<p style="font-size:14px; color:#555555; margin-bottom: 50px;">
									안녕하세요 !<br>
									회원가입을 완료하려면 아래 버튼을 클릭해 이메일 인증을 진행해주세요.
								</p>

								<div style="margin:30px 0; text-align:center;">
									<a href="http://localhost:8080/auth/emails/verification-tokens/%s"
										style="display:inline-block; background-color:#4CAF50; color:white; padding:12px 24px; text-decoration:none; border-radius:4px; font-weight:bold;">
										이메일 인증하기
									</a>
								</div>

								<p style="font-size:12px; color:#999999; margin-top: 50px;">
									본 메일은 회원가입 과정에서 자동으로 발송된 메일입니다.<br>
									인증 링크는 10분 동안만 유효하니 시간내에 인증을 완료해주세요.
								</p>

								<hr style="border:none; border-top:1px solid #e0e0e0; margin:30px 0;">

								<p style="font-size:12px; color:#999999; text-align:center;">
									© 2025 SeoDun. All rights reserved.
								</p>
							</div>
							""",
					token);
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper;
			helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setTo(email);
			helper.setFrom("sh120404@naver.com");
			helper.setSubject("[SeoDun] 이메일 인증 요청");
			helper.setText(content, true);

			javaMailSender.send(message);
		} catch (MessagingException e) {
			log.error("이메일 인증 전송 실패 - 대상: {}, 메시지: {}", email, e.getMessage(), e);
			e.printStackTrace();
		}
	}

//	이메일 인증 확인
	@Override
	@Transactional
	public void confirmToken(String token) {
		// DB에서 토큰 검색
		EmailToken emailToken = emailRepository.findById(token)
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
		emailToken.markAsDeleted();;
		
		// 해당 회원 찾아서 이메일 인증 완료 처리
		User user = userRepository.findByEmail(emailToken.getEmail())
				.orElseThrow(() -> new UsernameNotFoundException("해당 회원은 존재하지 않습니다."));
		user.changeEmailVerified();
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
		emailToken.markAsDeleted();;
		
		// 이메일 인증 토큰 보내기
		sendVerificationEmail(email);
	}
}
