package com.board.notice.security.jwt;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.board.notice.entity.User;
import com.board.notice.repository.UserRepository;
import com.board.notice.security.CustomUserDetail;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String bearer = request.getHeader("Authorization");
		if (bearer != null && bearer.toLowerCase().startsWith("bearer ")) {
			String token = bearer.substring(7).trim();

			if (jwtUtil.isTokenValid(token)) {
				String userId = jwtUtil.getId(token);
				Optional<User> optionalUser = userRepository.findById(userId);
				if (optionalUser.isPresent()) {
					User user = optionalUser.get();
					CustomUserDetail userDetail = new CustomUserDetail(user);

					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetail, null,
							userDetail.getAuthorities());

					SecurityContextHolder.getContext().setAuthentication(auth);
				} else {
					// 토큰은 유효하지만 사용자 없음 → 인증 실패 처리
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.getWriter().write("User not found");
					return; // ❌ 더 이상 진행 안 함
				}
			}
		}

		filterChain.doFilter(request, response);
	}

}
