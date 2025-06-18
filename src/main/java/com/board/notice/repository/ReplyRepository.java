package com.board.notice.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.board.notice.entity.Reply;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Integer>{
//	해당 게시글의 선택한 댓글 조회 
	Optional<Reply> findByRnoAndBoard_Bno(int rno, int bno);
//	선택 게시글 댓글 전체조회
	Page<Reply> findAllByBoard_Bno(int bno, Pageable pageable);
}
