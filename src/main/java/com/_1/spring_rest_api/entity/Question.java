package com._1.spring_rest_api.entity;

import com._1.spring_rest_api.api.dto.QuestionResponse;
import com._1.spring_rest_api.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "QUESTION")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Question extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "week_id")
    private Week week;

    @Column(name = "front", columnDefinition = "TEXT")
    private String front;

    @Column(name = "back", columnDefinition = "TEXT")
    private String back;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Builder.Default
    private List<QuizQuestionMapping> quizQuestionMappings = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserAnswer> userAnswers = new ArrayList<>();

    // Week와 Question 간의 양방향 연관관계 메서드
    public void changeWeek(Week week) {
        this.week = week;
        if (week != null && !week.getQuestions().contains(this)) {
            week.getQuestions().add(this);
        }
    }

    // Question과 QuizQuestionMapping 간의 양방향 연관관계 메서드
    public void addQuizQuestionMapping(QuizQuestionMapping mapping) {
        this.quizQuestionMappings.add(mapping);
        if (mapping.getQuestion() != this) {
            mapping.changeQuestion(this);
        }
    }

    public void removeQuizQuestionMapping(QuizQuestionMapping mapping) {
        this.quizQuestionMappings.remove(mapping);
        if (mapping.getQuestion() == this) {
            mapping.changeQuestion(null);
        }
    }

    // Question과 UserAnswer 간의 양방향 연관관계 메서드
    public void addUserAnswer(UserAnswer userAnswer) {
        this.userAnswers.add(userAnswer);
        if (userAnswer.getQuestion() != this) {
            userAnswer.changeQuestion(this);
        }
    }

    public void removeUserAnswer(UserAnswer userAnswer) {
        this.userAnswers.remove(userAnswer);
        if (userAnswer.getQuestion() == this) {
            userAnswer.changeQuestion(null);
        }
    }

    // DTO로 변환하는 메서드
    public QuestionResponse toQuestionResponse() {
        return QuestionResponse.builder()
                .id(this.id)
                .weekId(this.week != null ? this.week.getId() : null)
                .front(this.front) // 필드명 변경
                .back(this.back) // 필드명 변경
                .build();
    }
}