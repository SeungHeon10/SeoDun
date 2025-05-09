package com.board.notice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.board.notice.dto.request.ReplyRequestDTO;
import com.board.notice.dto.response.ReplyResponseDTO;
import com.board.notice.service.ReplyService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reply")
public class ReplyRestController {
	private final ReplyService replyService;
	
//	댓글 전체조회
	@GetMapping("/list")
	public ResponseEntity<List<ReplyResponseDTO>> list() {
		List<ReplyResponseDTO> list = replyService.list();
		
		return ResponseEntity.ok(list);
	}
	
//	댓글 등록
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody ReplyRequestDTO replyRequestDTO) {
		replyService.register(replyRequestDTO);
		
		return ResponseEntity.ok("댓글이 등록되었습니다!");
	}
	
//	댓글 수정
	@PutMapping("/update")
	public ResponseEntity<String> update(@RequestBody ReplyRequestDTO replyRequestDTO) {
		replyService.update(replyRequestDTO);
		
		return ResponseEntity.ok("댓글이 수정되었습니다!");
	}
	
//	댓글 삭제
	@DeleteMapping("/delete/{rno}")
	public ResponseEntity<String> delete(@PathVariable("rno") int rno) {
		replyService.delete(rno);
		
		return ResponseEntity.ok("댓글이 삭제되었습니다!");
	}
}
