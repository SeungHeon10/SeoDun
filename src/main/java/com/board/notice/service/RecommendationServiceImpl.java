package com.board.notice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

//	비로그인/데이터 없음일 때 추천 게시글 조회
	@Override
	public List<BoardResponseDTO> recommendPublic() {
		List<BoardResponseDTO> result = new ArrayList<>(2);
		Set<Integer> used = new HashSet<>();

		// 최근 7일 게시글 중 인기글 조회
		LocalDateTime since = LocalDateTime.now().minusDays(7);
		List<Board> popularCandidates = boardRepository.findTop2ByCreatedAtAfterOrderByViewCountDesc(since);

		// 최신글 조회
		List<Board> latestCandidates = boardRepository.findTop2ByOrderByCreatedAtDesc();

		// 인기글 1개 픽
		Board popularPick = firstDistinct(popularCandidates, used);
		if (popularPick != null) {
			result.add(new BoardResponseDTO(popularPick));
			used.add(popularPick.getBno());
		}

		// 최신글 1개 픽 (인기와 중복x)
		Board latestPick = firstDistinct(latestCandidates, used);
		if (latestPick != null) {
			result.add(new BoardResponseDTO(latestPick));
			used.add(latestPick.getBno());
		}

		// 데이터가 적어서 2개가 안 채워졌을 시 랜덤 게시글로 대체
		if (result.size() < 2) {
			int need = 2 - result.size();
			int pageSize = Math.max(need * 2, 10);

			List<Board> randoms = boardRepository.findRandom(PageRequest.of(0, pageSize));
			Board randomPick = firstDistinct(randoms, used);
			if (randomPick != null) {
				result.add(new BoardResponseDTO(randomPick));
				used.add(randomPick.getBno());
			}
		}
		
		// 그럼에도 데이터가 부족하다면 2개의 게시글 모두 랜덤으로 가져와 반환
		if(result.size() < 2) {
			List<Board> randoms = boardRepository.findRandom(PageRequest.of(0, 2));
			
			return randoms.stream().map(BoardResponseDTO::new).toList();
		}

		return result;
	}
	
	// List에서 아직 선택되지 않은 첫 번째 게시글을 반환
	private Board firstDistinct(List<Board> candidates, Set<Integer> used) {
		if (candidates == null)
			return null;
		for (Board b : candidates) {
			if (b != null && b.getBno() != 0 && !used.contains(b.getBno())) {
				return b;
			}
		}
		return null;
	}

	// 맞춤 데이터가 없을 때 대체 추천 목록 생성
	private List<BoardResponseDTO> getFallbackRecommendations() {
		// 7일 전 시간 계산
		LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);

		// 최근 1주일 내 이슈성 글 Top 2
		List<Board> hotBoards = boardRepository.findTop2ByCreatedAtAfterOrderByViewCountDesc(lastWeek);

		if (!(hotBoards.isEmpty())) {
			return hotBoards.stream().map(BoardResponseDTO::new).toList();
		} else {
			// 최근 1주일 글이 없을 경우 인기글 Top 2
			List<Board> popular = boardRepository.findTop3ByOrderByViewCountDesc();
			return popular.stream().map(BoardResponseDTO::new).limit(2).toList();
		}
	}

}
