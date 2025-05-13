package com.board.notice.exception;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.mail.MessagingException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
//	UsernameNotFoundException 처리
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleUserNotFound(UsernameNotFoundException exception){
		Map<String, String> error = new HashMap<String, String>();
		error.put("message", exception.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
//	IOException 처리
	@ExceptionHandler(IOException.class)
	public ResponseEntity<Map<String, String>> handleIOException(IOException exception){
		Map<String, String> error = new HashMap<String, String>();
		error.put("message", exception.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
	
//	EntityNotFoundException 처리
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException exception){
		Map<String, String> error = new HashMap<String, String>();
		exception.getBindingResult().getFieldErrors().forEach(ex -> {
			error.put(ex.getField(), ex.getDefaultMessage());
		});
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
//	MessagingException 처리
	@ExceptionHandler(MessagingException.class)
	public ResponseEntity<Map<String, String>> handleValidationException(MessagingException exception){
		Map<String, String> error = new HashMap<String, String>();
		error.put("message", "이메일 전송에 실패했습니다. 잠시 후 다시 시도해주세요.");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
	
//	RuntimeException 처리
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException exception){
		Map<String, String> error = new HashMap<String, String>();
		error.put("message", exception.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
	
}
