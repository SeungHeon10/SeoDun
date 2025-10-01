package com.board.notice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DwellTimeRequestDTO {
	private String userId;
	private int boardId;
	private Double dwellTime;
	private Integer interactionCount;
}
