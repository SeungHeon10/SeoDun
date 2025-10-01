package com.board.notice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

//	로그인 페이지 이동
	@GetMapping("/login")
	public String loginPage() {

		return "auth/login";
	}

//	비밀번호 찾기 페이지 이동
	@GetMapping("/recovery/password")
	public String recoveryPassword() {

		return "auth/recovery-password";
	}

//	아이디 찾기 페이지 이동
	@GetMapping("/recovery/id")
	public String recoveryId() {

		return "auth/recovery-id";
	}

//	비밀번호 재설정 페이지 이동
	@GetMapping("/account/password/reset")
	public String PasswordResetPage() {

		return "auth/passwordReset";
	}

//	회원가입 페이지 이동
	@GetMapping("/membership")
	public String membershipPage() {

		return "auth/membership";
	}

//	소셜 추가정보 입력 페이지
	@GetMapping("/signup-extra")
	public String signupExtraPage() {
		return "auth/membership-extra";
	}

//	회원가입 완료 시 페이지 이동
	@GetMapping("/succ-member")
	public String succMemberPage() {

		return "auth/successmembership";
	}

}
