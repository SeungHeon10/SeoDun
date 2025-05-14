package com.board.notice.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.board.notice.entity.User;

public interface UserRepository extends JpaRepository<User , String>{
	// 이메일로 유저 검색
	Optional<User> findByEmail(String email);
}
