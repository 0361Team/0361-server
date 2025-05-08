package com._1.spring_rest_api.entity;

import com._1.spring_rest_api.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "UserTB")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String name;

    private Boolean isActive;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserKakao userKakao;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserAgree> userAgrees = new ArrayList<>();

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<QuizSession> quizSessions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserAnswer> userAnswers = new ArrayList<>();

    // 양방향 연관관계 설정 메서드
    public void linkWithKakao(UserKakao userKakao) {
        this.userKakao = userKakao;
        // userKakao의 user 필드가 this가 아닌 경우에만 설정
        if (userKakao.getUser() != this) {
            userKakao.linkWithUser(this);
        }
    }


    public User(Long id) {
        this.id = id;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateUserKakao(UserKakao userKakao) {
        this.userKakao = userKakao;
    }

    public static User createKakaoUser(String email, String name) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("이메일은 필수 값입니다.");
        }

        return User.builder()
                .email(email)
                .name(name)
                .isActive(true)
                .build();
    }
}