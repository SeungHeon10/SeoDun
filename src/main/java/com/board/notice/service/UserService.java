package com.board.notice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.board.notice.dto.request.SocialUserRequestDTO;
import com.board.notice.dto.request.UserRequestDTO;
import com.board.notice.dto.response.UserResponseDTO;
import com.board.notice.security.oauth2.CustomOAuth2User;

public interface UserService {

//	회원목록 조회
	Page<UserResponseDTO> list(Pageable pageable, String mode, String keyword);

//	회원 상세보기
	UserResponseDTO detail(String id);

//	회원 상세보기(admin)
	UserResponseDTO detailForAdmin(String id);

//	회원 등록
	void register(UserRequestDTO userDTO);

//	회원 등록(소셜)
	void registerSocial(SocialUserRequestDTO userRequestDTO, CustomOAuth2User oAuth2User);

//	회원 수정
	void update(String id, String field, String value);

//	회원 삭제
	void delete(String id);

//	회원 복원
	ResponseEntity<?> restore(String id);

//	아이디 중복 여부
	boolean isDuplicationId(String id);

//	이메일 중복 여부
	String isDuplicationEmail(String email);

//	닉네임 중복 여부
	String isDuplicationNickname(String nickname);

//	기존 비밀번호 확인
	boolean checkCurrentPassword(String id, String currentPassword);
}
