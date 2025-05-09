package com.board.notice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.board.notice.dto.request.UserRequestDTO;
import com.board.notice.dto.response.UserResponseDTO;
import com.board.notice.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/user")
public class UserRestController {
	private final UserService userService;
	
//	회원 전체 조회
	@GetMapping("/list")
	public ResponseEntity<List<UserResponseDTO>> list() {
		List<UserResponseDTO> list = userService.list();
		
		return ResponseEntity.ok(list);
	}
	
//	회원 상세보기
	@GetMapping("/detail/{id}")
	public ResponseEntity<UserResponseDTO> detail(@PathVariable("id") String id) {
		UserResponseDTO userResponseDTO = userService.detail(id);
		
		return ResponseEntity.ok(userResponseDTO);
	}
	
//	회원 등록하기
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody UserRequestDTO userRequestDTO) {
		userService.register(userRequestDTO);
		
		return ResponseEntity.ok("회원가입이 완료되었습니다!");
	}
	
//	회원 수정하기
	@PutMapping("/update/{id}")
	public ResponseEntity<String> update(@RequestBody UserRequestDTO userRequestDTO) {
		userService.update(userRequestDTO);
		
		return ResponseEntity.ok("수정이 완료되었습니다!");
	}
	
//	회원 삭제하기
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> delete(@PathVariable("id") String id) {
		userService.delete(id);
		
		return ResponseEntity.ok("삭제가 완료되었습니다!");
	}
	
//	아이디 중복 확인
	@GetMapping("/findId/{id}")
	public ResponseEntity<Boolean> checkDuplicateId(@PathVariable("id") String id) {
		boolean isDuplicate = userService.isDuplicationId(id);
		
		return ResponseEntity.ok(isDuplicate);
	}
	
}
