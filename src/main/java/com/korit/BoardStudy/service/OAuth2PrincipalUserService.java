package com.korit.BoardStudy.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OAuth2PrincipalUserService extends DefaultOAuth2UserService {

    @Override //사용자 정보 추출
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);//userRequest = 로그인 요청에 대한 정보
                                //super = DefaultOAuth2UserService를 말함

        Map<String, Object> attributes = oAuth2User.getAttributes();

        String provider = userRequest.getClientRegistration().getRegistrationId();//ClientRegistration의 고유 식별자(Id)를 문자열로 반환
             //구글, 네이버, 카카오 등   //OAuth2서비스로 로그인 요청이 들어왔는지
        String email = null;
        String id = null;

        switch (provider) {
            case "google":
                id = attributes.get("sub").toString();//get의 형태가 object -> 어떤 형태든 String 형태로 저장하기 위함
                email = (String) attributes.get("email");
                break;
            case "naver":
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                                                                    //attributes안에 -> response안에 사용자 정보
                id = response.get("id").toString();
                email = (String) response.get("email");
                break;
            case "kakao":
                id = attributes.get("id").toString();
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                                                                        //네이버랑 동일
                email = (String) kakaoAccount.get("email");
                break;
        }

        Map<String, Object> newAttributes = Map.of(
                "id", id,
                "provider", provider,
                "email", email
        );

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_TEMPORARY"));
            //사용자의 권한을 나타내는 클래스                                                 //임시 사용자
        return new DefaultOAuth2User(authorities, newAttributes, "id");
                                    //권한 리스트, 사용자 주요 정보(Map), 사용자의 주요 속성-> providerUserId 가지고 옴
    }
}