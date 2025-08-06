package com.board.notice.dto.response;

import java.time.LocalDateTime;

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
	private String nickname;
	private String pno;
	private String email;
	private long postCount;
	private long commentCount;
	private LocalDateTime createdAt;
	private boolean isDeleted;
	
    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.role = user.getRole();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.pno = user.getPno();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt();
        this.isDeleted = user.isDeleted();
    }
}
