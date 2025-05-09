package com.board.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.board.notice.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer>{

}
