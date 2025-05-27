package com.board.notice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.board.notice.security.jwt.JwtAuthFilter;
import com.board.notice.security.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	private final UserDetailsService userDetailsService;
	private final JwtUtil jwtUtil;
	
	@Bean
	public SecurityFilterChain customFilterChain(HttpSecurity http) throws Exception {
		
		// CSRF 보안 설정 비활성화
		http.csrf(csrf -> csrf
			.disable()
				
		)
		// URL 접근 권한 설정
	    .authorizeHttpRequests(auth -> auth
	        // /home, /login, 정적 리소스(css/js 등)은 모두 접근 허용
	        .requestMatchers("/users/**", "/", "/login", "/user/register", "/membership", "/succ-member", "/.well-known/**", "/css/**", "/fonts/**", "/img/**", "/js/**").permitAll()
	        // 나머지 모든 요청은 로그인한 사용자만 접근 가능
	        .anyRequest().authenticated()
	    )

	    // 로그인 설정
//	    .formLogin(login -> login
//	        // 커스텀 로그인 페이지 URL 지정
//	        .loginPage("/login")
//	        // 로그인 성공 시 기본 이동 페이지 설정
//	        .defaultSuccessUrl("/", true)
//	        .permitAll() // 로그인 페이지는 누구나 접근 가능하게 허용
//	    )

	    // 로그아웃 설정
//	    .logout(logout -> logout
//	        // 로그아웃 요청을 받을 URL
//	        .logoutUrl("/logout")
//	        // 로그아웃 후 이동할 URL
//	        .logoutSuccessUrl("/")
//	        // 세션 쿠키 삭제
//	        .deleteCookies("JSESSIONID")
//	        // 세션 무효화
//	        .invalidateHttpSession(true)
//	        .permitAll() // 로그아웃 URL도 누구나 접근 가능
//	    )

	    // Remember-Me 기능 설정
//	    .rememberMe(remember -> remember
//	        // Remember-Me 쿠키 암호화에 사용할 고유한 키
//	        .key("my-super-secret-key-2025")
//	        // HTML form에서 사용하는 체크박스 name 속성값
//	        .rememberMeParameter("remember_login")
//	        // 로그인 기억 지속 시간 (초 단위) → 60 * 60 * 60 * 7 = 15일
//	        .tokenValiditySeconds(60 * 60 * 60 * 7)
//	        .userDetailsService(userDetailsService)
//	    )

	    // 세션 설정
	    .sessionManagement(session -> session
//	        // 로그인 시 새로운 세션 생성
//	        .sessionFixation().migrateSession()
//	        // 동시에 허용할 최대 세션 수
//	        .maximumSessions(1)
//	        // 이미 로그인 중인 세션이 있어도 새 로그인 허용
//	        .maxSessionsPreventsLogin(false)
	    	.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	    )

	    // 예외 처리 설정
	    .exceptionHandling(ex -> ex
	        // 권한 없는 사용자 접근 시 보여줄 커스텀 403 에러 페이지
	        .accessDeniedPage("/error-403.html")
	    )
	    
	    .addFilterBefore(new JwtAuthFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
	    return config.getAuthenticationManager();
	}
}
