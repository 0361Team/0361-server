package com._1.spring_rest_api.entity;


import com._1.spring_rest_api.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "USER_KAKAO")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserKakao extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "kakao_account_id")
    private String kakaoAccountId;  // 카카오에서 제공하는 사용자 고유 ID

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "token_expires_at")
    private LocalDateTime tokenExpiresAt;

    // 필요에 따라 추가 정보 (프로필 이미지 URL, 닉네임 등)
    /**
     * 카카오 토큰 정보를 업데이트한다.
     **/
    public void updateTokens(String accessToken, String refreshToken, LocalDateTime expiresAt) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenExpiresAt = expiresAt;
    }

    public static UserKakao createKakaoAccountLink(User user, String kakaoId) {
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보는 필수입니다.");
        }
        if (kakaoId == null || kakaoId.isEmpty()) {
            throw new IllegalArgumentException("카카오 계정 ID는 필수 값입니다.");
        }

        UserKakao userKakao = UserKakao.builder()
                .user(user)
                .kakaoAccountId(kakaoId)
                .build();

        // 양방향 연관관계 설정
        user.linkWithKakao(userKakao);

        return userKakao;
    }
}