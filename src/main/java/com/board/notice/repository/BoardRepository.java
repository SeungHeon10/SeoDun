package com.board.notice.repository;

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
}
