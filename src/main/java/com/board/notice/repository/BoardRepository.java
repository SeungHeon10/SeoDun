package com.board.notice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.board.notice.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer>{
	// 제목 검색
	Page<Board> findByTitleContaining(String keyword, Pageable pageable);
	// 본문 검색
	Page<Board> findByContentContaining(String keyword, Pageable pageable);
	// 제목 + 본문 검색
	Page<Board> findByTitleContainingOrContentContaining(String keyword1, String keyword2, Pageable pageable);
	// 작성자 검색
	Page<Board> findByWriterContaining(String keyword, Pageable pageable);
	// 인기글 검색
	List<Board> findTop3ByOrderByViewCountDesc();
	// 카테고리별 게시글 조회
	List<Board> findTop6ByCategoryOrderByCreatedAtDesc(String category);
	// 전체 카테고리 6개 게시글 조회
	List<Board> findTop6ByOrderByCreatedAtDesc();
	// 최근 2개의 게시글 조회
	List<Board> findTop2ByOrderByCreatedAtDesc();
}
