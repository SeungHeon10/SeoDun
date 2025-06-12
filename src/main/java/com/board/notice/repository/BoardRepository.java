package com.board.notice.repository;

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
}
