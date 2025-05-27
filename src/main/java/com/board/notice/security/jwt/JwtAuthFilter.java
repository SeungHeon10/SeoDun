package com.board.notice.security.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.board.notice.enums.Role;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String bearer = request.getHeader("Authorization");
		
		if(bearer != null && bearer.toLowerCase().startsWith("bearer ")) {
			String token = bearer.substring(7).trim();
			
			if(jwtUtil.isTokenValid(token)) {
				String id = jwtUtil.getId(token);
				Role role = jwtUtil.getRole(token);
				
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(id, null, List.of(new SimpleGrantedAuthority(role.name())));
				
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
		}
		
		filterChain.doFilter(request, response);
	}

}
