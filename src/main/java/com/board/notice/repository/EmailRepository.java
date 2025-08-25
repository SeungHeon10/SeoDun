package com.board.notice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.board.notice.entity.EmailToken;

@Repository
public interface EmailRepository extends JpaRepository<EmailToken, String>{
	// 이메일로 EmailToken엔티티 검색
	Optional<EmailToken> findByEmail(String email);
	// 이메일 + 인증번호 일치 확인
	Optional<EmailToken> findByEmailAndTokenAndIsValidTrueAndIsDeletedFalse(String email, String token);
	// token으로 EmailToken엔티티 검색
	Optional<EmailToken> findByTokenAndIsDeletedFalse(String token);
}
