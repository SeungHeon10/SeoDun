package com.board.notice.service;

import java.util.List;

import com.board.notice.dto.request.ReplyRequestDTO;
import com.board.notice.dto.response.ReplyResponseDTO;

public interface ReplyService {
//	댓글 전체 조회
	List<ReplyResponseDTO> list(int bno);
//	댓글 등록
	void register(int boardId, ReplyRequestDTO replyRequestDTO);
//	댓글 수정
	void update(int boardId, int rno, ReplyRequestDTO replyRequestDTO);
//	댓글 삭제
	void delete(int boardId, int rno);
}
