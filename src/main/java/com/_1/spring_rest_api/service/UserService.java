package com._1.spring_rest_api.service;

import com._1.spring_rest_api.entity.User;
import com._1.spring_rest_api.entity.UserKakao;
import com._1.spring_rest_api.repository.UserKakaoRepository;
import com._1.spring_rest_api.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserKakaoRepository userKakaoRepository;

    public UserService(UserRepository userRepository, UserKakaoRepository userKakaoRepository) {
        this.userRepository = userRepository;
        this.userKakaoRepository = userKakaoRepository;
    }

    public User findByKakaoId(String kakaoId) {
        Optional<UserKakao> userKakaoOpt = userKakaoRepository.findByKakaoAccountId(kakaoId);
        return userKakaoOpt.map(UserKakao::getUser).orElse(null);
    }

    @Transactional
    public User createKakaoUser(String email, String name, String kakaoId) {
        // Create new user
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setIsActive(true);

        // Save user to get ID
        User savedUser = userRepository.save(user);

        // Create Kakao account link
        UserKakao userKakao = new UserKakao();
        userKakao.setUser(savedUser);
        userKakao.setKakaoAccountId(kakaoId);

        userKakaoRepository.save(userKakao);

        return savedUser;
    }

    @Transactional
    public void updateKakaoTokens(User user, String accessToken, String refreshToken, LocalDateTime expiresAt) {
        UserKakao userKakao = user.getUserKakao();

        userKakao.setAccessToken(accessToken);
        userKakao.setRefreshToken(refreshToken);
        userKakao.setTokenExpiresAt(expiresAt);

        userKakaoRepository.save(userKakao);
    }

    public UserDetails createUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                "", // No password for OAuth2 users
                user.getIsActive(),
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}