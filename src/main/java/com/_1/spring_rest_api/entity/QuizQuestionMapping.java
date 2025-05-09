package com._1.spring_rest_api.entity;

import com._1.spring_rest_api.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "QUIZ_QUESTION_MAPPING")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class QuizQuestionMapping extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private CustomQuiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    // CustomQuiz와 QuizQuestionMapping 간의 양방향 연관관계 메서드
    public void changeQuiz(CustomQuiz quiz) {
        this.quiz = quiz;
        if (quiz != null && !quiz.getQuizQuestionMappings().contains(this)) {
            quiz.getQuizQuestionMappings().add(this);
        }
    }

    // Question과 QuizQuestionMapping 간의 양방향 연관관계 메서드
    public void changeQuestion(Question question) {
        this.question = question;
        if (question != null && !question.getQuizQuestionMappings().contains(this)) {
            question.getQuizQuestionMappings().add(this);
        }
    }
}