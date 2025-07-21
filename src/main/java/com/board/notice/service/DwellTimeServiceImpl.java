package com.board.notice.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.board.notice.dto.request.DwellTimeRequestDTO;
import com.board.notice.entity.Board;
import com.board.notice.entity.DwellTimeLog;
import com.board.notice.entity.User;
import com.board.notice.repository.BoardRepository;
import com.board.notice.repository.DwellTimeLogRepository;
import com.board.notice.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DwellTimeServiceImpl implements DwellTimeService {
	private final DwellTimeLogRepository dwellTimeLogRepository;
	private final UserRepository userRepository;
	private final BoardRepository boardRepository;

	@Override
	public void saveDwellTime(DwellTimeRequestDTO dto) {
		User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
		Board board = boardRepository.findById(dto.getBoardId())
				.orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다"));

		DwellTimeLog log = DwellTimeLog.builder().userId(user).boardId(board).dwellTime(dto.getDwellTime())
				.interactionCount(dto.getInteractionCount())
				.startTime(LocalDateTime.now().minusSeconds(dto.getDwellTime().longValue()))
				.endTime(LocalDateTime.now()).build();
		
		dwellTimeLogRepository.save(log);
	}

	@Override
	public Double getAverageDwellTime(String userId, int boardId) {
		return dwellTimeLogRepository.findAverageDwellTime(userId, boardId);
	}
}
