package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.QuizSessionDetailResponse;
import com._1.spring_rest_api.api.dto.QuizSessionResponse;

import java.util.List;

public interface QuizSessionQueryService {

    List<QuizSessionResponse> getUserQuizSessions(Long userId);

    QuizSessionDetailResponse getSessionDetail(Long sessionId);
}
