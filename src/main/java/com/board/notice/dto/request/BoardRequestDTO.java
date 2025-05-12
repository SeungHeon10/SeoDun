package com.board.notice.dto.request;

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
	private int bno; //게시글 번호(기본키)
	private String title; //게시글 제목
	private String content; // 게시글 본문
	private String writer; // 게시글 작성자
    private String category; // 게시글 카테고리
    private String filePath; // 게시글 첨부파일 경로
    private boolean filechanged; // 업로드 파일 변경여부
    private String tags; // 게시글 태그 (콤마로 저장)
    private String userId; // 게시글 작성자 id
}
