package com.korit.BoardStudy.security.handler;

import com.korit.BoardStudy.entity.OAuth2User;
import com.korit.BoardStudy.entity.User;
import com.korit.BoardStudy.repository.OAuth2UserRepository;
import com.korit.BoardStudy.repository.UserRepository;
import com.korit.BoardStudy.security.jwt.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component //자동으로 빈 등록
//로그인 성공(인증객체) 후 어떻게? 토큰 발급/ 연동/ 로그인
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler  { //로그인 후 후처리 할 수 있는 인터페이스

    @Autowired
    private OAuth2UserRepository oAuth2UserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String provider = defaultOAuth2User.getAttribute("provider");
        String providerUserId = defaultOAuth2User.getAttribute("id");
        String email = defaultOAuth2User.getAttribute("email");

        Optional<OAuth2User> optionalOAuth2User = oAuth2UserRepository.getOAuth2UserByProviderAndProviderUserId(provider, providerUserId);

        if (optionalOAuth2User.isEmpty()) { //로그인한 사람이 계정이 없거나 연동이 안됨
            response.sendRedirect("http://localhost:3000/auth/oauth2?provider=" + provider + "&providerUserId=" + providerUserId + "&email=" + email);
            return; //Redirect 이후 중복 이후 실행이 발생하지 않게 하기 위해
        }

        OAuth2User oAuth2User = optionalOAuth2User.get(); //연동이 된 경우

        Optional<User> optionalUser = userRepository.getUserByUserId(oAuth2User.getUserId());
        //연동이 된 경우라면 accessToken을 발급해줘야 함

        String accessToken = null;
        if (optionalUser.isPresent()) {
            accessToken = jwtUtils.generateAccessToken(optionalUser.get().getUserId().toString());
                                                    //Optional객체에서 -> User객체 -> User객체에서 userId -> 문자열
        }

        response.sendRedirect("http://localhost:3000/auth/oauth2/signin?accessToken=" + accessToken);
    }
}