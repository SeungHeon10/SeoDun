package com.board.notice.service;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.board.notice.dto.response.ReplyLikeResponseDTO;
import com.board.notice.entity.Reply;
import com.board.notice.entity.ReplyLike;
import com.board.notice.entity.User;
import com.board.notice.repository.ReplyLikeRepository;
import com.board.notice.repository.ReplyRepository;
import com.board.notice.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReplyLikeServiceImpl implements ReplyLikeService {
	private final ReplyRepository replyRepository;
	private final ReplyLikeRepository replyLikeRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public ReplyLikeResponseDTO toggleReplyLikeCount(int replyId, String userId) {
		Reply reply = replyRepository.findById(replyId)
				.orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

		Optional<ReplyLike> existing = replyLikeRepository.findByReply_RnoAndUser_Id(replyId, userId);

		boolean liked;
		if (existing.isPresent()) {
			// 이미 좋아요 → 취소
			replyLikeRepository.delete(existing.get());
			reply.decLike();
			liked = false;
		} else {
			// 아직 좋아요 안 함 → 추가
			User userRef = userRepository.getReferenceById(userId);
			try {
				replyLikeRepository.save(new ReplyLike(reply, userRef));
				reply.incLike();
				liked = true;
			} catch (DataIntegrityViolationException dup) {
				// 동시 클릭 등으로 unique 제약 위반 시 재계산
				liked = replyLikeRepository.findByReply_RnoAndUser_Id(replyId, userId).isPresent();
			}
		}

		// 최종적으로 정확한 카운트로 동기화
		int count = (int) replyLikeRepository.countByReply_Rno(replyId);
		reply.setLikeCount(count);

		return new ReplyLikeResponseDTO(liked, count);
	}
}
