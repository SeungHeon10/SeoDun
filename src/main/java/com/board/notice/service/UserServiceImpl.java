package com.board.notice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.board.notice.dto.request.UserRequestDTO;
import com.board.notice.dto.response.UserResponseDTO;
import com.board.notice.entity.User;
import com.board.notice.enums.Role;
import com.board.notice.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	
//	회원 전체 조회
	@Override
	public List<UserResponseDTO> list() {
		List<User> users = userRepository.findAll();
		
		return users.stream().map(user -> new UserResponseDTO(user)).toList();
	}
	
//	회원 상세보기
	@Override
	@Cacheable(value = "userDetail", key = "#p0", unless = "#result == null")
	public UserResponseDTO detail(String id) {
		User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("해당 회원은 존재하지 않습니다."));
		
		return new UserResponseDTO(user);
	}

//	회원 등록
	@Override
	@Transactional
	public void register(UserRequestDTO userDTO) {
		User user = User.builder()
		.id(userDTO.getId())
		.name(userDTO.getName())
		.password(passwordEncoder.encode(userDTO.getPassword()))
		.pno(userDTO.getPno())
		.email(userDTO.getEmail())
		.role(Role.ROLE_USER)
		.build();
		
		userRepository.save(user);
	}

//	회원 수정
	@Override
	@Transactional
	@CacheEvict(value = "userDetail", key = "#p0")
	public void update(UserRequestDTO userDTO) {
		User user = userRepository.findById(userDTO.getId()).orElseThrow(() -> new UsernameNotFoundException("해당 회원은 존재하지 않습니다."));
		// 회원 수정 메서드
		user.update(userDTO);	
	}

//	회원 삭제
	@Override
	@Transactional
	@CacheEvict(value = "userDetail", key = "#p0")
	public void delete(String id) {
		User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("해당 회원은 존재하지 않습니다."));
		// 회원 소프트 삭제 메서드
		user.markAsDeleted();;
	}

//	아이디 중복 여부
	@Override
	public boolean isDuplicationId(String id) {
		User user = userRepository.findById(id).orElse(new User("", null, null, null, null, false, null));
		boolean isDuplicate = user.getId().isEmpty() ? true : false;
		
		return isDuplicate;
	}

}
