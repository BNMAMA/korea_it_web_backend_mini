package com.korit.BoardStudy.dto.auth;

import com.korit.BoardStudy.entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SignupReqDto {
    private String username;
    private String password;
    private String email;

    public User toEntity(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return User.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .email(email)
                .build();
    }

}
