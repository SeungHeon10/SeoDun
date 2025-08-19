package com.board.notice.service;

import com.board.notice.dto.response.ReplyLikeResponseDTO;

public interface ReplyLikeService {
	public ReplyLikeResponseDTO toggleReplyLikeCount(int replyId, String userId);
}
