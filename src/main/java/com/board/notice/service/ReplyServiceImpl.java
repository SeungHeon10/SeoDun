package com.board.notice.service;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.board.notice.dto.request.ReplyRequestDTO;
import com.board.notice.dto.response.ReplyResponseDTO;
import com.board.notice.entity.Board;
import com.board.notice.entity.Reply;
import com.board.notice.entity.User;
import com.board.notice.repository.BoardRepository;
import com.board.notice.repository.ReplyRepository;
import com.board.notice.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {
	private final ReplyRepository replyRepository;
	private final BoardRepository boardRepository;
	private final UserRepository userRepository;

//	댓글 전체조회
	@Override
	public List<ReplyResponseDTO> list() {
		List<Reply> list = replyRepository.findAll();
		return list.stream().map(reply -> new ReplyResponseDTO(reply)).toList();
	}

//	댓글 등록
	@Override
	@Transactional
	public void register(ReplyRequestDTO replyRequestDTO) {
		// 부모 댓글 정보 (대댓글의 상위 댓글)
		Reply parent = null;
		// 대댓글의 상위 정보 있을 시 정보 가져오는 로직
		if(replyRequestDTO.getParent() != null) {
			parent = replyRepository.findById(replyRequestDTO.getParent().getRno()).orElseThrow(() -> new EntityNotFoundException("해당 댓글은 존재하지 않습니다."));
		}
		// 게시글 정보
		Board board = boardRepository.findById(replyRequestDTO.getBoard().getBno()).orElseThrow(() -> new EntityNotFoundException("해당 게시글은 존재하지 않습니다."));
		// 회원 정보
		User user = userRepository.findById(replyRequestDTO.getUser().getId()).orElseThrow(() -> new UsernameNotFoundException("해당 회원은 존재하지 않습니다."));
		Reply reply = Reply.builder()
				.content(replyRequestDTO.getContent())
				.writer(replyRequestDTO.getWriter())
				.board(board)
				.user(user)
				.build();
		replyRepository.save(reply);
	}

//	댓글 수정
	@Override
	@Transactional
	public void update(ReplyRequestDTO replyRequestDTO) {
		// 수정할 댓글 찾기
		Reply reply = replyRepository.findById(replyRequestDTO.getRno()).orElseThrow(() -> new EntityNotFoundException("해당 댓글은 존재하지 않습니다."));
		// 댓글 수정 메서드
		reply.update(replyRequestDTO.getContent());
	}

//	댓글 삭제
	@Override
	@Transactional
	public void delete(int rno) {
		Reply reply = replyRepository.findById(rno).orElseThrow(() -> new EntityNotFoundException("해당 댓글은 존재하지 않습니다."));
		reply.toggleIsDeleted();
	}

}
