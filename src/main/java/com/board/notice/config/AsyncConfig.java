package com.board.notice.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer{

	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		
		executor.setCorePoolSize(2); // 동시에 실행할 기본 스레드 수 
		executor.setMaxPoolSize(4); // 최대 실행 가능한 스레드 수
		executor.setQueueCapacity(100); // 대기할 수 있는 작업 수 
		executor.setThreadNamePrefix("async-thread-");
		
		executor.initialize();
		return executor;
	}
}
