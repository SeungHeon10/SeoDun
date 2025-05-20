package com.board.notice.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.board.notice.dto.request.BoardRequestDTO;
import com.board.notice.dto.response.BoardResponseDTO;
import com.board.notice.entity.Board;

public interface BoardService {
//	게시글 전체 조회
	List<BoardResponseDTO> list();
//	게시글 캐싱
	Board getBoardContentOnly(int bno);
//	게시글 상세보기
	BoardResponseDTO detail(int bno);
//	게시글 등록
	void register(BoardRequestDTO boardRequestDTO, MultipartFile multipartFile) throws IOException;
//	게시글 수정
	void update(BoardRequestDTO boardRequestDTO, MultipartFile multipartFile) throws IOException;
//	게시글 삭제
	void delete(int bno);
}
