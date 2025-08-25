package com.board.notice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtUtil jwtUtil;
	private final CustomOAuth2UserService auth2UserService;
	private final OAuth2LoginSuccessHandler successHandler;
	private final UserRepository userRepository;

	@Bean
	public SecurityFilterChain customFilterChain(HttpSecurity http) throws Exception {

		// CSRF 보안 설정 비활성화
		http.csrf(csrf -> csrf.disable()

		)
				// URL 접근 권한 설정
				.authorizeHttpRequests(auth -> auth
						// 모두 접근 허용 페이지/API
						.requestMatchers("/", // 홈
								"/login", "/logout", "/recovery/*", // 로그인, 로그아웃, 아이디/비밀번호 찾기
								"/signup-extra", "/succ-member", "/membership", // 회원가입 관련
								"/token", // accessToken 재발급
								"/social-login-success.html", // OAuth 로그인 완료 페이지

								// 정적 리소스
								"/css/**", "/js/**", "/img/**", "/fonts/**", "/html/**", "/.well-known/**",

								// 공개된 페이지
								"/board/**", // 게시판 페이지 이동
								"/api/boards/**", // 게시판 데이터
								"/api/log/dwell-time", // 로그 데이터 전송
								"/user/**", // 회원 페이지 이동
								"/api/users/**", // 사용자 데이터
								"/api/recommend/public", // 맞춤 콘텐츠 조회
								"/auth/emails")
						.permitAll().requestMatchers("/admin/**").hasRole("ADMIN")
						// 나머지 모든 요청은 로그인한 사용자만 접근 가능
						.anyRequest().authenticated())

				// 세션 설정
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.logout(logout -> logout.disable())

				// OAuth2 설정
				.oauth2Login(oauth -> oauth.loginPage("/login")
						.userInfoEndpoint(user -> user.userService(auth2UserService)).successHandler(successHandler))

				.addFilterBefore(new JwtAuthFilter(jwtUtil, userRepository),
						UsernamePasswordAuthenticationFilter.class);

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
