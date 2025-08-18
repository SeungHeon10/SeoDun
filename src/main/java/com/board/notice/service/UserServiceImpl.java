package com.board.notice.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.board.notice.dto.request.SocialUserRequestDTO;
import com.board.notice.dto.request.UserRequestDTO;
import com.board.notice.dto.response.UserResponseDTO;
import com.board.notice.entity.User;
import com.board.notice.enums.Role;
import com.board.notice.repository.UserRepository;
import com.board.notice.security.oauth2.CustomOAuth2User;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;

//	회원 전체 조회
	@Override
	public Page<UserResponseDTO> list(Pageable pageable, String mode, String keyword) {
		boolean noKeyword = (keyword == null || keyword.trim().isEmpty() || "undefined".equals(keyword));

		if (noKeyword) {
			// 검색어 없을 때
			return userRepository.findAllNative(pageable).map(UserResponseDTO::new);
		}

		// 검색어 있을 때
		switch (mode) {
		case "name":
			return userRepository.findByNameContaining(keyword, pageable).map(UserResponseDTO::new);
		case "id":
			return userRepository.findByIdContaining(keyword, pageable).map(UserResponseDTO::new);
		case "nickname":
			return userRepository.findByNicknameContaining(keyword, pageable).map(UserResponseDTO::new);
		default:
			return userRepository.findAllNative(pageable).map(UserResponseDTO::new);
		}
	}

//	회원 상세보기
	@Override
	public UserResponseDTO detail(String id) {
		User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("해당 회원은 존재하지 않습니다."));

		return new UserResponseDTO(user);
	}

//	회원 상세보기(admin)
	@Override
	public UserResponseDTO detailForAdmin(String id) {
		User user = userRepository.findByIdNative(id)
				.orElseThrow(() -> new UsernameNotFoundException("해당 회원은 존재하지 않습니다."));

		return new UserResponseDTO(user);
	}

//	회원 등록
	@Override
	@Transactional
	public void register(UserRequestDTO userDTO) {
		User user = User.builder().id(userDTO.getId()).password(passwordEncoder.encode(userDTO.getPassword()))
				.name(userDTO.getName()).pno(userDTO.getPno()).email(userDTO.getEmail()).role(Role.ROLE_USER).build();

		userRepository.save(user);
	}

//	회원 등록(소셜)
	@Override
	@Transactional
	public void registerSocial(SocialUserRequestDTO userRequestDTO, CustomOAuth2User oAuth2User) {
		String id = oAuth2User.getProvider() + "_" + oAuth2User.getProviderId();
		String password = UUID.randomUUID().toString();

		User user = User.builder().id(id).password(passwordEncoder.encode(password)).name(userRequestDTO.getName())
				.pno(userRequestDTO.getPno()).email(oAuth2User.getEmail()).provider(oAuth2User.getProvider())
				.providerId(oAuth2User.getProviderId()).role(Role.ROLE_USER).build();

		userRepository.save(user);
	}

//	회원 수정
	@Override
	@Transactional
	public void update(String id, String field, String value) {
		User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("해당 회원은 존재하지 않습니다."));

		switch (field) {
		case "password":
			user.validateRawPassword(value);
			String encoded = passwordEncoder.encode(value);
			user.setEncodedPassword(encoded);
			break;

		case "name":
			user.updateName(value);
			break;

		case "nickname":
			if (userRepository.existsByNicknameAndIdNot(value, id)) {
				throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
			}
			
			user.updateNickname(value);
			break;

		case "phone":
			user.updatePhone(value);
			break;

		case "email":
			if (userRepository.existsByEmailAndIdNot(value, id)) {
				throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
			}
			
			user.updateEmail(value);
			break;
			
		case "role":
			Role changeRole = user.fromString(value);
			user.setRole(changeRole);
			break;
		default:
			throw new IllegalArgumentException("지원하지 않는 수정 항목입니다.");
		}
	}

//	회원 삭제
	@Override
	@Transactional
	public void delete(String id) {
		User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("해당 회원은 존재하지 않습니다."));
		// 회원 소프트 삭제 메서드
		user.markAsDeleted();
	}

//	회원 복원하기
	@Override
	@Transactional
	public ResponseEntity<?> restore(String id) {
		User user = userRepository.findByIdNative(id)
				.orElseThrow(() -> new EntityNotFoundException("해당 회원은 존재하지 않습니다."));

		// 게시글 복원 메서드
		user.markAsRestored();

		return ResponseEntity.ok("게시글이 복원되었습니다.");
	}

//	아이디 중복 여부
	@Override
	public boolean isDuplicationId(String id) {
		User user = userRepository.findById(id).orElse(null);
		boolean isDuplicate = user == null ? true : false;

		return isDuplicate;
	}

//	이메일 중복 여부
	@Override
	public String isDuplicationEmail(String email) {
		User user = userRepository.findByEmail(email).orElse(null);

		if (user != null) {
			return user.getProvider() != null ? "소셜 가입된 이메일입니다. 소셜 로그인을 이용해주세요." : "이미 가입된 이메일 입니다.";
		} else {
			return "사용 가능한 이메일 입니다.";
		}
	}

//	닉네임 중복 여부
	@Override
	public String isDuplicationNickname(String nickname) {
		User user = userRepository.findByNickname(nickname).orElse(null);

		if (user != null) {
			return "이미 존재하는 닉네임 입니다.";
		} else {
			return "사용 가능한 닉네임 입니다.";
		}
	}

//	기존 비밀번호 확인
	@Override
	public boolean checkCurrentPassword(String id, String currentPassword) {
		User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 회원은 존재하지 않습니다."));

		return user.getPassword() != null && passwordEncoder.matches(currentPassword, user.getPassword());
	}

}
