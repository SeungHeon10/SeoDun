package com.board.notice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.board.notice.repository.UserRepository;
import com.board.notice.security.jwt.JwtAuthFilter;
import com.board.notice.security.jwt.JwtUtil;
import com.board.notice.security.oauth2.CustomOAuth2UserService;
import com.board.notice.security.oauth2.OAuth2LoginSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtUtil jwtUtil;
	private final CustomOAuth2UserService auth2UserService;
	private final OAuth2LoginSuccessHandler successHandler;
	private final UserRepository userRepository;
	
	@Bean
	public SecurityFilterChain customFilterChain(HttpSecurity http) throws Exception {
		
		// CSRF 보안 설정 비활성화
		http.csrf(csrf -> csrf
			.disable()
				
		)
		// URL 접근 권한 설정
	    .authorizeHttpRequests(auth -> auth
	        // /home, /login, 정적 리소스(css/js 등)은 모두 접근 허용
	        .requestMatchers("/board/**", "/api/boards/**", "/users/**", "/", "/login", "/token", "/auth/emails/**", "/logout", "/signup-extra", "/membership", "/succ-member", "/social-login-success.html", "/.well-known/**", "/css/**", "/fonts/**", "/html/**", "/img/**", "/js/**").permitAll()
	        // 나머지 모든 요청은 로그인한 사용자만 접근 가능
	        .anyRequest().authenticated()
	    )

	    // 세션 설정
	    .sessionManagement(session -> session
	    	.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	    )
	    .logout(logout -> logout.disable())

	    // 예외 처리 설정
	    .exceptionHandling(ex -> ex
	        // 권한 없는 사용자 접근 시 보여줄 커스텀 403 에러 페이지
	        .accessDeniedPage("/error-403.html")
	    )
	    
	    // OAuth2 설정
	    .oauth2Login(oauth -> oauth
	    	.loginPage("/login")
	    	.userInfoEndpoint(user -> user.userService(auth2UserService))
	    	.successHandler(successHandler)
	    )
	    
	    .addFilterBefore(new JwtAuthFilter(jwtUtil, userRepository), UsernamePasswordAuthenticationFilter.class);
		
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
