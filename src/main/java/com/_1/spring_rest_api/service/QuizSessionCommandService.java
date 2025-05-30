package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.AnswerRequest;
import com._1.spring_rest_api.api.dto.AnswerResponse;

public interface QuizSessionCommandService {

    AnswerResponse answerQuestion(Long sessionId, AnswerRequest request);

    void completeSession(Long sessionId);
}
