package com.board.notice.dto.response;

import java.time.LocalDateTime;

import com.board.notice.entity.Board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class BoardResponseDTO {
	private int bno; // 게시글 번호(기본키)
	private String title; // 게시글 제목
	private String content; // 게시글 본문
	private String writer; // 게시글 작성자
	private int viewCount; // 게시글 조회수
	private int commentCount; // 게시글 댓글수
	private String category; // 게시글 카테고리
	private String filePath; // 게시글 첨부파일 경로
	private String tags; // 게시글 태그 (콤마로 저장)
	private String userId; // 게시글 작성자 id
	private LocalDateTime createdAt;
	
	public BoardResponseDTO(Board board) {
		this.bno = board.getBno();
		this.title = board.getTitle();
		this.content = board.getContent();
		this.writer = board.getWriter();
		this.viewCount = board.getViewCount();
		this.commentCount = board.getCommentCount();
		this.category = board.getCategory();
		this.filePath = board.getFilePath();
		this.tags = board.getTags();
		this.userId = board.getUserId().getId();
		this.createdAt = board.getCreatedAt();
	}
	
	public BoardResponseDTO(int bno, String category, String title, int commentCount, String writer, LocalDateTime createdAt, int viewCount) {
        this.bno = bno;
        this.category = category;
        this.title = title;
        this.commentCount = commentCount;
        this.writer = writer;
        this.createdAt = createdAt;
        this.viewCount = viewCount;
    }

	public static BoardResponseDTO fromEntity(Board board) {
		return new BoardResponseDTO(board.getBno(), board.getCategory(),board.getTitle(), board.getCommentCount(), board.getWriter(),
				board.getCreatedAt(), board.getViewCount());
	}
}
