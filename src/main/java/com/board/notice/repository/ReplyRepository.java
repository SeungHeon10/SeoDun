package com.board.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.board.notice.entity.Reply;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Integer>{

}
