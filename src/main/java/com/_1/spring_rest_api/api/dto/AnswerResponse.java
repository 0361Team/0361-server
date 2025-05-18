package com._1.spring_rest_api.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponse {

    private Boolean isCorrect;
    private String correctAnswer;
    private Boolean isComplete;
    private Integer nextQuestionIndex;
    private QuestionResponse nextQuestion;
}