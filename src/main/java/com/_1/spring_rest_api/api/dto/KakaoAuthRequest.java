package com._1.spring_rest_api.api.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoAuthRequest {

    @NotEmpty(message = "카카오 액세스 토큰은 필수입니다")
    private String accessToken;

    private String refreshToken;

    @NotEmpty(message = "카카오 사용자 ID는 필수입니다")
    private String kakaoId;

    private Long expiresIn; // 토큰 만료 시간(초)
}