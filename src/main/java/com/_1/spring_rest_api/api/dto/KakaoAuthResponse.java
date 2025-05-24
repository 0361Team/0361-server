package com._1.spring_rest_api.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class KakaoAuthResponse {

    private String accessToken; // 우리 서버에서 발급한 JWT 토큰
    private String tokenType;   // "Bearer"
    private Long expiresIn;     // JWT 토큰 만료 시간(초)
    private Long userId;        // 사용자 ID
    private String email;       // 사용자 이메일
    private String name;        // 사용자 이름
}