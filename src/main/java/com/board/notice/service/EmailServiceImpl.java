package com.board.notice.service;

import org.springframework.stereotype.Service;

import com.board.notice.repository.EmailRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl {
	private final EmailRepository emailRepository;
}
