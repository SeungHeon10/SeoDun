package com.board.notice.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.board.notice.dto.request.SocialUserRequestDTO;
import com.board.notice.dto.request.UserRequestDTO;
import com.board.notice.dto.response.UserResponseDTO;
import com.board.notice.enums.Role;
import com.board.notice.security.CustomUserDetail;
import com.board.notice.security.oauth2.CustomOAuth2User;
import com.board.notice.service.BoardService;
import com.board.notice.service.ReplyService;
import com.board.notice.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/users")
public class UserRestController {
	private final UserService userService;
	private final BoardService boardService;
	private final ReplyService replyService;

//	회원 전체 조회(admin)
	@GetMapping("/admin")
	public ResponseEntity<Page<UserResponseDTO>> list(
			@PageableDefault(page = 0, size = 10, sort = "created_at", direction = Sort.Direction.DESC) Pageable pageable,
			@RequestParam(name = "mode", defaultValue = "name") String mode,
			@RequestParam(name = "keyword", defaultValue = "") String keyword) {
		Page<UserResponseDTO> list = userService.list(pageable, mode, keyword);

		return ResponseEntity.ok(list);
	}

//	회원 상세보기
	@GetMapping("/username/{id}")
	public ResponseEntity<UserResponseDTO> detail(@PathVariable("id") String id) {
		UserResponseDTO userResponseDTO = userService.detail(id);

		return ResponseEntity.ok(userResponseDTO);
	}

//	회원 상세보기(admin)
	@GetMapping("/admin/{id}")
	public ResponseEntity<UserResponseDTO> detailForAdmin(@PathVariable("id") String id) {
		UserResponseDTO userResponseDTO = userService.detailForAdmin(id);

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
	public ResponseEntity<String> registerSocial(@RequestBody SocialUserRequestDTO socialUserRequestDTO,
			HttpServletRequest request) {
		CustomOAuth2User oAuth2User = (CustomOAuth2User) request.getSession().getAttribute("oauthUser");

		if (oAuth2User == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("세션 만료 또는 로그인 정보가 없습니다.");
		}

		userService.registerSocial(socialUserRequestDTO, oAuth2User);

		return ResponseEntity.ok("회원가입이 완료되었습니다!");
	}

//	회원 수정하기
	@PutMapping("/{id}/{field}")
	public ResponseEntity<String> update(@PathVariable("id") String id, @PathVariable("field") String field,
			@RequestBody String body, @AuthenticationPrincipal CustomUserDetail me) {
		if (me == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		// 본인 또는 관리자만 수정 가능
		if (!me.getUsername().equals(id) && me.getRole() != Role.ROLE_ADMIN) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
		}
		
		if (body.length() >= 2 && body.startsWith("\"") && body.endsWith("\"")) {
			String value = body.substring(1, body.length() - 1);
			userService.update(id,field,value);
        } else {
        	userService.update(id,field,body);
        }

		return ResponseEntity.ok("수정이 완료되었습니다!");
	}

//	회원 삭제하기
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable("id") String id) {
		userService.delete(id);

		return ResponseEntity.ok("계정이 삭제되었습니다!");
	}

//	회원 복원하기(admin)
	@PatchMapping("/admin/{id}")
	public ResponseEntity<?> restore(@PathVariable("id") String id) {

		return userService.restore(id);
	}

//	아이디 중복 확인
	@GetMapping("/exists/id/{id}")
	public ResponseEntity<Boolean> checkDuplicateId(@PathVariable("id") String id) {
		boolean isDuplicate = userService.isDuplicationId(id);

		return ResponseEntity.ok(isDuplicate);
	}

//	이메일 중복 확인
	@GetMapping("/exists/email/{email}")
	public ResponseEntity<String> checkDuplicateEmail(@PathVariable("email") String email) {
		String msg = userService.isDuplicationEmail(email);

		return ResponseEntity.ok(msg);
	}

//	닉네임 중복 확인
	@GetMapping("/exists/nickname/{nickname}")
	public ResponseEntity<String> checkDuplicateNickname(@PathVariable("nickname") String nickname) {
		String msg = userService.isDuplicationNickname(nickname);

		return ResponseEntity.ok(msg);
	}

//	로그인 회원 정보 가져오기
	@GetMapping("/me")
	public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticationPrincipal CustomUserDetail userDetails) {
		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		long postCount = boardService.getUserPostCount(userDetails.getUsername());
		long commentCount = replyService.getUserCommentCount(userDetails.getUsername());
		return ResponseEntity.ok(new UserResponseDTO(userDetails.getUsername(), userDetails.getRole(),
				userDetails.getName(), userDetails.getNickname(), userDetails.getPno(), userDetails.getEmail(),
				postCount, commentCount, userDetails.getCreatedAt(), userDetails.isEmailVerified(),
				userDetails.isDeleted()));
	}

//	기존 비밀번호 확인
	@PostMapping("/{id}/password/check")
	public ResponseEntity<Boolean> checkCurrentPassword(@AuthenticationPrincipal CustomUserDetail principal,
			@RequestBody String currentPassword) {
		boolean match = userService.checkCurrentPassword(principal.getUsername(), currentPassword);

		return ResponseEntity.ok(match);
	}

}
