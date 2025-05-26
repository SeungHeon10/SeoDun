package com.board.notice.dto.response;

import java.io.Serializable;

import com.board.notice.entity.User;

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
	private String name;
	private String pno;
	private String email;
	
    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.pno = user.getPno();
        this.email = user.getEmail();
    }
}
