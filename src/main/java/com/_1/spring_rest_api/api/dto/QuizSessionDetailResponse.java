package com._1.spring_rest_api.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSessionDetailResponse {
    private Long id;
    private Long quizId;
    private String quizTitle;
    private String quizDescription;
    private Integer totalQuestions;
    private Integer currentQuestionIndex;
    private QuestionResponse currentQuestion;
    private Boolean completed;
    private Integer score;
    private Integer totalQuestionsAnswered;
    private Integer totalCorrectAnswers;
    private List<UserAnswerResponse> userAnswers;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}