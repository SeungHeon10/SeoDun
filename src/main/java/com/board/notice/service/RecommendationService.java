package com.board.notice.service;

import java.util.List;

import com.board.notice.dto.response.BoardResponseDTO;

public interface RecommendationService {
	public List<BoardResponseDTO> recommendByReadHistory(String userId);
}
