package com.board.notice.entity;

import com.board.notice.dto.request.UserRequestDTO;
import com.board.notice.enums.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Builder
public class User extends BaseEntity{
	@Id
	private String id;
	private String password;
	private String name;
	private String pno;
	private String email;
	@Enumerated(EnumType.STRING)
	private Role role;
	
//	비밀번호 변경
	public void changePassword(String newPassword) {
		String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$";
		
		if (!newPassword.matches(regex)) {
	        throw new IllegalArgumentException("비밀번호는 8~20자, 영문자/숫자/특수문자를 모두 포함해야 합니다.");
	    }
		
		this.password = newPassword;
	}
	
//	회원 정보 수정
	public void update(UserRequestDTO userRequestDTO) {
		this.password = userRequestDTO.getPassword();
		this.pno = userRequestDTO.getPno();
		this.email = userRequestDTO.getEmail();
	}
}
