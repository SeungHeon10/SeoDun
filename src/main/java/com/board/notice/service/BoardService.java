package com.board.notice.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.board.notice.dto.request.BoardRequestDTO;
import com.board.notice.dto.response.BoardResponseDTO;

public interface BoardService {
//	게시글 전체 조회
	Page<BoardResponseDTO> list(Pageable pageable, String mode, String keyword);
//	게시글 상세보기
	BoardResponseDTO detail(int bno);
//	게시글 등록
	void register(BoardRequestDTO boardRequestDTO, MultipartFile multipartFile) throws IOException;
//	게시글 수정
	void update(BoardRequestDTO boardRequestDTO, MultipartFile multipartFile) throws IOException;
//	게시글 삭제
	void delete(int bno);
//	인기글 조회
	List<BoardResponseDTO> popularBoards();
//	카테고리별 게시글 조회
	List<BoardResponseDTO> loadBoardsByCategory(String category);
//	전체 카테고리 6개 게시글 조회
	List<BoardResponseDTO> loadBoardsByAll();
//	최근 2개의 게시글 조회
	List<BoardResponseDTO> recentBoards();
}
