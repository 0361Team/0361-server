package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.FlashcardsRequest;

import java.util.List;

public interface QuestionCommandService {

    List<Long> saveFlashcards(Long weekId, FlashcardsRequest flashcardsRequest);

    void deleteQuestion(Long questionId);
}
