package com.board.notice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 검색 기록 고유 ID

    @Column(nullable = false)
    private Long userId;  // 검색한 사용자 ID

    @Column(length = 100)
    private String keyword;  // 검색어

    private Long clickedPostId;  // 클릭한 게시글 ID (없을 수 있으므로 nullable)

    @Column(length = 50)
    private String category;  // 검색 당시 카테고리

    private LocalDateTime searchedAt;  // 검색 발생 시각

    private Integer sessionDuration;  // 체류 시간 (초)
}
