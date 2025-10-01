package com.board.notice.service;

import com.board.notice.dto.request.DwellTimeRequestDTO;

public interface DwellTimeService {
	public void saveDwellTime(DwellTimeRequestDTO dto);

	public Double getAverageDwellTime(String userId, int boardId);
}
