package com.board.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.board.notice.entity.EmailToken;

@Repository
public interface EmailRepository extends JpaRepository<EmailToken, String>{

}
