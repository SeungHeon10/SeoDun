package com.board.notice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.board.notice.dto.response.BoardResponseDTO;
import com.board.notice.security.CustomUserDetail;
import com.board.notice.service.RecommendationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
public class RecommendationRestController {

	private final RecommendationService recommendationService;

	@GetMapping("/read-based")
	public ResponseEntity<?> recommendByReadHistory(@AuthenticationPrincipal CustomUserDetail userDetails) {
		String userId = userDetails.getUsername();
		List<BoardResponseDTO> result = recommendationService.recommendByReadHistory(userId);
		return ResponseEntity.ok(result);
	}
}
