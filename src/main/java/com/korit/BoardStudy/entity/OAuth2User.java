package com.korit.BoardStudy.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
@Data
@Builder
public class OAuth2User {
    private Integer oAuth2UserId;
    private Integer userid;
    private String provider;
    private String providerUserid;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;

}