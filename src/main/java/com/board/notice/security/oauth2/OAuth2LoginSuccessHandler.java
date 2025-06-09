package com.board.notice.security.oauth2;

import java.io.IOException;
import java.time.Duration;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.board.notice.dto.response.TokenResponseDTO;
import com.board.notice.entity.User;
import com.board.notice.repository.UserRepository;
import com.board.notice.security.jwt.JwtUtil;
import com.board.notice.service.RefreshTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;
	private final RefreshTokenService refreshTokenService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
		String email = oAuth2User.getEmail();

		User user = userRepository.findByEmail(email).orElse(null);
		if (user != null) {
			String accessToken = jwtUtil.createToken(user.getId(), user.getRole());
			String refreshToken = jwtUtil.createRefreshToken(user.getId(), user.getRole());

			ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken).httpOnly(true).path("/")
					.secure(false).maxAge(Duration.ofDays(7)).build();
			response.addHeader("Set-Cookie", cookie.toString());

			refreshTokenService.saveRefreshToken(user.getId(), refreshToken, Duration.ofDays(7));
			
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write("<script>window.name = '" + accessToken + "'; window.location.href = '/html/social-login-success.html';</script>");
		} else {
			request.getSession().setAttribute("oauthUser", oAuth2User);
			response.sendRedirect("/signup-extra");
		}
	}
}
