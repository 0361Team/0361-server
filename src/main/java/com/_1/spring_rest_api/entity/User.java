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

    // User와 UserKakao 간의 양방향 연관관계 메서드
    public void linkWithKakao(UserKakao userKakao) {
        this.userKakao = userKakao;
        // userKakao의 user 필드가 this가 아닌 경우에만 설정
        if (userKakao.getUser() != this) {
            userKakao.linkWithUser(this);
        }
    }

    // User와 UserAgree 간의 양방향 연관관계 메서드
    public void addUserAgree(UserAgree userAgree) {
        this.userAgrees.add(userAgree);
        if (userAgree.getUser() != this) {
            userAgree.changeUser(this);
        }
    }

    public void removeUserAgree(UserAgree userAgree) {
        this.userAgrees.remove(userAgree);
        if (userAgree.getUser() == this) {
            userAgree.changeUser(null);
        }
    }

    // User와 Course 간의 양방향 연관관계 메서드
    public void addCourse(Course course) {
        this.courses.add(course);
        if (course.getCreator() != this) {
            course.changeCreator(this);
        }
    }

    public void removeCourse(Course course) {
        this.courses.remove(course);
        if (course.getCreator() == this) {
            course.changeCreator(null);
        }
    }

    // 7. User와 QuizSession 간의 양방향 연관관계 메서드
    public void addQuizSession(QuizSession session) {
        this.quizSessions.add(session);
        if (session.getUser() != this) {
            session.changeUser(this);
        }
    }

    public void removeQuizSession(QuizSession session) {
        this.quizSessions.remove(session);
        if (session.getUser() == this) {
            session.changeUser(null);
        }
    }

    // User와 UserAnswer 간의 양방향 연관관계 메서드
    public void addUserAnswer(UserAnswer userAnswer) {
        this.userAnswers.add(userAnswer);
        if (userAnswer.getUser() != this) {
            userAnswer.changeUser(this);
        }
    }

    public void removeUserAnswer(UserAnswer userAnswer) {
        this.userAnswers.remove(userAnswer);
        if (userAnswer.getUser() == this) {
            userAnswer.changeUser(null);
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