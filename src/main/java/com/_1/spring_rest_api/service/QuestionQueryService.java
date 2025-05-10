package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.QuestionResponse;

import java.util.List;

public interface QuestionQueryService {

    List<QuestionResponse> getAllQuestionsByWeekId(Long weekId);

    QuestionResponse getQuestionById(Long questionId);

}
