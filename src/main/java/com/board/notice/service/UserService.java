package com.board.notice.service;

import java.util.List;

import com.board.notice.dto.request.SocialUserRequestDTO;
import com.board.notice.dto.request.UserRequestDTO;
import com.board.notice.dto.response.UserResponseDTO;
import com.board.notice.security.oauth2.CustomOAuth2User;

public interface UserService {
	
//	회원목록 조회
	List<UserResponseDTO> list();
//	회원 상세보기
	UserResponseDTO detail(String id);
//	회원 등록
	void register(UserRequestDTO userDTO);
//	회원 등록(소셜)
	void registerSocial(SocialUserRequestDTO userRequestDTO, CustomOAuth2User oAuth2User);
//	회원 수정
	void update(UserRequestDTO userDTO);
//	회원 삭제
	void delete(String id);
//	아이디 중복 여부
	boolean isDuplicationId(String id);
//	이메일 중복 여부
	String isDuplicationEmail(String email);
}
