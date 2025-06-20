package com.board.notice.service;

import org.springframework.data.domain.Pageable;

import com.board.notice.dto.request.ReplyRequestDTO;
import com.board.notice.dto.response.ReplyPageResponseDTO;

public interface ReplyService {
//	댓글 전체 조회
	ReplyPageResponseDTO list(int bno, Pageable pageable);
//	댓글 등록
	void register(int boardId, ReplyRequestDTO replyRequestDTO);
//	댓글 수정
	void update(int boardId, int rno, ReplyRequestDTO replyRequestDTO);
//	댓글 삭제
	void delete(int boardId, int rno);
}
