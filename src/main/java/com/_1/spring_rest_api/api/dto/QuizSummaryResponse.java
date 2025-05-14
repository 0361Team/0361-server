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
public class QuizSummaryResponse {
    private Long id;
    private String title;
    private String description;
    private String quizType;
    private Integer totalQuestions;
    private Integer weekCount;
    private LocalDateTime createdAt;
}