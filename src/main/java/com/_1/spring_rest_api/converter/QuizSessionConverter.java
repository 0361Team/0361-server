package com._1.spring_rest_api.converter;

import com._1.spring_rest_api.api.dto.QuizSessionResponse;
import com._1.spring_rest_api.entity.QuizSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuizSessionConverter implements Converter<QuizSession, QuizSessionResponse> {
    @Override
    public QuizSessionResponse toDto(QuizSession entity) {
        return QuizSessionResponse.builder()
                .id(entity.getId())
                .quizId(entity.getQuiz().getId())
                .quizTitle(entity.getQuiz().getTitle())
                .currentQuestionIndex(entity.getCurrentQuestionIndex())
                .completed(entity.getCompleted())
                .createdAt(entity.getCreateAt())
                .completedAt(entity.getCompletedAt())
                .build();
    }

    @Override
    public QuizSession toEntity(QuizSessionResponse dto) {
        return null;
    }
}