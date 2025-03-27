package com._1.spring_rest_api.entity;

import com._1.spring_rest_api.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "UserTB")
@Getter
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
    private List<UserAgree> userAgrees = new ArrayList<>();

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<QuizSession> quizSessions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserAnswer> userAnswers = new ArrayList<>();
}