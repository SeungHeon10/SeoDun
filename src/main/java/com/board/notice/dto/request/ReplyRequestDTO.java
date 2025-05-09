package com.board.notice.dto.request;

import com.board.notice.entity.Board;
import com.board.notice.entity.Reply;
import com.board.notice.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyRequestDTO {
	private int rno;
	private String content; // 댓글 내용
	private String writer; // 댓글 작성자
	private int likeCount; // 댓글 좋아요
	private Reply parent; // 댓글 부모 (대댓글을 위한)
	private Board board; // 게시글 정보
	private User user; // 작성자 정보
}
