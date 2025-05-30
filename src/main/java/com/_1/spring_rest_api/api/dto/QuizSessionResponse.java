package com._1.spring_rest_api.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSessionResponse {

    private Long id;
    private Long quizId;
    private String quizTitle;
    private Integer currentQuestionIndex;
    private Boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}