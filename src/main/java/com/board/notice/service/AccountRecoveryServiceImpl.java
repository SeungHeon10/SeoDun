package com.board.notice.service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.board.notice.entity.EmailToken;
import com.board.notice.entity.User;
import com.board.notice.repository.EmailRepository;
import com.board.notice.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountRecoveryServiceImpl implements AccountRecoveryService {
	private final UserRepository userRepository;
	private final EmailSenderService emailSenderService;
	private final EmailRepository emailRepository;
	private final PasswordEncoder passwordEncoder;

	// 아이디 찾기 인증코드 메일 전송
	@Override
	@Transactional
	public void sendIdLookupVerificationCode(String email) {
		String code = String.format("%06d", new Random().nextInt(999999));
		EmailToken emailToken = EmailToken.builder().token(code).email(email)
				.expiryDate(LocalDateTime.now().plusMinutes(10)).build();
		emailRepository.save(emailToken);

		String body = String.format(
				"""
						<div
							style="max-width:600px; margin:0 auto; padding:20px; font-family:Arial, sans-serif; background-color:#ffffff; border:1px solid #e0e0e0;">
								<h2 style="color:#333333;">[SeoDun] 아이디 찾기 이메일 인증 요청</h2>

								<p style="font-size:14px; color:#555555; margin-bottom: 50px;">
									아이디 찾기를 완료하려면 아래 코드를 입력해 이메일 인증을 진행해주세요.
								</p>

								<div style="margin:30px 0; text-align:center;">
									<p style="font-size:24px; font-weight:bold; color:#4CAF50;">%s</p>
								</div>

								<p style="font-size:12px; color:#999999; margin-top: 50px;">
									본 메일은 아이디 찾기 과정에서 자동으로 발송된 메일입니다.<br>
									인증 링크는 10분 동안만 유효하니 시간내에 인증을 완료해주세요.
								</p>

								<hr style="border:none; border-top:1px solid #e0e0e0; margin:30px 0;">

								<p style="font-size:12px; color:#999999; text-align:center;">
									© 2025 SeoDun. All rights reserved.
								</p>
							</div>
							""",
				code);
		emailSenderService.sendIdLookupCodeEmail(email, body);
	}

	// 아이디 찾기 인증코드 확인
	@Override
	@Transactional
	public void verifyIdLookupCode(String email, String code) {
		EmailToken resetToken = emailRepository.findByEmailAndTokenAndIsValidTrueAndIsDeletedFalse(email, code)
				.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다. 다시 시도해주세요."));

		if (resetToken.isConfirmed()) {
			throw new IllegalArgumentException("이미 사용된 토큰입니다. 다시 시도해주세요.");
		}

		if (!resetToken.getExpiryDate().isAfter(LocalDateTime.now())) {
			throw new IllegalArgumentException("토큰이 만료되었습니다. 다시 시도해주세요.");
		}

		// 토큰 인증 확인 변경
		resetToken.changeConfirmed();
		// 토큰 무효화
		resetToken.invalidate();
		// 토큰 소프트 삭제
		resetToken.markAsDeleted();
	}

	// 이름 + 이메일로 아이디 찾기
	@Override
	@Transactional
	public String findIdByNameAndEmail(String name, String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("일치하는 회원이 없습니다."));
		
		if(user.getName() != name) {
			new IllegalArgumentException("입력한 이름과 회원 이름이 다릅니다.");
		}
		
		return user.getId();
	}

	// 비밀번호 재설정 메일 전송
	@Override
	@Transactional
	public void sendPasswordResetVerificationEmail(String id, String email) {
		User user = userRepository.findById(id).orElse(null);
		// 해당 유저 없으면 보내지 않음
		if (user == null)
			return;

		// 요청 email과 DB email이 일치하지 않으면 보내지 않음
		if (user.getEmail() == null || !user.getEmail().equalsIgnoreCase(email)) {
			return;
		}

		String token = UUID.randomUUID().toString();
		EmailToken emailToken = EmailToken.builder().token(token).email(user.getEmail())
				.expiryDate(LocalDateTime.now().plusMinutes(10)).build();
		emailRepository.save(emailToken);

		String link = "http://localhost:8080/account/password/reset?token=" + token;
		String body = String.format(
				"""
						<div
						style="max-width:600px; margin:0 auto; padding:20px; font-family:Arial, sans-serif; background-color:#ffffff; border:1px solid #e0e0e0;">
							<h2 style="color:#333333;">[SeoDun] 비밀번호 재설정 요청</h2>

							<p style="font-size:14px; color:#555555; margin-bottom: 50px;">
								비밀번호를 재설정 하려면 아래 버튼을 눌러 비밀번호 재설정을 진행해주세요.
							</p>

							<div style="margin:30px 0; text-align:center;">
								<a href="%s" style="display:inline-block;background:#4CAF50;border:1px solid #4CAF50;color:#fff;
						        	text-decoration:none;font-weight:700;font-size:20px;
						        	padding:5px 24px;border-radius:8px;">비밀번호 재설정</a>
							</div>

							<p style="font-size:12px; color:#999999; margin-top: 50px;">
								본 메일은 비밀번호 찾기 과정에서 자동으로 발송된 메일입니다.<br>
								인증 링크는 10분 동안만 유효하니 시간내에 인증을 완료해주세요.
							</p>

							<hr style="border:none; border-top:1px solid #e0e0e0; margin:30px 0;">

							<p style="font-size:12px; color:#999999; text-align:center;">
								© 2025 SeoDun. All rights reserved.
							</p>
						</div>
						""",
				link);
		emailSenderService.sendPasswordResetEmail(user.getEmail(), body);
	}

	// 토큰 검증 후 비밀번호 변경
	@Override
	@Transactional
	public void confirmReset(String token, String newPassword) {
		EmailToken resetToken = emailRepository.findByTokenAndIsDeletedFalse(token)
				.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다. 다시 시도해주세요."));

		if (resetToken.isConfirmed()) {
			throw new IllegalArgumentException("이미 사용된 토큰입니다. 다시 시도해주세요.");
		}

		if (!resetToken.getExpiryDate().isAfter(LocalDateTime.now())) {
			throw new IllegalArgumentException("토큰이 만료되었습니다. 다시 시도해주세요.");
		}

		// 토큰 인증 확인 변경
		resetToken.changeConfirmed();
		// 토큰 무효화
		resetToken.invalidate();
		// 토큰 소프트 삭제
		resetToken.markAsDeleted();

		User user = userRepository.findByEmail(resetToken.getEmail())
				.orElseThrow(() -> new UsernameNotFoundException("해당 회원은 존재하지 않습니다. 다시 시도해주세요."));
		user.setEncodedPassword(passwordEncoder.encode(newPassword));
	}

}
