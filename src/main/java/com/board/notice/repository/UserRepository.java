package com.board.notice.repository;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.board.notice.entity.User;

public interface UserRepository extends JpaRepository<User , String>{
	// 이메일로 유저 검색
	Optional<User> findByEmail(String email);
	
	// 이름으로 검색
    Page<User> findByNameContaining(String keyword, Pageable pageable);

    // 아이디로 검색
    Page<User> findByIdContaining(String keyword, Pageable pageable);
    
    // 닉네임으로 검색
    Page<User> findByNicknameContaining(String keyword, Pageable pageable);

}
