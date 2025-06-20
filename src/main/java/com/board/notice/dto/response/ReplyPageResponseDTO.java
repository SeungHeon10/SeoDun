package com.board.notice.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ReplyPageResponseDTO {
    private List<ReplyResponseDTO> parentReplies; // 페이징된 부모 댓글
    private List<ReplyResponseDTO> childReplies;  // 전체 자식 댓글
    private PageInfo pageInfo;                    // 페이지 정보

    @Getter 
    @Setter
    public static class PageInfo {
    	private int number;       // 현재 페이지 (0부터 시작)
        private int totalPages;   // 전체 페이지 수
        private boolean first;    // 첫 페이지 여부
        private boolean last;     // 마지막 페이지 여부
    }
}
