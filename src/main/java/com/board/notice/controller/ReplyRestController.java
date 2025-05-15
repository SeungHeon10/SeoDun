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
@RequestMapping("api/boards/{boardId}/replies")
public class ReplyRestController {
	private final ReplyService replyService;

//	댓글 조회
	@GetMapping
	public ResponseEntity<List<ReplyResponseDTO>> list(@PathVariable("boardId") int boardId) {
		List<ReplyResponseDTO> list = replyService.list(boardId);
		return ResponseEntity.ok(list);
	}

//	댓글 등록
	@PostMapping
	public ResponseEntity<String> register(@PathVariable("boardId") int boardId,
			@RequestBody ReplyRequestDTO replyRequestDTO) {
		replyService.register(boardId, replyRequestDTO);
		return ResponseEntity.ok("댓글이 등록되었습니다.");
	}

//	댓글 수정
	@PutMapping("/{rno}")
	public ResponseEntity<String> update(@PathVariable("boardId") int boardId, @PathVariable("rno") int rno,
			@RequestBody ReplyRequestDTO replyRequestDTO) {
		replyService.update(boardId, rno, replyRequestDTO);
		return ResponseEntity.ok("댓글이 수정되었습니다.");
	}

//	댓글 삭제
	@DeleteMapping("/{rno}")
	public ResponseEntity<String> delete(@PathVariable("boardId") int boardId, @PathVariable("rno") int rno) {
		replyService.delete(boardId, rno);
		return ResponseEntity.ok("해당 댓글이 삭제되었습니다.");
	}
}
