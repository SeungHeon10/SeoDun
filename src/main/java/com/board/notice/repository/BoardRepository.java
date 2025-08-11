package com.board.notice.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
	// admin이 붙은 쿼리문은 소프트 삭제된 항목도 모두 조회

	// 전체 카테고리에서 제목 검색
	Page<Board> findByTitleContaining(String keyword, Pageable pageable);

	// 해당 카테고리에서 제목 검색
	Page<Board> findByTitleContainingAndCategory(String keyword, String category, Pageable pageable);

	// 전체 카테고리에서 본문 검색
	Page<Board> findByContentContaining(String keyword, Pageable pageable);

	// 해당 카테고리에서 본문 검색
	Page<Board> findByContentContainingAndCategory(String keyword, String category, Pageable pageable);

	// 전체 카테고리에서 제목 or 본문 검색
	@Query("SELECT b FROM Board b WHERE (b.title LIKE %:keyword% OR b.content LIKE %:keyword%)")
	Page<Board> searchByTitleOrContent(@Param("keyword") String keyword, Pageable pageable);

	// 해당 카테고리에서 제목 or 본문 검색
	@Query("SELECT b FROM Board b WHERE (b.title LIKE %:keyword% OR b.content LIKE %:keyword%) AND b.category = :category")
	Page<Board> searchByTitleOrContentAndCategory(@Param("keyword") String keyword, @Param("category") String category,
			Pageable pageable);

	// 전체 카테고리에서 작성자 검색
	Page<Board> findByUserId_NicknameContainingIgnoreCase(String keyword, Pageable pageable);

	// 해당 카테고리에서 작성자 검색
	Page<Board> findByUserId_NicknameContainingIgnoreCaseAndCategory(String keyword, String category,
			Pageable pageable);

	// 전체 게시글 조회(admin)
	@Query(value = "SELECT * FROM board", countQuery = "SELECT COUNT(*) FROM board", nativeQuery = true)
	Page<Board> findAllBoardsNative(Pageable pageable);

	// 전체 게시글에서 제목 검색(admin)
	@Query(value = "SELECT * FROM board WHERE title LIKE CONCAT('%', :keyword, '%')", countQuery = "SELECT COUNT(*) FROM board WHERE title LIKE CONCAT('%', :keyword, '%')", nativeQuery = true)
	Page<Board> findByTitleContainingNative(@Param("keyword") String keyword, Pageable pageable);

	// 전체 게시글에서 본문 검색(admin)
	@Query(value = "SELECT * FROM board WHERE content LIKE CONCAT('%', :keyword, '%')", countQuery = "SELECT COUNT(*) FROM board WHERE content LIKE CONCAT('%', :keyword, '%')", nativeQuery = true)
	Page<Board> findByContentContainingNative(@Param("keyword") String keyword, Pageable pageable);

	// 전체 게시글에서 제목 or 본문 검색(admin)
	@Query(value = "SELECT * FROM board WHERE title LIKE CONCAT('%', :keyword, '%') OR content LIKE CONCAT('%', :keyword, '%')", countQuery = "SELECT COUNT(*) FROM board WHERE title LIKE CONCAT('%', :keyword, '%') OR content LIKE CONCAT('%', :keyword, '%')", nativeQuery = true)
	Page<Board> searchByTitleOrContentNative(@Param("keyword") String keyword, Pageable pageable);

	// 전체 게시글에서 작성자 검색(admin)
	@Query(value = """
			SELECT b.*
			FROM board b
			JOIN `user` u ON u.id = b.user_id
			WHERE u.nickname LIKE CONCAT('%', :keyword, '%')
			""", countQuery = """
			SELECT COUNT(*)
			FROM board b
			JOIN `user` u ON u.id = b.user_id
			WHERE u.nickname LIKE CONCAT('%', :keyword, '%')
			""", nativeQuery = true)
	Page<Board> findByUserNicknameContainingNative(@Param("keyword") String keyword, Pageable pageable);

	// 해당 게시글 상세보기(admin)
	@Query(value = "SELECT * FROM board WHERE bno = :bno", nativeQuery = true)
	Optional<Board> findByIdNative(@Param("bno") int bno);

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

	// 게시글들에 포함되어 있는 태그 가져오기
	@Query(value = "SELECT DISTINCT tag FROM board_tags WHERE board_bno IN (:boardIds)", nativeQuery = true)
	List<String> findTagsByBoardIds(@Param("boardIds") List<Integer> boardIds);

	// 카테고리별 개수를 세어 가장 많이 등장한 카테고리부터 정렬
	@Query("SELECT b.category FROM Board b WHERE b.id IN :boardIds GROUP BY b.category ORDER BY COUNT(b.id) DESC")
	List<String> findMostCommonCategoryByBoardIds(@Param("boardIds") List<Integer> boardIds);

	// 제외할 게시글 ID 목록에 포함되지 않는 게시글중 주어진 태그 목록 또는 카테고리 중 하나라도 일치하는 게시글을 조회
	@Query("SELECT DISTINCT b FROM Board b JOIN b.tags t WHERE (t IN :tags OR b.category = :category) AND b.id NOT IN :excludedIds ORDER BY b.createdAt DESC")
	List<Board> findSimilarBoards(@Param("tags") List<String> tags, @Param("category") String category,
			@Param("excludedIds") List<Integer> excludedIds, Pageable pageable);

	// 카테고리 기반 게시글 조회 (제외할 게시글 ID 포함)
	@Query("SELECT b FROM Board b WHERE b.category = :category AND b.id NOT IN :excludedIds ORDER BY b.createdAt DESC")
	List<Board> findBoardsByCategoryExcluding(@Param("category") String category,
			@Param("excludedIds") List<Integer> excludedIds, Pageable pageable);

	// 최근 7일 내 인기글 상위 2개
	List<Board> findTop2ByCreatedAtAfterOrderByViewCountDesc(LocalDateTime since);

	// 게시글 랜덤으로 조회하기
	@Query("SELECT b FROM Board b ORDER BY FUNCTION('RAND')")
	List<Board> findRandom(Pageable pageable);
}
