package com.board.notice.entity;

import org.hibernate.annotations.Where;

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
public class User extends BaseEntity {
	@Id
	private String id; // 회원 아이디
	private String password; // 회원 비밀번호
	private String name; // 회원 이름
	private String nickname; // 회원 닉네임
	private String pno; // 회원 휴대폰 번호
	private String email; // 회원 이메일
	private String provider; // 어떤 플랫폼으로 로그인 했는지
	private String providerId; // 해당 플랫폼 고유 ID
	@Builder.Default
	@Column(columnDefinition = "TINYINT(1) DEFAULT 0")
	private boolean emailVerified = false; // 회원 이메일 인증 여부
	@Enumerated(EnumType.STRING)
	private Role role; // 권한

//	비밀번호 형식 검증
	public void validateRawPassword(String Password) {
		String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$";

		if (!Password.matches(regex)) {
			throw new IllegalArgumentException("비밀번호는 8~20자, 영문자/숫자/특수문자를 모두 포함해야 합니다.");
		}
	}

//	비밀번호 변경
	public void setEncodedPassword(String encodedPassword) {
		if (encodedPassword == null || encodedPassword.isBlank()) {
			throw new IllegalArgumentException("암호화된 비밀번호가 유효하지 않습니다.");
		}

		this.password = encodedPassword;
	}

//	이름 변경
	public void updateName(String name) {
		String regex = "^[가-힣a-zA-Z]{1,20}$";

		if (!name.matches(regex)) {
			throw new IllegalArgumentException("이름은 1자이상 20자이내의 문자로만 입력 가능합니다.");
		}

		this.name = name;
	}

//	닉네임 변경
	public void updateNickname(String nickname) {
		String regex = "^(?=.{2,20}$)[가-힣a-zA-Z0-9 ]+$";

		if (!nickname.matches(regex)) {
			throw new IllegalArgumentException("닉네임은 2자이상 12자이내의 문자/숫자로 입력 가능합니다.");
		}

		this.nickname = nickname;
	}

//	이메일 변경
	public void updateEmail(String email) {
		String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

		if (!email.matches(regex)) {
			throw new IllegalArgumentException("이메일 형식에 맞게 입력 가능합니다.");
		}

		this.email = email;
		this.emailVerified = false;
	}

//	휴대폰번호 변경
	public void updatePhone(String pno) {
		String regex = "^[0-9]{3}-[0-9]{4}-[0-9]{4}$";

		if (!pno.matches(regex)) {
			throw new IllegalArgumentException("휴대폰 번호 형식에 맞게 숫자로 작성해주세요. (예: 010-1234-5678)");
		}

		this.pno = pno;
	}

//	권한 변경(admin)
	public void setRole(Role role) {
		this.role = role;
	}

//	이메일 인증 확인여부 변경
	public void changeEmailVerified() {
		this.emailVerified = true;
	}

//	권한 String 값 Role 타입으로 변경 유틸 메서드
	public Role fromString(String roleString) {
		try {
			return Role.valueOf(roleString.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new IllegalArgumentException("유효하지 않은 권한 값입니다: " + roleString);
		}
	}

}
