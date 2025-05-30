package com._1.spring_rest_api.converter;

import com._1.spring_rest_api.api.dto.UserAnswerResponse;
import com._1.spring_rest_api.entity.UserAnswer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAnswerConverter implements Converter<UserAnswer, UserAnswerResponse> {

    @Override
    public UserAnswerResponse toDto(UserAnswer entity) {
        return UserAnswerResponse.builder()
                .id(entity.getId())
                .questionId(entity.getQuestion().getId())
                .questionFront(entity.getQuestion().getFront())
                .userAnswer(entity.getUserAnswer())
                .correctAnswer(entity.getQuestion().getBack())
                .isCorrect(entity.getIsCorrect())
                .answeredAt(entity.getAnsweredAt())
                .build();
    }

    @Override
    public UserAnswer toEntity(UserAnswerResponse dto) {
        return null;
    }
}