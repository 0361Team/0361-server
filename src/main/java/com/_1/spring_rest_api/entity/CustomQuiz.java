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

    // 총 질문 수 업데이트 메서드
    public void updateTotalQuestions(int count) {
        this.totalQuestions = count;
    }

    public static CustomQuiz create(User user, String title, String description, String quizType) {
        if (user == null) {
            throw new IllegalArgumentException("생성자(사용자)는 null이 될 수 없습니다");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("퀴즈 제목은 빈 값이 될 수 없습니다");
        }

        return CustomQuiz.builder()
                .creator(user)
                .title(title)
                .description(description)
                .quizType(quizType)
                .totalQuestions(0)
                .build();
    }

    /**
     * 질문을 퀴즈에 추가
     */
    public void addQuestion(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("질문은 null이 될 수 없습니다.");
        }

        // 이미 추가된 질문인지 확인
        boolean alreadyAdded = this.quizQuestionMappings.stream()
                .anyMatch(mapping -> mapping.getQuestion().getId().equals(question.getId()));

        if (alreadyAdded) {
            return; // 이미 추가된 질문이면 무시
        }

        // 정적 팩토리 메서드를 통해 매핑 생성 및 양방향 연관관계 설정
        QuizQuestionMapping.create(this, question);

        // 질문 수 증가
        this.updateTotalQuestions(this.totalQuestions + 1);
    }

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