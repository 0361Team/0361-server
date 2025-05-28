package com._1.spring_rest_api.converter;

import com._1.spring_rest_api.api.dto.QuestionDto;
import com._1.spring_rest_api.entity.Question;
import com._1.spring_rest_api.entity.Week;
import com._1.spring_rest_api.repository.WeekRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 생성 질문 DTO와 Question 엔티티 간의 변환을 담당하는 컨버터
 */
@Component
@RequiredArgsConstructor
public class QuestionGenerationConverter implements Converter<Question, QuestionDto> {

    private final WeekRepository weekRepository;

    @Override
    public QuestionDto toDto(Question entity) {
        if (entity == null) {
            return null;
        }

        return QuestionDto.builder()
                .front(entity.getFront())
                .back(entity.getBack())
                .build();
    }

    @Override
    public Question toEntity(QuestionDto dto) {
        if (dto == null) {
            return null;
        }

        return Question.builder()
                .front(dto.getFront())
                .back(dto.getBack())
                .build();
    }

    public List<Question> createQuestionsForWeek(List<QuestionDto> dtos, Long weekId) {
        if (dtos == null || dtos.isEmpty()) {
            return new ArrayList<>();
        }

        return dtos.stream()
                .map(dto -> createQuestionForWeek(dto, weekId))
                .collect(Collectors.toList());
    }

    public Question createQuestionForWeek(QuestionDto dto, Long weekId) {
        if (dto == null) {
            return null;
        }

        Question question = toEntity(dto);

        if (weekId != null) {
            Week week = weekRepository.findById(weekId)
                    .orElseThrow(() -> new EntityNotFoundException("Week not found with id: " + weekId));

            week.addQuestion(question);
        }

        return question;
    }


}