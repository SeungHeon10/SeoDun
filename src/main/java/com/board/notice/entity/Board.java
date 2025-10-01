package com.board.notice.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Where;

import com.board.notice.dto.request.BoardRequestDTO;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
	@Builder.Default
	@Column(columnDefinition = "INT DEFAULT 0")
	private int viewCount = 0; // 게시글 조회수
	@Builder.Default
	@Column(columnDefinition = "INT DEFAULT 0")
    private int commentCount = 0; // 게시글 댓글수
    private String category; // 게시글 카테고리
    private String filePath; // 게시글 첨부파일 경로
    @ElementCollection
    @CollectionTable(name = "board_tags", joinColumns = @JoinColumn(name = "board_bno"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>(); // 게시글 태그 (콤마로 저장)
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
    
//  태그 등록
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
//  file 수정
    public void setFilePath(String filePath) {
    	this.filePath = filePath;
    }
    
//  게시글 수정
    public void update(BoardRequestDTO boardRequestDTO) {
    	this.title = boardRequestDTO.getTitle();
    	this.content = boardRequestDTO.getContent();
    	this.category = boardRequestDTO.getCategory();
    	this.tags = boardRequestDTO.getTags();
    }
    
}
