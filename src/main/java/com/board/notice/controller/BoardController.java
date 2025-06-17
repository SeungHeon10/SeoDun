package com.board.notice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/board")
public class BoardController {

//	게시글 전체조회 페이지 이동
	@GetMapping("/list")
	public String listPage() {
		
		return "board/list";
	}
	
//	게시글 상세보기 페이지 이동
	@GetMapping("/detail/{bno}")
	public String detailPage() {
		
		return "board/detail";
	}
	
//	게시글 등록 페이지 이동
	@GetMapping("/register")
	public String registerPage() {
		
		return "board/register";
	}
	
//	게시글 수정 페이지 이동
	@GetMapping("/update/{bno}")
	public String updatePage() {
		
		return "board/update";
	}
}
