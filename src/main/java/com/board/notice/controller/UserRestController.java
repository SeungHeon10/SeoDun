package com.board.notice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.board.notice.dto.request.SocialUserRequestDTO;
import com.board.notice.dto.request.UserRequestDTO;
import com.board.notice.dto.response.UserResponseDTO;
import com.board.notice.security.CustomUserDetail;
import com.board.notice.security.oauth2.CustomOAuth2User;
import com.board.notice.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserRestController {
	private final UserService userService;

//	회원 전체 조회
	@GetMapping
	public ResponseEntity<List<UserResponseDTO>> list() {
		List<UserResponseDTO> list = userService.list();

		return ResponseEntity.ok(list);
	}

//	회원 상세보기
	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDTO> detail(@PathVariable("id") String id) {
		UserResponseDTO userResponseDTO = userService.detail(id);

		return ResponseEntity.ok(userResponseDTO);
	}

//	회원 등록하기
	@PostMapping
	public ResponseEntity<String> register(@RequestBody UserRequestDTO userRequestDTO) {
		userService.register(userRequestDTO);

		return ResponseEntity.ok("회원가입이 완료되었습니다!");
	}
	
//	회원 등록하기(소셜)
	@PostMapping("/social")
	public ResponseEntity<String> registerSocial(@RequestBody SocialUserRequestDTO socialUserRequestDTO , HttpServletRequest request) {
		CustomOAuth2User oAuth2User = (CustomOAuth2User) request.getSession().getAttribute("oauthUser");
		
		if (oAuth2User == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("세션 만료 또는 로그인 정보가 없습니다.");
	    }
		
		userService.registerSocial(socialUserRequestDTO, oAuth2User);
		
		return ResponseEntity.ok("회원가입이 완료되었습니다!");
	}

//	회원 수정하기
	@PutMapping("/{id}")
	public ResponseEntity<String> update(@PathVariable("id") String id, @RequestBody UserRequestDTO userRequestDTO) {
		userService.update(userRequestDTO);

		return ResponseEntity.ok("수정이 완료되었습니다!");
	}

//	회원 삭제하기
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable("id") String id) {
		userService.delete(id);

		return ResponseEntity.ok("삭제가 완료되었습니다!");
	}

//	아이디 중복 확인
	@GetMapping("/exists/{id}")
	public ResponseEntity<Boolean> checkDuplicateId(@PathVariable("id") String id) {
		boolean isDuplicate = userService.isDuplicationId(id);

		return ResponseEntity.ok(isDuplicate);
	}
	
//	로그인 회원 정보 가져오기
	@GetMapping("/me")
	public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticationPrincipal CustomUserDetail userDetails) {
	    return ResponseEntity.ok(new UserResponseDTO(userDetails.getUsername(), userDetails.getName(), userDetails.getPno(), userDetails.getEmail()));
	}

}
