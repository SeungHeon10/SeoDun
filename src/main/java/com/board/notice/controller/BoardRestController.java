package com.board.notice.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.board.notice.dto.request.BoardRequestDTO;
import com.board.notice.dto.response.BoardResponseDTO;
import com.board.notice.dto.response.TagCountResponseDTO;
import com.board.notice.security.CustomUserDetail;
import com.board.notice.service.BoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardRestController {
	private final BoardService boardService;

//	게시글 전체 조회
	@GetMapping
	public ResponseEntity<Page<BoardResponseDTO>> list(
			@PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
			@RequestParam(name = "mode", defaultValue = "title") String mode,
			@RequestParam(name = "keyword", defaultValue = "") String keyword,
			@RequestParam(name = "category", defaultValue = "all") String category) {
		String realCategory = switch (category) {
		case "free" -> "자유";
		case "study" -> "학습";
		case "share" -> "정보공유";
		case "qna" -> "질문답변";
		default -> "전체";
		};

		Page<BoardResponseDTO> list = boardService.list(pageable, mode, keyword, realCategory);

		return ResponseEntity.ok(list);
	}

//	게시글 전체 조회(admin)
	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Page<BoardResponseDTO>> listForAdmin(
			@RequestParam(name = "mode", defaultValue = "title") String mode,
			@RequestParam(name = "keyword", defaultValue = "") String keyword,
			@RequestParam(name = "sort", defaultValue = "createdAt") String sort,
			@RequestParam(name = "direction", defaultValue = "desc") String direction,
			@PageableDefault(page = 0, size = 10) Pageable pageableBase) {
		String sortColumn;
		switch (sort) {
		case "commentCount":
			sortColumn = "comment_count";
			break;
		case "viewCount":
			sortColumn = "view_count";
			break;
		case "createdAt":
		default:
			sortColumn = "created_at";
		}

		Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

		Pageable pageable = PageRequest.of(pageableBase.getPageNumber(), pageableBase.getPageSize(),
				Sort.by(sortDirection, sortColumn));
		Page<BoardResponseDTO> list = boardService.listForAdmin(pageable, mode, keyword);

		return ResponseEntity.ok(list);
	}

//	게시글 상세보기
	@GetMapping("/{bno}")
	public ResponseEntity<BoardResponseDTO> detail(@PathVariable("bno") int bno) {
		BoardResponseDTO boardResponseDTO = boardService.detail(bno);
		
		return ResponseEntity.ok(boardResponseDTO);
	}
	
//	게시글 상세보기(admin)
	@GetMapping("/admin/{bno}")
	public ResponseEntity<BoardResponseDTO> detailForAdmin(@PathVariable("bno") int bno) {
		BoardResponseDTO boardResponseDTO = boardService.detailForAdmin(bno);

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
	@PostMapping("/{bno}/edit")
	public ResponseEntity<?> update(@PathVariable("bno") int bno, @ModelAttribute BoardRequestDTO boardRequestDTO,
			@RequestParam(value = "file", required = false) MultipartFile file,
			@AuthenticationPrincipal CustomUserDetail userDetails,
			@RequestParam(value = "deleteFile", required = false, defaultValue = "false") boolean deleteFile)
			throws IOException {

		return boardService.update(boardRequestDTO, file, deleteFile, userDetails);
	}

//	게시글 삭제하기
	@DeleteMapping("/{bno}")
	public ResponseEntity<?> delete(@PathVariable("bno") int bno,
			@AuthenticationPrincipal CustomUserDetail userDetails) {

		return boardService.delete(bno, userDetails);
	}
	
//	게시글 복원하기(admin)
	@PatchMapping("/admin/{bno}")
	public ResponseEntity<?> restore(@PathVariable("bno") int bno) {
		
		return boardService.restore(bno);
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
		if (category.equals("전체")) {
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

//	이미지 업로드
	@PostMapping("/upload/image")
	public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile image) throws IOException {

		return ResponseEntity.ok(boardService.uploadImage(image));
	}

//	태그 많이 사용된 5개 조회
	@GetMapping("/tags/popular")
	public ResponseEntity<List<TagCountResponseDTO>> getTopTags() {
		List<TagCountResponseDTO> topTags = boardService.getTop6Tags();

		return ResponseEntity.ok(topTags);
	}
}
