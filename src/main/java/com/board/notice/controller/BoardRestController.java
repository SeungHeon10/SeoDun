package com.board.notice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.board.notice.dto.request.BoardRequestDTO;
import com.board.notice.dto.response.BoardResponseDTO;
import com.board.notice.service.BoardService;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardRestController {
	private final BoardService boardService;

//	게시글 전체 조회
	@GetMapping
	public ResponseEntity<List<BoardResponseDTO>> list() {
		List<BoardResponseDTO> list = boardService.list();
		return ResponseEntity.ok(list);
	}

//	게시글 상세보기
	@GetMapping("/{bno}")
	public ResponseEntity<BoardResponseDTO> detail(@PathVariable("bno") int bno) {
		BoardResponseDTO boardResponseDTO = boardService.detail(bno);
		return ResponseEntity.ok(boardResponseDTO);
	}

//	게시글 등록하기
	@PostMapping
	public ResponseEntity<String> register(@ModelAttribute BoardRequestDTO boardRequestDTO,
			@RequestParam(value = "file" , required = false) MultipartFile file) throws IOException {
		boardService.register(boardRequestDTO, file);
		return ResponseEntity.ok("게시글이 등록되었습니다.");
	}

//	게시글 수정하기
	@PutMapping("/{bno}")
	public ResponseEntity<String> update(@PathVariable("bno") int bno, @ModelAttribute BoardRequestDTO boardRequestDTO,
			@RequestParam("file") MultipartFile file) throws IOException {
		boardService.update(boardRequestDTO, file);
		return ResponseEntity.ok("게시글이 수정되었습니다.");
	}

//	게시글 삭제하기
	@DeleteMapping("/{bno}")
	public ResponseEntity<String> delete(@PathVariable("bno") int bno) {
		boardService.delete(bno);
		return ResponseEntity.ok("게시글이 삭제되었습니다.");
	}

}
