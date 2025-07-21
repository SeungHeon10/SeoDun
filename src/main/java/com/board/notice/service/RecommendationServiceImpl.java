package com.board.notice.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.board.notice.dto.response.BoardResponseDTO;
import com.board.notice.entity.Board;
import com.board.notice.repository.BoardRepository;
import com.board.notice.repository.DwellTimeLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
	private final DwellTimeLogRepository dwellTimeLogRepository;
	private final BoardRepository boardRepository;

	@Override
	public List<BoardResponseDTO> recommendByReadHistory(String userId) {
		// 1. 오래 본 게시글 5개
		List<Integer> recentBoardIds = dwellTimeLogRepository.findTopBoardIdsByUser(userId, PageRequest.of(0, 5));
		if (recentBoardIds.isEmpty())
			return List.of();

		// 2. 전체 본 게시글 (제외용)
		List<Integer> seenBoardIds = dwellTimeLogRepository.findAllBoardIdsByUser(userId);

		// 3. 태그 + 카테고리
		List<String> tags = boardRepository.findTagsByBoardIds(recentBoardIds);
		List<String> topCategories = boardRepository.findMostCommonCategoryByBoardIds(recentBoardIds);
		String mostCommonCategory = topCategories.isEmpty() ? null : topCategories.get(0);

		// 4. 태그/카테고리 겹치는 후보 게시글 조회 (이미 본 게시글 제외)
		List<Board> candidates = boardRepository.findSimilarBoards(tags, mostCommonCategory, seenBoardIds,
				PageRequest.of(0, 50));

		// 5. 태그 유사도 점수 계산
		Map<Board, Long> scored = candidates.stream().collect(Collectors.toMap(board -> board,
				board -> board.getTags().stream().filter(tag -> tags.contains(tag)).count()));

		// 6. 점수 기준으로 정렬
		return scored.entrySet().stream().sorted(Map.Entry.<Board, Long>comparingByValue().reversed()).limit(2)
				.map(entry -> new BoardResponseDTO(entry.getKey())).toList();
	}

}
