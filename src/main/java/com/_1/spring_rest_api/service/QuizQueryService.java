package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.QuizDetailResponse;
import com._1.spring_rest_api.api.dto.QuizSummaryResponse;

import java.util.List;

public interface QuizQueryService {

    List<QuizSummaryResponse> getQuizzesByUserId(Long userId);

    QuizDetailResponse getQuizDetail(Long quizId);
}
