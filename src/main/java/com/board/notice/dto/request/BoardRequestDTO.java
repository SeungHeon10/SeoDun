package com.board.notice.dto.request;

import java.util.List;

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
public class BoardRequestDTO {
	private int bno;
	private String title; //게시글 제목
	private String content; // 게시글 본문
	private String writer; // 게시글 작성자
    private String category; // 게시글 카테고리
    private String filePath; // 게시글 첨부파일 경로
    private List<String> tags; // 게시글 태그 (콤마로 저장)
    private String userId; // 게시글 작성자 id
}
