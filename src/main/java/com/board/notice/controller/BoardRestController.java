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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
	public ResponseEntity<Page<BoardResponseDTO>> list(
			@PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
			,@RequestParam(name = "mode", defaultValue = "title") String mode
			,@RequestParam(name = "keyword", defaultValue = "") String keyword) {
		Page<BoardResponseDTO> list = boardService.list(pageable, mode, keyword);
		
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
			@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
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
	
//	인기글 조회
	@GetMapping("/popular")
	public ResponseEntity<List<BoardResponseDTO>> popularPosts() {
		List<BoardResponseDTO> popular = boardService.popularBoards();
		return ResponseEntity.ok(popular);
	}
	
//	카테고리별 조회
	@GetMapping("/category/{category}")
	public ResponseEntity<List<BoardResponseDTO>> loadBoardsByCategory(@PathVariable("category") String category) {
		if(category.equals("전체")) {
			List<BoardResponseDTO> boards = boardService.loadBoardsByAll();
			return ResponseEntity.ok(boards);
		} else {
			List<BoardResponseDTO> boards = boardService.loadBoardsByCategory(category);
			return ResponseEntity.ok(boards);
		}
	}
	
//	최신 게시글 조회
	@GetMapping("/recent")
	public ResponseEntity<List<BoardResponseDTO>> recentBoards() {
		List<BoardResponseDTO> recent = boardService.recentBoards();
		
		return ResponseEntity.ok(recent);
	}
	
}
