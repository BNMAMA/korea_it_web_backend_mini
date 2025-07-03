package com.korit.BoardStudy.config;

import com.korit.BoardStudy.security.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean //등록 파일로 인식
    public BCryptPasswordEncoder bCryptPasswordEncoder() { //비밀번호를 암호화 저장, 입력 비번과 DB저장 비번을 비교
        return new BCryptPasswordEncoder(); //싱글턴?
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration(); //다른 도메인에서 서버에 요청을 보낼 수 있도록 함
        corsConfiguration.addAllowedOriginPattern(CorsConfiguration.ALL); //모든 도메인 요청 허용
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL); //GET,POST,PUT,DELETE 모든 메소드 허용
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL); //모든 헤더(요청의 추가 정보(주소,이름)) 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); //어떤 경로에 CORS규칙을 적용할지
        source.registerCorsConfiguration("/**", corsConfiguration);
        //모든 경로 허용 //규칙 적용
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)//어떤걸 보안에 거칠지 설정(보안필터)
            throws Exception { //예외 생길 수 있음
        http.cors(Customizer.withDefaults()); //위에서 설정한 값 디폴트로 설정
        http.csrf(csrf -> csrf.disable()); //보안 장치 끄기(인용방지)
        http.formLogin(formLogin -> formLogin.disable()); //로그인 창 자동기능 끄기
        http.httpBasic(httpBasic -> httpBasic.disable()); //브라우저의 팝업 끄기
        http.logout(logout -> logout.disable()); //로그아웃 기능 끄기

        http.sessionManagement(Session -> Session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //서버의 기억 저장장치임
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.authorizeHttpRequests(auth -> {
            //모든 보안 요청
            auth.requestMatchers("/auth/**").permitAll();
            //전부 허가
            auth.anyRequest().authenticated();
            //모든 요청은 로그인 인증
        });
        return http.build();
    }
}