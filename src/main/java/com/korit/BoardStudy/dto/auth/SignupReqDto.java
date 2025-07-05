package com.korit.BoardStudy.dto.auth;

import com.korit.BoardStudy.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
@AllArgsConstructor
public class SignupReqDto {
    private String username;
    private String password;
    private String email;

    public User toEntity(BCryptPasswordEncoder bCryptPasswordEncoder) {
        //DTO객체를 -> entity객체로 변환
        return User.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .email(email)
                .build();
        //signupReqDto로 값을 받고 -> user객체로 변환 -> Bcryp로 암호화 -> User객체에 저장

    }
}
