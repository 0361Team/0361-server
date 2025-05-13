package com._1.spring_rest_api.service;

import java.util.List;

public interface QuestionCommandService {

    List<Long> generateAndSaveQuestions(Long weekId, int minQuestionCount);
    void deleteQuestion(Long questionId);
}
