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
public class QuizDetailResponse {
    private Long id;
    private String title;
    private String description;
    private String quizType;
    private Integer totalQuestions;
    private UserSummaryResponse creator;
    private List<WeekSummaryResponse> weeks;
    private List<QuestionResponse> questions;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}