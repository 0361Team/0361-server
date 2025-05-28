package com._1.spring_rest_api.service;

import com._1.spring_rest_api.entity.User;
import com._1.spring_rest_api.entity.UserKakao;
import com._1.spring_rest_api.repository.UserKakaoRepository;
import com._1.spring_rest_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserKakaoRepository userKakaoRepository;

    public User findByKakaoId(String kakaoId) {
        Optional<UserKakao> userKakaoOpt = userKakaoRepository.findByKakaoAccountId(kakaoId);
        return userKakaoOpt.map(UserKakao::getUser).orElse(null);
    }

    public User createKakaoUser(String email, String name, String kakaoId) {
        User user = User.createKakaoUser(email, name);
        User savedUser = userRepository.save(user);

        // Create Kakao account link
        UserKakao userKakao = UserKakao.createKakaoAccountLink(savedUser, kakaoId);

        userKakaoRepository.save(userKakao);

        return savedUser;
    }

    public void updateKakaoTokens(User user, String accessToken, String refreshToken, LocalDateTime expiresAt) {
        UserKakao userKakao = user.getUserKakao();

        userKakao.updateTokens(accessToken, refreshToken, expiresAt);

        userKakaoRepository.save(userKakao);
    }

    public UserDetails createUserDetails(User user) {
        return createSecurityPrincipal(user);
    }

    /**
     * 사용자 엔티티를 Spring Security 인증 객체로 변환한다.
     */
    private UserDetails createSecurityPrincipal(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                "", // OAuth2 사용자는 비밀번호 없음
                user.getIsActive(),
                true,
                true,
                true,
                getUserAuthorities()
        );
    }

    /**
     * 기본 사용자 권한을 반환한다.
     */
    private Collection<? extends GrantedAuthority> getUserAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }
}