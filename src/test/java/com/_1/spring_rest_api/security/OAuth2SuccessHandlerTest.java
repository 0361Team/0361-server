package com._1.spring_rest_api.security;

import com._1.spring_rest_api.entity.User;
import com._1.spring_rest_api.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2SuccessHandlerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    private final String KAKAO_ID = "12345";
    private final String EMAIL = "test@example.com";
    private final String NAME = "Test User";
    private final String ACCESS_TOKEN = "kakao_access_token";
    private final String REFRESH_TOKEN = "kakao_refresh_token";
    private final Integer EXPIRES_IN = 3600;

    @BeforeEach
    void setUp() {
        // 공통 준비 작업
    }

    @Test
    void onAuthenticationSuccess_shouldProcessKakaoUser_whenExistingUser() throws IOException, ServletException {
        // Given
        User existingUser = mockExistingUser();
        mockKakaoAuthentication();

        when(userService.findByKakaoId(KAKAO_ID)).thenReturn(existingUser);

        UserDetails userDetails = mock(UserDetails.class);
        when(userService.createUserDetails(existingUser)).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt_token");

        // When
        oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // Then
        verify(userService).updateKakaoTokens(
                eq(existingUser),
                eq(ACCESS_TOKEN),
                eq(REFRESH_TOKEN),
                any(LocalDateTime.class)
        );
        verify(response).sendRedirect(contains("myapp://auth?token="));
    }

    @Test
    void onAuthenticationSuccess_shouldProcessKakaoUser_whenNewUser() throws IOException, ServletException {
        // Given
        mockKakaoAuthentication();

        when(userService.findByKakaoId(KAKAO_ID)).thenReturn(null);

        User newUser = new User();
        newUser.setId(1L);
        newUser.setEmail(EMAIL);
        when(userService.createKakaoUser(eq(EMAIL), eq(NAME), eq(KAKAO_ID))).thenReturn(newUser);

        UserDetails userDetails = mock(UserDetails.class);
        when(userService.createUserDetails(newUser)).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt_token");

        // When
        oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // Then
        verify(userService).createKakaoUser(EMAIL, NAME, KAKAO_ID);
        verify(userService).updateKakaoTokens(
                eq(newUser),
                eq(ACCESS_TOKEN),
                eq(REFRESH_TOKEN),
                any(LocalDateTime.class)
        );
        verify(response).sendRedirect(contains("myapp://auth?token="));
    }

    @Test
    void onAuthenticationSuccess_shouldIgnore_whenNotKakaoProvider() throws IOException, ServletException {
        // Given
        OAuth2AuthenticationToken mockToken = mock(OAuth2AuthenticationToken.class);
        when(mockToken.getAuthorizedClientRegistrationId()).thenReturn("google");

        // When
        oAuth2SuccessHandler.onAuthenticationSuccess(request, response, mockToken);

        // Then
        verify(userService, never()).findByKakaoId(anyString());
        verify(userService, never()).createKakaoUser(anyString(), anyString(), anyString());
        verify(userService, never()).updateKakaoTokens(any(), anyString(), anyString(), any());
    }

    // 헬퍼 메서드
    private User mockExistingUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail(EMAIL);
        user.setName(NAME);
        user.setIsActive(true);
        return user;
    }

    private void mockKakaoAuthentication() {
        // Kakao attributes 생성
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", KAKAO_ID);
        attributes.put("access_token", ACCESS_TOKEN);
        attributes.put("refresh_token", REFRESH_TOKEN);
        attributes.put("expires_in", EXPIRES_IN);

        // Kakao account 정보
        Map<String, Object> kakaoAccount = new HashMap<>();
        kakaoAccount.put("email", EMAIL);

        // Profile 정보
        Map<String, Object> profile = new HashMap<>();
        profile.put("nickname", NAME);

        kakaoAccount.put("profile", profile);
        attributes.put("kakao_account", kakaoAccount);

        // OAuth2User 생성
        OAuth2User oAuth2User = new DefaultOAuth2User(
                java.util.Collections.emptyList(),
                attributes,
                "id"
        );

        // OAuth2AuthenticationToken 생성
        OAuth2AuthenticationToken authToken = mock(OAuth2AuthenticationToken.class);
        when(authToken.getPrincipal()).thenReturn(oAuth2User);
        when(authToken.getAuthorizedClientRegistrationId()).thenReturn("kakao");

        this.authentication = authToken;
    }
}