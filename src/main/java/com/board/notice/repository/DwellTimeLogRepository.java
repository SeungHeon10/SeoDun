package com.board.notice.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.board.notice.entity.DwellTimeLog;

public interface DwellTimeLogRepository extends JpaRepository<DwellTimeLog, Long> {
	@Query("SELECT AVG(d.dwellTime) FROM DwellTimeLog d WHERE d.userId.id = :userId AND d.boardId.id = :boardId")
	Double findAverageDwellTime(@Param("userId") String userId, @Param("boardId") int boardId);

	@Query("SELECT d.boardId.id FROM DwellTimeLog d WHERE d.userId.id = :userId ORDER BY d.dwellTime DESC")
	List<Integer> findTopBoardIdsByUser(@Param("userId") String userId, Pageable pageable);

	@Query("SELECT DISTINCT d.boardId.id FROM DwellTimeLog d WHERE d.userId.id = :userId")
	List<Integer> findAllBoardIdsByUser(@Param("userId") String userId);
}
