package com.board.notice.entity;

import org.hibernate.annotations.Where;

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
public class Reply extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int rno; // 댓글 번호(기본키)
	@Column(columnDefinition = "TEXT")
	private String content; // 댓글 내용
	private String writer; // 댓글 작성자
	@Builder.Default
	@Column(columnDefinition = "INT DEFAULT 0")
	private int likeCount = 0; // 댓글 좋아요
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Reply parent; // 댓글 부모 (대댓글을 위한)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_id")
	private Board board; // 게시글 정보
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user; // 작성자 정보

//	댓글 내용 수정 메서드
	public void update(String content) {
		this.content = content;
	}

//	댓글 좋아요 증가
	public void incLike() {
		this.likeCount++;
	}

//	댓글 좋아요 감소
	public void decLike() {
		if (this.likeCount > 0)
			this.likeCount--;
	}

//	댓글 좋아요 카운트 조회
	public int getLikeCount() {
		return likeCount;
	}
	
//	댓글 좋아요 수 변경
	public void setLikeCount(int count) {
		this.likeCount = count;
	}
}
