package com.board.notice.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.board.notice.dto.response.TagCountResponseDTO;
import com.board.notice.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
	// 전체 카테고리에서 제목 검색
	Page<Board> findByTitleContaining(String keyword, Pageable pageable);

	// 해당 카테고리에서 제목 검색
	Page<Board> findByTitleContainingAndCategory(String keyword, String category, Pageable pageable);

	// 전체 카테고리에서 본문 검색
	Page<Board> findByContentContaining(String keyword, Pageable pageable);

	// 해당 카테고리에서 본문 검색
	Page<Board> findByContentContainingAndCategory(String keyword, String category, Pageable pageable);

	// 전체 카테고리에서 제목 + 본문 검색
	@Query("SELECT b FROM Board b WHERE (b.title LIKE %:keyword% OR b.content LIKE %:keyword%)")
	Page<Board> searchByTitleOrContent(@Param("keyword") String keyword, Pageable pageable);

	// 해당 카테고리에서 제목 + 본문 검색
	@Query("SELECT b FROM Board b WHERE (b.title LIKE %:keyword% OR b.content LIKE %:keyword%) AND b.category = :category")
	Page<Board> searchByTitleOrContentAndCategory(@Param("keyword") String keyword, @Param("category") String category,
			Pageable pageable);

	// 전체 카테고리에서 작성자 검색
	Page<Board> findByWriterContaining(String keyword, Pageable pageable);

	// 해당 카테고리에서 작성자 검색
	Page<Board> findByWriterContainingAndCategory(String keyword, String category, Pageable pageable);

	// 인기글 검색
	List<Board> findTop3ByOrderByViewCountDesc();

	// 카테고리별 6개 게시글 조회
	List<Board> findTop6ByCategoryOrderByCreatedAtDesc(String category);

	// 전체 카테고리 6개 게시글 조회
	List<Board> findTop6ByOrderByCreatedAtDesc();

	// 최근 2개의 게시글 조회
	List<Board> findTop2ByOrderByCreatedAtDesc();

	// 카테고리별 전체 게시글 조회
	Page<Board> findAllByCategory(String category, Pageable pageable);

	// 사용자의 게시글 수
	long countByUserId_Id(String userId);

	// 인기 태그 조회
	@Query("SELECT new com.board.notice.dto.response.TagCountResponseDTO(t, COUNT(t)) "
			+ "FROM Board b JOIN b.tags t GROUP BY t ORDER BY COUNT(t) DESC")
	List<TagCountResponseDTO> findTopTags(Pageable pageable);

	@Query(value = "SELECT DISTINCT tag FROM board_tags WHERE board_bno IN (:boardIds)", nativeQuery = true)
	List<String> findTagsByBoardIds(@Param("boardIds") List<Integer> boardIds);

	@Query("SELECT b.category FROM Board b WHERE b.id IN :boardIds GROUP BY b.category ORDER BY COUNT(b.id) DESC")
	List<String> findMostCommonCategoryByBoardIds(@Param("boardIds") List<Integer> boardIds);

	@Query("SELECT DISTINCT b FROM Board b JOIN b.tags t WHERE (t IN :tags OR b.category = :category) AND b.id NOT IN :excludedIds ORDER BY b.createdAt DESC")
	List<Board> findSimilarBoards(@Param("tags") List<String> tags, @Param("category") String category,
			@Param("excludedIds") List<Integer> excludedIds, Pageable pageable);

	// 카테고리 기반 게시글 조회 (제외할 게시글 ID 포함)
	@Query("SELECT b FROM Board b WHERE b.category = :category AND b.id NOT IN :excludedIds ORDER BY b.createdAt DESC")
	List<Board> findBoardsByCategoryExcluding(@Param("category") String category,
			@Param("excludedIds") List<Integer> excludedIds, Pageable pageable);
	
	// 최근 1주일 내 이슈성 글 Top 2
	@Query("""
			    SELECT b FROM Board b
			    WHERE b.createdAt >= :startDate
			    ORDER BY (b.viewCount * 2 + b.commentCount * 3) DESC
			""")
	List<Board> findHotBoardsInLastWeek(@Param("startDate") LocalDateTime startDate, Pageable pageable);
}
