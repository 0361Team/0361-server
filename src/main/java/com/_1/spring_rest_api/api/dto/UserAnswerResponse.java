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
public class UserAnswerResponse {

    private Long id;
    private Long questionId;
    private String questionFront;
    private String userAnswer;
    private String correctAnswer;
    private Boolean isCorrect;
    private LocalDateTime answeredAt;
}