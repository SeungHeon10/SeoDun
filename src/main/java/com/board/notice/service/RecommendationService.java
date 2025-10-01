package com.board.notice.service;

import java.util.List;

import com.board.notice.dto.response.BoardResponseDTO;

public interface RecommendationService {
//	사용자 맞춤 게시글 조회
	public List<BoardResponseDTO> recommendByReadHistory(String userId);
//	비로그인/데이터 없을 때 추천 게시글 조회
	public List<BoardResponseDTO> recommendPublic();
}
