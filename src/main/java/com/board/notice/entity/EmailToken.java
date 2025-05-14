package com.board.notice.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Where;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "is_deleted = false")
public class EmailToken {
	@Id
	private String token; // uuid 형식의 키
	@Column(nullable = false)
	private String email; // 인증받을 사용자 이메일
	@Column(nullable = false)
	private LocalDateTime expiryDate; // 토큰의 만료시간
	@Column(nullable = false , columnDefinition = "TINYINT(1) DEFAULT 0")
	private boolean isConfirmed; // 인증 확인여부
	@Column(nullable = false , columnDefinition = "TINYINT(1) DEFAULT 0")
	private boolean isValid; // 토큰 유효 여부
	
	// 인증 확인여부 변경
	public void changeConfirmed() {
		this.isConfirmed = true;
	}
	
	// 토큰 무효화
	public void invalidate() {
		this.isValid = false;
	}
}
