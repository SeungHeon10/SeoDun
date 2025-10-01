package com.board.notice.dto.request;

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
	private int parent_id; // 댓글 부모 (대댓글을 위한)
	private String user_id; // 작성자 정보
}
