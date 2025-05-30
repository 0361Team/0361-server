package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.KakaoAuthRequest;
import com._1.spring_rest_api.api.dto.KakaoAuthResponse;
import com._1.spring_rest_api.api.dto.KakaoAuthResponse.KakaoUserInfo;
import com._1.spring_rest_api.entity.User;
import com._1.spring_rest_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class KakaoAuthService {

    private final KakaoApiClientService kakaoApiClientService;
    private final UserService userService;
    private final JwtService jwtService;

    public KakaoAuthResponse authenticateWithKakao(KakaoAuthRequest request) {
        try {
            // 1. 카카오 액세스 토큰으로 사용자 정보 조회
            KakaoUserInfo kakaoUserInfo =
                    kakaoApiClientService.getUserInfo(request.getAccessToken());

            // 2. 기존 사용자 조회 또는 신규 사용자 생성
            User user = findOrCreateUser(kakaoUserInfo);

            // 3. 카카오 토큰 정보 업데이트
            updateKakaoTokens(user, request);

            // 4. JWT 토큰 생성
            UserDetails userDetails = userService.createUserDetails(user);
            String jwtToken = jwtService.generateToken(userDetails);

            // 5. 응답 생성
            return buildAuthResponse(jwtToken, user);

        } catch (Exception e) {
            log.error("카카오 인증 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("카카오 인증 처리 실패: " + e.getMessage());
        }
    }

    /**
     * 카카오 사용자 정보로 기존 사용자를 찾거나 신규 사용자를 생성합니다.
     */
    private User findOrCreateUser(KakaoUserInfo kakaoUserInfo) {
        User existingUser = userService.findByKakaoId(kakaoUserInfo.getKakaoId());

        if (existingUser != null) {
            log.info("기존 사용자 로그인: kakaoId={}, email={}",
                    kakaoUserInfo.getKakaoId(), existingUser.getEmail());
            return existingUser;
        } else {
            log.info("신규 사용자 회원가입: kakaoId={}, email={}",
                    kakaoUserInfo.getKakaoId(), kakaoUserInfo.getEmail());
            return userService.createKakaoUser(
                    kakaoUserInfo.getEmail(),
                    kakaoUserInfo.getNickname(),
                    kakaoUserInfo.getKakaoId()
            );
        }
    }

    /**
     * 카카오 토큰 정보를 업데이트합니다.
     */
    private void updateKakaoTokens(User user, KakaoAuthRequest request) {
        LocalDateTime expiresAt = request.getExpiresIn() != null
                ? LocalDateTime.now().plusSeconds(request.getExpiresIn())
                : LocalDateTime.now().plusHours(24); // 기본 1시간

        userService.updateKakaoTokens(
                user,
                request.getAccessToken(),
                request.getRefreshToken(),
                expiresAt
        );
    }

    /**
     * 인증 응답을 생성합니다.
     */
    private KakaoAuthResponse buildAuthResponse(String jwtToken, User user) {
        return KakaoAuthResponse.builder()
                .accessToken(jwtToken)
                .tokenType("Bearer")
                .expiresIn(24 * 60 * 60L) // 24시간 (초 단위)
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}