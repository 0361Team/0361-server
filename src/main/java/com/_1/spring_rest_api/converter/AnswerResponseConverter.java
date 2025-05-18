package com._1.spring_rest_api.converter;

import com._1.spring_rest_api.api.dto.AnswerResponse;
import com._1.spring_rest_api.api.dto.QuestionResponse;
import com._1.spring_rest_api.entity.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnswerResponseConverter {

    private final QuestionConverter questionConverter;

    /**
     * 사용자 답변 결과를 AnswerResponse DTO로 변환
     */
    public AnswerResponse toDto(
            boolean isCorrect,
            String correctAnswer,
            Question nextQuestion,
            int nextQuestionIndex,
            boolean isComplete) {

        // 다음 질문이 있으면 DTO로 변환
        QuestionResponse nextQuestionResponse = questionConverter.toDto(nextQuestion);

        return AnswerResponse.builder()
                .isCorrect(isCorrect)
                .correctAnswer(correctAnswer)
                .isComplete(isComplete)
                .nextQuestionIndex(nextQuestionIndex)
                .nextQuestion(nextQuestionResponse)
                .build();
    }
}