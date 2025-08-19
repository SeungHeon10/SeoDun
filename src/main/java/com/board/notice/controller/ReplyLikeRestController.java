package com.board.notice.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.board.notice.dto.response.ReplyLikeResponseDTO;
import com.board.notice.security.CustomUserDetail;
import com.board.notice.service.ReplyLikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/replies")
public class ReplyLikeRestController {
	private final ReplyLikeService replyLikeService;

	// 토글 (좋아요 → 취소, 취소 → 좋아요)
	@PostMapping("/{replyId}/like")
	public ResponseEntity<?> toggleLike(@PathVariable("replyId") int replyId,
			@AuthenticationPrincipal CustomUserDetail principal) {
		if (principal == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
		}
		ReplyLikeResponseDTO result = replyLikeService.toggleReplyLikeCount(replyId, principal.getUsername());
		return ResponseEntity.ok(result);
	}

	// (선택) 초기 렌더링 시 내가 좋아요 했는지 조회
	@GetMapping("/{replyId}/like/me")
	public ResponseEntity<?> myLike(@PathVariable("replyId") int replyId, @AuthenticationPrincipal CustomUserDetail principal) {
		boolean liked = false;
		int count = 0;
		
		return ResponseEntity.ok(Map.of("liked", liked, "likeCount", count));
	}
}
