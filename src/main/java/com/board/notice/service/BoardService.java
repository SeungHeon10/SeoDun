package com.board.notice.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.board.notice.dto.request.BoardRequestDTO;
import com.board.notice.dto.response.BoardResponseDTO;
import com.board.notice.dto.response.TagCountResponseDTO;
import com.board.notice.security.CustomUserDetail;

public interface BoardService {
//	게시글 전체 조회
	Page<BoardResponseDTO> list(Pageable pageable, String mode, String keyword, String category);

//	게시글 전체 조회(admin)
	Page<BoardResponseDTO> listForAdmin(Pageable pageable, String mode, String keyword);
	
//	게시글 상세보기
	BoardResponseDTO detail(int bno);
	
//	게시글 상세보기(admin)
	BoardResponseDTO detailForAdmin(int bno);

//	게시글 등록
	void register(BoardRequestDTO boardRequestDTO, MultipartFile multipartFile) throws IOException;

//	게시글 수정
	ResponseEntity<?> update(BoardRequestDTO boardRequestDTO, MultipartFile multipartFile, boolean deleteFile,
			CustomUserDetail userDetails) throws IOException;

//	게시글 삭제
	ResponseEntity<?> delete(int bno, CustomUserDetail userDetails);
	
//	게시글 복원
	ResponseEntity<?> restore(int bno);

//	인기글 조회
	List<BoardResponseDTO> popularBoards();

//	카테고리별 게시글 조회
	List<BoardResponseDTO> loadBoardsByCategory(String category);

//	전체 카테고리 6개 게시글 조회
	List<BoardResponseDTO> loadBoardsByAll();

//	최근 2개의 게시글 조회
	List<BoardResponseDTO> recentBoards();

//	이미지 s3 저장
	String uploadImage(MultipartFile image) throws IOException;

//	사용자의 게시글 수 가져오기
	long getUserPostCount(String userId);

//	태그 많이 사용된 순으로 5개 가져오기
	List<TagCountResponseDTO> getTop6Tags();
}
