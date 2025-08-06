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
	
}
