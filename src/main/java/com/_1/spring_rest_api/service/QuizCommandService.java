package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.CreateQuizRequest;

public interface QuizCommandService {

    Long createQuiz(CreateQuizRequest request);

    void deleteQuiz(Long quizId);

    Long startQuizSession(Long quizId, Long userId);
}
