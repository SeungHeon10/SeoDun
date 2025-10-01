package com.board.notice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.board.notice.entity.ReplyLike;

@Repository
public interface ReplyLikeRepository extends JpaRepository<ReplyLike, Long> {
	Optional<ReplyLike> findByReply_RnoAndUser_Id(int replyId, String userId);

	long countByReply_Rno(int replyId);
}
