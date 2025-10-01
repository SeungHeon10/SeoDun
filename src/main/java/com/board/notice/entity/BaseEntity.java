package com.board.notice.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Builder;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
	@CreatedDate
	@Column(updatable = false)
	protected LocalDateTime createdAt; // 게시글 작성일
	@LastModifiedDate
	protected LocalDateTime updatedAt; // 게시글 수정일
	@Builder.Default
	@Column(columnDefinition = "TINYINT(1) DEFAULT 0")
	protected boolean isDeleted = false; // 게시글 삭제여부
	
//	게시글 소프트 삭제
	public void markAsDeleted() {
		this.isDeleted = true;
	}
	
//	게시글 복원
	public void markAsRestored() {
		this.isDeleted = false;
	}
	
	
}
