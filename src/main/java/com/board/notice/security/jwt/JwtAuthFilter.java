package com.board.notice.security.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.board.notice.entity.User;
import com.board.notice.enums.Role;
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
		if(bearer != null && bearer.toLowerCase().startsWith("bearer ")) {
			String token = bearer.substring(7).trim();
			
			if(jwtUtil.isTokenValid(token)) {
				User user = userRepository.findById(jwtUtil.getId(token)).orElse(null);
				CustomUserDetail userDetail = new CustomUserDetail(user);
				
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
				
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
		}
		
		filterChain.doFilter(request, response);
	}

}
