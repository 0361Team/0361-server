package com._1.spring_rest_api.converter;

import com._1.spring_rest_api.api.dto.QuestionDto;
import com._1.spring_rest_api.entity.Question;
import com._1.spring_rest_api.repository.WeekRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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

    /**
     * AI가 생성한 질문 DTO를 특정 주차의 Question 엔티티로 변환
     *
     * @param dto    질문 DTO
     * @param weekId 주차 ID
     * @return 주차에 연결된 Question 엔티티
     */
    public Question createQuestionForWeek(QuestionDto dto, Long weekId) {
        if (dto == null) {
            return null;
        }

        Question question = toEntity(dto);

        if (weekId != null) {
            weekRepository.findById(weekId).ifPresent(question::changeWeek);
        }

        return question;
    }
}