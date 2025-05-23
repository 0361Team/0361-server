package com._1.spring_rest_api.entity;


import com._1.spring_rest_api.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "QUIZ_SESSION")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class QuizSession extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private CustomQuiz quiz;

    private Integer currentQuestionIndex;

    private Boolean completed;

    private LocalDateTime completedAt;

    public static QuizSession create(User user, CustomQuiz quiz) {
        if (user == null) {
            throw new IllegalArgumentException("사용자는 null이 될 수 없습니다");
        }
        if (quiz == null) {
            throw new IllegalArgumentException("퀴즈는 null이 될 수 없습니다");
        }

        return QuizSession.builder()
                .user(user)
                .quiz(quiz)
                .currentQuestionIndex(0)
                .completed(false)
                .build();
    }

    public void moveToNextQuestion() {
        this.currentQuestionIndex++;
    }

    public void recordAnswer() {
        // 점수 관리 없이 단순히 다음 문제로 이동
        moveToNextQuestion();
    }

    public void completeSession() {
        this.completed = true;
        this.completedAt = LocalDateTime.now();
    }

    // QuizSession 클래스에 추가
    public Question getCurrentQuestion() {
        if (this.currentQuestionIndex >= this.quiz.getQuizQuestionMappings().size()) {
            return null;
        }
        return this.quiz.getQuizQuestionMappings().get(this.currentQuestionIndex).getQuestion();
    }

    public Question getNextQuestion() {
        int nextIndex = this.currentQuestionIndex + 1;
        if (nextIndex < this.quiz.getQuizQuestionMappings().size()) {
            return this.quiz.getQuizQuestionMappings().get(nextIndex).getQuestion();
        }
        return null;
    }

    public boolean isComplete() {
        return this.currentQuestionIndex >= this.quiz.getQuizQuestionMappings().size();
    }

    public UserAnswer createAnswer(String userAnswerText) {
        Question currentQuestion = getCurrentQuestion();
        if (currentQuestion == null) {
            throw new IllegalStateException("세션에 현재 질문이 없습니다");
        }

        boolean isCorrect = currentQuestion.isCorrectAnswer(userAnswerText);

        return UserAnswer.builder()
                .user(this.user)
                .question(currentQuestion)
                .userAnswer(userAnswerText)
                .isCorrect(isCorrect)
                .attemptCount(1)
                .answeredAt(LocalDateTime.now())
                .build();
    }

    // User와 QuizSession 간의 양방향 연관관계 메서드
    public void changeUser(User user) {
        this.user = user;
        if (user != null && !user.getQuizSessions().contains(this)) {
            user.getQuizSessions().add(this);
        }
    }

    // CustomQuiz와 QuizSession 간의 양방향 연관관계 메서드
    public void changeQuiz(CustomQuiz quiz) {
        this.quiz = quiz;
        if (quiz != null && !quiz.getQuizSessions().contains(this)) {
            quiz.getQuizSessions().add(this);
        }
    }
}