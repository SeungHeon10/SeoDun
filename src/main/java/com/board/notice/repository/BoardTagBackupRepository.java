package com.board.notice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.board.notice.entity.BoardTagBackup;

public interface BoardTagBackupRepository extends JpaRepository<BoardTagBackup, Integer> {
	// 특정 게시글의 백업 태그 전체 조회
	List<BoardTagBackup> findByBoardBno(Integer boardBno);

	// 복구 후 삭제
	void deleteByBoardBno(Integer boardBno);
}
