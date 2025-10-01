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
public class UserRequestDTO {
	private String id;
	private String name;
	private String nickname;
	private String password;
	private String pno;
	private String email;
	private boolean emailVerified;
}
