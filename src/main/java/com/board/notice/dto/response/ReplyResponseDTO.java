package com.board.notice.dto.response;

import java.time.LocalDateTime;

import com.board.notice.entity.Reply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyResponseDTO {
	private int rno; // 댓글 번호(기본키)
	private int parent_id;
	private String content; // 댓글 내용
	private String writer; // 댓글 작성자
	private int likeCount; // 댓글 좋아요
	private LocalDateTime createAt; // 등록일
	private LocalDateTime updatedAt; // 수정일
	
	public ReplyResponseDTO(Reply reply) {
		this.rno = reply.getRno();
		this.content = reply.getContent();
		this.parent_id = reply.getParent() != null ? reply.getParent().getRno() : 0;
		this.writer = reply.getWriter();
		this.likeCount = reply.getLikeCount();
		this.createAt = reply.getCreatedAt();
		this.updatedAt = reply.getUpdatedAt();
	}
}
