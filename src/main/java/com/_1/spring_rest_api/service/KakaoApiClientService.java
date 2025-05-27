package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.KakaoAuthResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoApiClientService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    public KakaoAuthResponse.KakaoUserInfo getUserInfo(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    KAKAO_USER_INFO_URL,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return parseKakaoUserInfo(response.getBody());
            } else {
                throw new RuntimeException("카카오 사용자 정보 조회 실패: HTTP " + response.getStatusCode());
            }

        } catch (HttpClientErrorException e) {
            log.error("카카오 API 호출 실패: {}", e.getResponseBodyAsString());
            throw new RuntimeException("유효하지 않은 카카오 액세스 토큰입니다.", e);
        } catch (Exception e) {
            log.error("카카오 사용자 정보 조회 중 오류 발생", e);
            throw new RuntimeException("카카오 사용자 정보 조회 실패", e);
        }
    }

    private KakaoAuthResponse.KakaoUserInfo parseKakaoUserInfo(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);

            String kakaoId = rootNode.path("id").asText();
            JsonNode kakaoAccount = rootNode.path("kakao_account");
            String email = kakaoAccount.path("email").asText();

            JsonNode profile = kakaoAccount.path("profile");
            String nickname = profile.path("nickname").asText();

            return KakaoAuthResponse.KakaoUserInfo.builder()
                    .kakaoId(kakaoId)
                    .email(email)
                    .nickname(nickname)
                    .build();

        } catch (Exception e) {
            log.error("카카오 사용자 정보 파싱 실패", e);
            throw new RuntimeException("카카오 사용자 정보 파싱 실패", e);
        }
    }
}