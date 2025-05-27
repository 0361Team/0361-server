package com._1.spring_rest_api.api.controller;

import com._1.spring_rest_api.api.dto.KakaoAuthRequest;
import com._1.spring_rest_api.api.dto.KakaoAuthResponse;
import com._1.spring_rest_api.service.KakaoAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "사용자 인증 관련 API")
public class AuthController {

    private final KakaoAuthService kakaoAuthService;

    @PostMapping("/kakao/token")
    @Operation(
            summary = "카카오 토큰 인증 (iOS/모바일용)",
            description = "iOS SDK에서 받은 카카오 액세스 토큰을 검증하고 JWT 토큰을 발급합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 성공 - JWT 토큰 발급됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 - 필수 필드 누락"),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 카카오 토큰"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @SecurityRequirements
    public ResponseEntity<KakaoAuthResponse> authenticateWithKakaoToken(
            @Valid @RequestBody KakaoAuthRequest request) {

        KakaoAuthResponse response = kakaoAuthService.authenticateWithKakao(request);
        return ResponseEntity.ok(response);
    }

}