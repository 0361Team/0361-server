package com._1.spring_rest_api.entity;

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
@Table(name = "CUSTOM_QUIZ")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CustomQuiz extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User creator;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "quiz_type")
    private String quizType;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    @Builder.Default
    private List<QuizQuestionMapping> quizQuestionMappings = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    @Builder.Default
    private List<QuizSession> quizSessions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    @Builder.Default
    private List<QuizWeekMapping> quizWeekMappings = new ArrayList<>();

    // CustomQuiz와 QuizSession 간의 양방향 연관관계 메서드
    public void addQuizSession(QuizSession session) {
        this.quizSessions.add(session);
        if (session.getQuiz() != this) {
            session.changeQuiz(this);
        }
    }

    public void removeQuizSession(QuizSession session) {
        this.quizSessions.remove(session);
        if (session.getQuiz() == this) {
            session.changeQuiz(null);
        }
    }

    // CustomQuiz와 QuizQuestionMapping 간의 양방향 연관관계 메서드
    public void addQuizQuestionMapping(QuizQuestionMapping mapping) {
        this.quizQuestionMappings.add(mapping);
        if (mapping.getQuiz() != this) {
            mapping.changeQuiz(this);
        }
    }

    public void removeQuizQuestionMapping(QuizQuestionMapping mapping) {
        this.quizQuestionMappings.remove(mapping);
        if (mapping.getQuiz() == this) {
            mapping.changeQuiz(null);
        }
    }

    // CustomQuiz와 QuizWeekMapping 간의 양방향 연관관계 메서드
    public void addQuizWeekMapping(QuizWeekMapping mapping) {
        this.quizWeekMappings.add(mapping);
        if (mapping.getQuiz() != this) {
            mapping.changeQuiz(this);
        }
    }

    public void removeQuizWeekMapping(QuizWeekMapping mapping) {
        this.quizWeekMappings.remove(mapping);
        if (mapping.getQuiz() == this) {
            mapping.changeQuiz(null);
        }
    }

}