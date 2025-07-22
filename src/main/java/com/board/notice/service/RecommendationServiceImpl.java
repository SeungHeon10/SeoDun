package com.board.notice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
		// 비로그인 사용자일 경우
		if (userId == null || userId.isBlank()) {
			return getFallbackRecommendations();
		}

		// 오래 본 게시글 5개
		List<Integer> recentBoardIds = dwellTimeLogRepository.findTopBoardIdsByUser(userId, PageRequest.of(0, 5));

		if (recentBoardIds.isEmpty()) {
			return getFallbackRecommendations();
		}

		// 전체 본 게시글 (제외용)
		List<Integer> seenBoardIds = dwellTimeLogRepository.findAllBoardIdsByUser(userId);

		// 태그 + 카테고리
		List<String> tags = boardRepository.findTagsByBoardIds(recentBoardIds);
		List<String> topCategories = boardRepository.findMostCommonCategoryByBoardIds(recentBoardIds);
		String mainCategory = topCategories.isEmpty() ? null : topCategories.get(0);

		Pageable pageable = PageRequest.of(0, 20);
		List<Board> candidates;

		// 태그가 있는 경우 → 태그+카테고리 기반 후보 추출 후 유사도 점수화
		if (!tags.isEmpty()) {
			candidates = boardRepository.findSimilarBoards(tags, mainCategory, seenBoardIds, pageable);

			// 태그 유사도 기반 점수 계산
			Map<Board, Long> scored = candidates.stream().collect(
					Collectors.toMap(board -> board, board -> board.getTags().stream().filter(tags::contains).count()));

			return scored.entrySet().stream().sorted(Map.Entry.<Board, Long>comparingByValue().reversed()).limit(3)
					.map(entry -> new BoardResponseDTO(entry.getKey())).toList();
		}

		// 태그는 없지만 카테고리는 있는 경우 → 카테고리 기반 추천
		if (mainCategory != null) {
			candidates = boardRepository.findBoardsByCategoryExcluding(mainCategory, seenBoardIds, pageable);
			return candidates.stream().map(BoardResponseDTO::new).limit(3).toList();
		}

		// 둘 다 없는 경우
		return getFallbackRecommendations();
	}

	private List<BoardResponseDTO> getFallbackRecommendations() {
		// 7일 전 시간 계산
		LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);

		// 1. 최근 1주일 내 이슈성 글 Top 2
		List<Board> hotBoards = boardRepository.findHotBoardsInLastWeek(lastWeek, PageRequest.of(0, 2));

		return hotBoards.stream().map(BoardResponseDTO::new).toList();
	}

}
