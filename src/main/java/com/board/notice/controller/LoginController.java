package com.board.notice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class LoginController {
	
//	로그인 페이지 이동
	@GetMapping("/login")
	public String loginPage() {
		
		return "login";
	}
	
//	회원가입 페이지 이동
	@GetMapping("/membership")
	public String membershipPage() {
		
		return "membership";
	}
	
//	소셜 추가정보 입력 페이지
	@GetMapping("/signup-extra")
	public String signupExtraPage() {
		return "membership-extra";
	}
	
	
//	회원가입 완료 시 페이지 이동
	@GetMapping("/succ-member")
	public String succMemberPage() {
		
		return "successmembership";
	}
	
	
}
