package com.board.notice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.board.notice.dto.request.BoardRequestDTO;
import com.board.notice.dto.response.BoardResponseDTO;
import com.board.notice.entity.Board;
import com.board.notice.entity.User;
import com.board.notice.repository.BoardRepository;
import com.board.notice.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{
	private final BoardRepository boardRepository;
	private final UserRepository userRepository;
	
//	게시글 전체조회
	@Override
	public List<BoardResponseDTO> list() {
		List<Board> list = boardRepository.findAll();
		
		return list.stream().map(board -> new BoardResponseDTO(board)).toList();
	}
	
//	게시글 상세보기
	@Override
	@Transactional
	public BoardResponseDTO detail(int bno) {
		Board board = boardRepository.findById(bno).orElseThrow(() -> new EntityNotFoundException("해당 게시글은 존재하지 않습니다."));
		// 조회수 증가 메서드
		board.increaseViewCount();
		// DB 반영
		boardRepository.save(board);
		return new BoardResponseDTO(board);
	}
	
//	게시글 등록하기
	@Override
	@Transactional
	public void register(BoardRequestDTO boardRequestDTO) {
		// 회원 조회
		User user = userRepository.findById(boardRequestDTO.getUserId()).orElseThrow(() -> new UsernameNotFoundException("해당 회원은 존재하지 않습니다."));
		
		// 게시글 등록
		Board board = Board.builder()
				.title(boardRequestDTO.getTitle())
				.content(boardRequestDTO.getContent())
				.category(boardRequestDTO.getCategory())
				.writer(boardRequestDTO.getWriter())
				.filePath(boardRequestDTO.getFilePath())
				.tags(boardRequestDTO.getTags())
				.userId(user)
				.build();
		boardRepository.save(board);
	}
	
//	게시글 수정하기
	@Override
	@Transactional
	public void update(BoardRequestDTO boardRequestDTO) {
		// 수정할 게시글 찾기
		Board board = boardRepository.findById(boardRequestDTO.getBno()).orElseThrow(() -> new EntityNotFoundException("해당 게시글은 존재하지 않습니다."));
		// 게시글 수정 메서드
		board.update(boardRequestDTO);
		// 게시글 DB 반영
		boardRepository.save(board);
	}
	
//	게시글 삭제하기
	@Override
	@Transactional
	public void delete(int bno) {
		// 삭제할 게시글 찾기
		Board board = boardRepository.findById(bno).orElseThrow(() -> new EntityNotFoundException("해당 게시글은 존재하지 않습니다."));
		// 게시글 소프트 삭제 메서드
		board.toggleIsDeleted();
		// 게시글 DB 반영
		boardRepository.save(board);
	}

}
