package com._1.spring_rest_api.security;

import com._1.spring_rest_api.entity.User;
import com._1.spring_rest_api.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String provider = oauthToken.getAuthorizedClientRegistrationId();

        if ("kakao".equals(provider)) {
            processKakaoUser(attributes, response);
        }
    }

    private void processKakaoUser(Map<String, Object> attributes, HttpServletResponse response) throws IOException {
        String kakaoId = attributes.get("id").toString();

        // Kakao account info is nested in 'kakao_account'
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = (String) kakaoAccount.get("email");

        // Profile info is nested in 'profile'
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String nickname = (String) profile.get("nickname");

        // Find or create the user
        User user = userService.findByKakaoId(kakaoId);

        if (user == null) {
            user = userService.createKakaoUser(email, nickname, kakaoId);
        }

        // Update tokens
        String accessToken = (String) attributes.get("access_token");
        String refreshToken = (String) attributes.get("refresh_token");
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds((Integer) attributes.get("expires_in"));

        userService.updateKakaoTokens(user, accessToken, refreshToken, expiresAt);

        // Generate JWT and send to client
        String jwt = jwtService.generateToken(userService.createUserDetails(user));

        response.sendRedirect("myapp://auth?token=" + jwt);
    }
}