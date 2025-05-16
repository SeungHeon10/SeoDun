package com.board.notice.entity;

import org.hibernate.annotations.Where;

import com.board.notice.dto.request.UserRequestDTO;
import com.board.notice.enums.Role;

import jakarta.persistence.Column;
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
@Where(clause = "is_deleted = false")
public class User extends BaseEntity{
	@Id
	private String id; // 회원 아이디
	private String password; // 회원 비밀번호
	private String name; // 회원 이름
	private String pno; // 회원 휴대폰 번호
	private String email; // 회원 이메일
	@Builder.Default
	@Column(columnDefinition = "TINYINT(1) DEFAULT 0")
	private boolean emailVerified = false; // 회원 이메일 인증 여부
	@Enumerated(EnumType.STRING)
	private Role role; // 권한
	
//	비밀번호 변경
	public void changePassword(String newPassword) {
		String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$";
		
		if (!newPassword.matches(regex)) {
	        throw new IllegalArgumentException("비밀번호는 8~20자, 영문자/숫자/특수문자를 모두 포함해야 합니다.");
	    }
		
		this.password = newPassword;
	}
	
//	이메일 인증 확인여부 변경
	public void changeEmailVerified() {
		this.emailVerified = true;
	}
	
//	회원 정보 수정
	public void update(UserRequestDTO userRequestDTO) {
		this.password = userRequestDTO.getPassword();
		this.pno = userRequestDTO.getPno();
		this.email = userRequestDTO.getEmail();
	}
}
