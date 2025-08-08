package com.board.notice.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.board.notice.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
	// 이메일로 유저 검색
	@Query(value = "SELECT * FROM `user` WHERE email = :email", nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);

	// 이름으로 검색
	@Query(value = """
            SELECT * 
            FROM `user`
            WHERE name LIKE CONCAT('%', :keyword, '%')
            """,
    countQuery = """
            SELECT COUNT(*) 
            FROM `user`
            WHERE name LIKE CONCAT('%', :keyword, '%')
            """,
			nativeQuery = true)
	Page<User> findByNameContaining(@Param("keyword") String keyword, Pageable pageable);

	// 아이디로 검색
	@Query(value = """
            SELECT * 
            FROM `user`
            WHERE id LIKE CONCAT('%', :keyword, '%')
            """,
    countQuery = """
            SELECT COUNT(*) 
            FROM `user`
            WHERE id LIKE CONCAT('%', :keyword, '%')
            """,
    nativeQuery = true)
	Page<User> findByIdContaining(@Param("keyword") String keyword, Pageable pageable);

	// 닉네임으로 검색
	@Query(value = """
            SELECT * 
            FROM `user`
            WHERE nickname LIKE CONCAT('%', :keyword, '%')
            """,
    countQuery = """
            SELECT COUNT(*) 
            FROM `user`
            WHERE nickname LIKE CONCAT('%', :keyword, '%')
            """,
    nativeQuery = true)
	Page<User> findByNicknameContaining(@Param("keyword") String keyword, Pageable pageable);
	
	// 회원 전체 조회(admin)
	@Query(value = "SELECT * FROM `user`",
	           countQuery = "SELECT COUNT(*) FROM `user`",
	           nativeQuery = true)
	Page<User> findAllNative(Pageable pageable);

	// 회원 조회(admin)
	@Query(value = "SELECT * FROM user WHERE id = :id", nativeQuery = true)
	Optional<User> findByIdNative(@Param("id") String id);

}
