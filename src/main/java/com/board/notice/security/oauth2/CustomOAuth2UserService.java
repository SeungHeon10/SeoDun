package com.board.notice.security.oauth2;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.board.notice.enums.Role;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User auth2User = new DefaultOAuth2UserService().loadUser(userRequest);
		Map<String, Object> attribute = auth2User.getAttributes();
		
		return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(Role.ROLE_USER.toString())), attribute, "id");
	}
	
	private String extractEmail(Map<String, Object> attribute) {
		Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
		return kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
	}
}
