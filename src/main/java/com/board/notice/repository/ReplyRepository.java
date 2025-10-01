package com.board.notice.repository;

import java.util.List;
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
// 	부모 댓글만 페이징
	Page<Reply> findByBoard_BnoAndParentIsNull(int bno, Pageable pageable);
// 	자식 댓글 전체 조회
	List<Reply> findByBoard_BnoAndParentIsNotNull(int bno);
//	사용자의 댓글 수
	long countByUserId_Id(String userId);
}
