package com.board.notice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping(value = "/user")
public class UserController {
	
//	회원 관리(admin)
	@GetMapping("/list/admin")
	public String list() {
		
		return "user/list";
	}
	
//	회원 상세정보(admin)
	@GetMapping("/detail/admin/{id}")
	public String detail() {
		
		return "user/detail";
	}
	
//	회원 정보 수정(admin)
	@GetMapping("/profile/edit/admin/{id}/{field}")
	public String edit() {
		return "user/edit";
	}
	
	
//	회원 탈퇴처리 시
	@GetMapping("/withdrawal")
	public String withdrawal() {
		return "/user/withdrawalpage";
	}
	
	
}
