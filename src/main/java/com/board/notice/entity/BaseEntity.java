package com.board.notice.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
	@CreatedDate
	@Column(updatable = false)
	protected LocalDateTime createdAt; // 게시글 작성일
	@LastModifiedDate
	protected LocalDateTime updatedAt; // 게시글 수정일
	@Column(columnDefinition = "TINYINT(1) DEFAULT 0")
	protected boolean isDeleted; // 게시글 삭제여부
	
//	게시글 삭제 여부 변경
	public void toggleIsDeleted() {
		this.isDeleted = !this.isDeleted;
	}
}
