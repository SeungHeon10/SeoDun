package com.board.notice.dto.response;

import com.board.notice.entity.User;
import com.board.notice.enums.Role;

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
public class UserResponseDTO {
	private String id;
	private Role role;
	private String name;
	private String pno;
	private String email;
	private long postCount;
	private long commentCount;
	
    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.pno = user.getPno();
        this.email = user.getEmail();
    }
}
