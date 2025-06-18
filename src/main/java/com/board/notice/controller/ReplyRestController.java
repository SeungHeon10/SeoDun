package com.board.notice.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.board.notice.dto.request.ReplyRequestDTO;
import com.board.notice.dto.response.ReplyResponseDTO;
import com.board.notice.service.ReplyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/boards/{bno}/replies")
public class ReplyRestController {
	private final ReplyService replyService;

//	댓글 조회
	@GetMapping
	public ResponseEntity<Page<ReplyResponseDTO>> list(@PageableDefault(page = 0, size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
			,@PathVariable("bno") int bno) {
		Page<ReplyResponseDTO> list = replyService.list(bno, pageable);
		return ResponseEntity.ok(list);
	}

//	댓글 등록
	@PostMapping
	public ResponseEntity<String> register(@PathVariable("bno") int bno,
			@RequestBody ReplyRequestDTO replyRequestDTO) {
		
		replyService.register(bno, replyRequestDTO);
		return ResponseEntity.ok("댓글이 등록되었습니다!");
	}

//	댓글 수정
	@PutMapping("/{rno}")
	public ResponseEntity<String> update(@PathVariable("bno") int bno, @PathVariable("rno") int rno,
			@RequestBody ReplyRequestDTO replyRequestDTO) {
		replyService.update(bno, rno, replyRequestDTO);
		return ResponseEntity.ok("댓글이 수정되었습니다.");
	}

//	댓글 삭제
	@DeleteMapping("/{rno}")
	public ResponseEntity<String> delete(@PathVariable("bno") int bno, @PathVariable("rno") int rno) {
		replyService.delete(bno, rno);
		return ResponseEntity.ok("해당 댓글이 삭제되었습니다.");
	}
}
