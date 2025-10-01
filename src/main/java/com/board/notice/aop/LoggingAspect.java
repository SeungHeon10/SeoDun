package com.board.notice.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
	
	@Pointcut("execution(* com.board.notice.controller..*(..)) || execution(* com.board.notice.service..*(..)) || execution(* com.board.notice.repository..*(..))")
	public void applicationPackagePointcut() {
		
	}
	
	@Around("applicationPackagePointcut()")
	public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		String className = signature.getDeclaringType().getSimpleName();
		String methodName = signature.getName();
		Object[] args = joinPoint.getArgs();
		
		log.info("▶️ [{}#{}] 호출 - 파라미터: {}", className, methodName, args);
		long start = System.currentTimeMillis();
		
		try {
			Object result = joinPoint.proceed();
			long duration = System.currentTimeMillis() - start;
			
            log.info("✅ [{}#{}] 정상 종료 - 실행 시간: {}ms", className, methodName, duration);
            return result;
		} catch (Throwable e) {
			log.error("❌ [{}#{}] 예외 발생: {}", className, methodName, e.getMessage(), e);
			throw e;
		}
	}
}
