package com._1.spring_rest_api.entity;


import com._1.spring_rest_api.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "USER_ANSWER")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserAnswer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_session_id")
    private QuizSession quizSession; // 추가

    @Column(name = "user_answer", columnDefinition = "TEXT")
    private String userAnswer;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "attempt_count")
    private Integer attemptCount;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    // User와 UserAnswer 간의 양방향 연관관계 메서드
    public void changeUser(User user) {
        this.user = user;
        if (user != null && !user.getUserAnswers().contains(this)) {
            user.getUserAnswers().add(this);
        }
    }

    // Question과 UserAnswer 간의 양방향 연관관계 메서드
    public void changeQuestion(Question question) {
        this.question = question;
        if (question != null && !question.getUserAnswers().contains(this)) {
            question.getUserAnswers().add(this);
        }
    }

    public void changeQuizSession(QuizSession quizSession) {
        this.quizSession = quizSession;
        if (quizSession != null && !quizSession.getUserAnswers().contains(this)) {
            quizSession.getUserAnswers().add(this);
        }
    }
}