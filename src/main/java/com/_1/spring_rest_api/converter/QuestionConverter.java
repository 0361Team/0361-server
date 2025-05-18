package com._1.spring_rest_api.converter;

import com._1.spring_rest_api.api.dto.QuestionResponse;
import com._1.spring_rest_api.entity.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuestionConverter implements Converter<Question, QuestionResponse> {

    @Override
    public QuestionResponse toDto(Question entity) {
        if (entity == null) {
            return null;
        }

        return QuestionResponse.builder()
                .id(entity.getId())
                .weekId(entity.getWeek() != null ? entity.getWeek().getId() : null)
                .front(entity.getFront())
                .back(entity.getBack())
                .build();
    }

    @Override
    public Question toEntity(QuestionResponse dto) {
        if (dto == null) {
            return null;
        }

        return Question.builder()
                .id(dto.getId())
                .front(dto.getFront())
                .back(dto.getBack())
                .build();
        // 주의: week 등 연관관계는 별도로 설정해야 함
    }
}