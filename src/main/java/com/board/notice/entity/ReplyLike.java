package com.board.notice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "reply_like", uniqueConstraints = @UniqueConstraint(columnNames = { "reply_id", "user_id" }))
public class ReplyLike {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // id

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reply_id", nullable = false)
	private Reply reply; // 댓글 id

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user; // 회원 id

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now(); // 누른 시점 기록

	// 생성자
	public ReplyLike(Reply reply, User user) {
		this.reply = reply;
		this.user = user;
	}
}