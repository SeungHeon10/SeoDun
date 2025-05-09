package com.board.notice.entity;

import org.hibernate.annotations.Where;

import com.board.notice.dto.request.BoardRequestDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Where(clause = "is_deleted = false")
public class Board extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int bno; //게시글 번호(기본키)
	private String title; //게시글 제목
	@Column(columnDefinition = "TEXT")
	private String content; // 게시글 본문
	private String writer; // 게시글 작성자
	@Column(columnDefinition = "INT DEFAULT 0")
	private int viewCount; // 게시글 조회수
	@Column(columnDefinition = "INT DEFAULT 0")
    private int commentCount; // 게시글 댓글수
    @Column(columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean isPinned; // 게시글 상단고정여부
    private String category; // 게시글 카테고리
    private String filePath; // 게시글 첨부파일 경로
    private String tags; // 게시글 태그 (콤마로 저장)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User userId; // 게시글 작성자 id
    
//  조회수 증가
    public void increaseViewCount() {
    	this.viewCount += 1;
    }
    
//  댓글수 증가
    public void increaseCommentCount() {
    	this.commentCount += 1;
    }
    
//  댓글수 감소
    public void decreaseCommentCount() {
    	this.commentCount -= 1;
    }
    
//  게시글 상단고정여부 변경
    public void toggleIsPinned() {
    	this.isPinned = !this.isPinned;
    }
    
//  게시글 수정
    public void update(BoardRequestDTO boardRequestDTO) {
    	this.title = boardRequestDTO.getTitle();
    	this.content = boardRequestDTO.getContent();
    	this.category = boardRequestDTO.getCategory();
    	this.filePath = boardRequestDTO.getFilePath();
    	this.tags = boardRequestDTO.getTags();
    }
}
