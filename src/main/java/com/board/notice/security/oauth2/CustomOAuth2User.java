package com.board.notice.security.oauth2;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {
	private final OAuth2User oAuth2User;
	private final String provider;
	
//	이메일 가져오기
	public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
        return kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
    }
	
//	Provider 가져오기
	public String getProvider() {
		return this.provider;
	}
	
//	ProviderId 가져오기
	public String getProviderId() {
        return oAuth2User.getAttribute("id").toString();
    }
	
//	사용자 정보 가져오기
	@Override
	public Map<String, Object> getAttributes() {

		return oAuth2User.getAttributes();
	}

//	사용자 권한 가져오기
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		return oAuth2User.getAuthorities();
	}

//	사용자 이름 가져오기
	@Override
	public String getName() {

		return oAuth2User.getName();
	}

}
