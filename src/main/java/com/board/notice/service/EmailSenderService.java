package com.board.notice.service;

import java.util.concurrent.CompletableFuture;

public interface EmailSenderService {
	
	public CompletableFuture<Boolean> sendVerificationEmail(String email);
	
}
